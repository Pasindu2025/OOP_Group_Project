package WildLife;

import Main.AboutUs;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import java.awt.*;
import java.awt.geom.Point2D;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import Main.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UpdateFenceWL extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private JTextArea alertFeed;
    private JTextField searchField;


    // Map to store GPS position as Key, and [PlaceName, Status] as Value
    private final Map<GeoPosition, Object[]> activeFences = new HashMap<>();
    private GeoPosition searchedLocation = null;
    private Connection con;

    public UpdateFenceWL() {
        initComponents();
        setupCustomComponents(); // Initialize Search and Feed
        initMapViewer();
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/oop_project",
                    "root",
                    ""
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadFencesFromAPI();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    private void setupCustomComponents() {
        // Search Field
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Search Village Name (Press Enter)"));
        jPanel1.add(searchField, new AbsoluteConstraints(1100, 180, 350, 50));

        // Alert Feed
        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setFont(new Font("Monospaced", Font.PLAIN, 12));
        alertFeed.setBackground(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(alertFeed);
        scroll.setBorder(BorderFactory.createTitledBorder("Active Fence Status"));
        jPanel1.add(scroll, new AbsoluteConstraints(1100, 260, 350, 500));

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();

            if (query.isEmpty()) return;

            // 1️⃣ check fences
            if (!searchFenceReturn(query)) {

                // 2️⃣ check DB
                if (!searchFromDatabaseReturn(query)) {

                    JOptionPane.showMessageDialog(this, "Location not found!");
                }
            }
        });
    }

    private void initMapViewer() {

        mapViewer = new JXMapViewer();

        System.setProperty("http.agent", "GajaSafeApp/1.0");
        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafe", "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        mapViewer.setAddressLocation(new GeoPosition(7.8731, 80.7718));
        mapViewer.setZoom(10);

        mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addMouseMotionListener(new org.jxmapviewer.input.PanMouseInputListener(mapViewer));

        // HOVER
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                GeoPosition mousePos = mapViewer.convertPointToGeoPosition(e.getPoint());
                boolean found = false;

                for (Map.Entry<GeoPosition, Object[]> entry : activeFences.entrySet()) {

                    Object[] data = entry.getValue();
                    String name = (String) data[0];
                    String status = (String) data[1];

                    double latDiff = Math.abs(entry.getKey().getLatitude() - mousePos.getLatitude());
                    double lonDiff = Math.abs(entry.getKey().getLongitude() - mousePos.getLongitude());

                    if (latDiff < 0.002 && lonDiff < 0.002) {
                        mapViewer.setToolTipText("Fence: " + name + " (" + status + ")");
                        found = true;
                        break;
                    }
                }
                if (!found) mapViewer.setToolTipText(null);
            }
        });

        // CLICK
        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                // 🔴 CLEAR SEARCH HIGHLIGHT
                searchedLocation = null;
                refreshMapOverlay();
                mapViewer.repaint();

                Point2D pt = e.getPoint();
                Rectangle rect = mapViewer.getViewportBounds();
                Point2D actualPt = new Point2D.Double(pt.getX() + rect.getX(), pt.getY() + rect.getY());
                GeoPosition clickedPos = mapViewer.getTileFactory().pixelToGeo(actualPt, mapViewer.getZoom());

                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    addNewFence(clickedPos);
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    removeFenceAt(clickedPos);
                }
            }
        });

        jPanel1.add(mapViewer, new AbsoluteConstraints(80, 180, 950, 580));
    }



    private Color getStatusColor(String status) {
        if (status == null) return new Color(0, 200, 50); // Default Green

        switch (status.trim().toUpperCase()) {
            case "BROKEN": return Color.RED;
            case "ELECTRIC FAILURE": return Color.ORANGE;
            case "DAMAGE": return Color.BLACK;
            case "OTHER": return Color.BLUE;
            default: return new Color(0, 200, 50); // Safe Green
        }
    }

    private void addNewFence(GeoPosition pos) {
        String village = getNearestVillage(pos.getLatitude(), pos.getLongitude());

        JTextField customNameField = new JTextField(village);

        Object[] message = {
                "Detected Location:", new JLabel(village),
                "Fence Name:", customNameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Fence", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String finalName = customNameField.getText().trim();

            // duplicate check
            for (Object[] data : activeFences.values()) {

                String name = (String) data[0];

                if (name.equalsIgnoreCase(finalName)) {
                    JOptionPane.showMessageDialog(this, "Name already exists!");
                    return;
                }
            }

            if (!finalName.isEmpty()) {
                try {
                    URL url = new URL("http://localhost:8081/api/fences");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // 🔥 DEFAULT STATUS
                    String jsonInput = String.format(
                            "{\"latitude\": %f, \"longitude\": %f, \"placeName\": \"%s\", \"status\": \"OK\"}",
                            pos.getLatitude(),
                            pos.getLongitude(),
                            finalName
                    );

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonInput.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println("ADD RESPONSE: " + responseCode);

                    loadFencesFromAPI();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshMapOverlay() {
        Painter<JXMapViewer> fencePainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {

            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();

            // 🔵 DRAW ALL FENCES (UPDATED 🔥)
            for (Map.Entry<GeoPosition, Object[]> entry : activeFences.entrySet()) {

                Object[] data = entry.getValue();

                String status = (String) data[1];
                int radius = (int) data[2];

                Color color = getStatusColor(status);

                Point2D pt = map.getTileFactory().geoToPixel(entry.getKey(), map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                // 🔥 SCALE (adjust if needed)
                int r = radius / 25;

                // 🔴 FILLED AREA (WATAK)
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                g.fillOval(x - r, y - r, r * 2, r * 2);

                // 🔴 BORDER
                g.setColor(color);
                g.setStroke(new BasicStroke(2));
                g.drawOval(x - r, y - r, r * 2, r * 2);
            }

            // 🔴 SEARCH HIGHLIGHT (KEEP SAME)
            if (searchedLocation != null) {
                Point2D pt = map.getTileFactory().geoToPixel(searchedLocation, map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                g.setColor(new Color(255, 0, 0, 120));
                g.fillOval(x - 20, y - 20, 40, 40);

                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(3));
                g.drawOval(x - 20, y - 20, 40, 40);
            }

            g.dispose();
        };

        mapViewer.setOverlayPainter(fencePainter);
    }

    private boolean searchFenceReturn(String query) {
        for (Map.Entry<GeoPosition, Object[]> entry : activeFences.entrySet()) {

            if (((String) entry.getValue()[0]).toLowerCase().contains(query.toLowerCase())) {

                searchedLocation = entry.getKey();
                mapViewer.setAddressLocation(searchedLocation);
                mapViewer.setZoom(5);

                refreshMapOverlay();
                return true;
            }
        }
        return false;
    }

    private boolean searchFromDatabaseReturn(String place) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT latitude, longitude FROM fence_reference_locations WHERE name LIKE ? LIMIT 1"
            );

            ps.setString(1, "%" + place + "%");

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");

                GeoPosition pos = new GeoPosition(lat, lon);

                mapViewer.setAddressLocation(pos);
                mapViewer.setZoom(5);

                JOptionPane.showMessageDialog(this, "Found: " + place);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void loadFencesFromAPI() {
        try {
            URL url = new URL("http://localhost:8081/api/fences");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            String json = response.toString();

            // clear old
            activeFences.clear();
            alertFeed.setText("");

            // 👉 SPLIT CORRECTLY
            String[] objects = json.split("\\},\\{");

            for (String obj : objects) {

                obj = obj.replace("[", "")
                        .replace("]", "")
                        .replace("{", "")
                        .replace("}", "");

                String[] parts = obj.split(",");

                double lat = 0, lon = 0;
                String name = "", status = "";

                for (String p : parts) {
                    String[] kv = p.split(":");

                    if (kv.length < 2) continue;

                    String key = kv[0].replace("\"", "").trim();
                    String value = kv[1].replace("\"", "").trim();

                    switch (key) {
                        case "latitude": lat = Double.parseDouble(value); break;
                        case "longitude": lon = Double.parseDouble(value); break;
                        case "placeName": name = value; break;
                        case "status": status = value; break;
                    }
                }

                GeoPosition pos = new GeoPosition(lat, lon);
                int radius = 500; //meters

                activeFences.put(pos, new Object[]{name, status, radius});
                alertFeed.append("📍 " + name + " -> STATUS: " + status + "\n");
            }

            refreshMapOverlay();
            mapViewer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNearestVillage(double lat, double lon) {
        return "Custom Location";
    }

    private void removeFenceAt(GeoPosition clickedPos) {
        GeoPosition toRemove = null;

        for (GeoPosition pos : activeFences.keySet()) {
            if (Math.abs(pos.getLatitude() - clickedPos.getLatitude()) < 0.005 &&
                    Math.abs(pos.getLongitude() - clickedPos.getLongitude()) < 0.005) {
                toRemove = pos;
                break;
            }
        }

        if (toRemove != null) {
            if (JOptionPane.showConfirmDialog(this, "Remove fence?", "Delete",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                try {
                    String fullUrl = "http://localhost:8081/api/fences?latitude="
                            + toRemove.getLatitude()
                            + "&longitude="
                            + toRemove.getLongitude();

                    System.out.println("DELETE URL: " + fullUrl);

                    URL url = new URL(fullUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");

                    int responseCode = conn.getResponseCode();
                    System.out.println("DELETE RESPONSE: " + responseCode);

                    loadFencesFromAPI();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "DANGER: This will delete ALL fences from the map and database. Proceed?",
                "Clear All Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                URL url = new URL("http://localhost:8081/api/fences");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

                int responseCode = conn.getResponseCode();
                System.out.println("CLEAR ALL RESPONSE: " + responseCode);

                // Reload map data
                loadFencesFromAPI();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        dashboard_btn = new javax.swing.JButton();
        aboutus_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboard_btn.setBackground(new java.awt.Color(0, 0, 0));
        dashboard_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        dashboard_btn.setForeground(new java.awt.Color(255, 255, 255));
        dashboard_btn.setText("DASHBOARD");
        dashboard_btn.setBorder(null);
        dashboard_btn.setContentAreaFilled(false);
        dashboard_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboard_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboard_btnMouseExited(evt);
            }
        });
        dashboard_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboard_btnActionPerformed(evt);
            }
        });
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 30, -1, -1));

        aboutus_btn.setBackground(new java.awt.Color(0, 0, 0));
        aboutus_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        aboutus_btn.setForeground(new java.awt.Color(255, 255, 255));
        aboutus_btn.setText("ELECTRIC FENCES");
        aboutus_btn.setBorder(null);
        aboutus_btn.setContentAreaFilled(false);
        aboutus_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aboutus_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aboutus_btnMouseExited(evt);
            }
        });
        aboutus_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutus_btnActionPerformed(evt);
            }
        });
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 30, -1, -1));

        logout_btn.setBackground(new java.awt.Color(0, 0, 0));
        logout_btn.setFont(new java.awt.Font("Poppins", 1, 16)); // NOI18N
        logout_btn.setForeground(new java.awt.Color(255, 255, 255));
        logout_btn.setText("LOGOUT");
        logout_btn.setBorder(null);
        logout_btn.setContentAreaFilled(false);
        logout_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logout_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logout_btnMouseExited(evt);
            }
        });
        logout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_btnActionPerformed(evt);
            }
        });
        jPanel2.add(logout_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1405, 30, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5.setFont(new java.awt.Font("Script MT Bold", 1, 45)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Efind");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 1550, 790));

        pack();
    }// </editor-fold>

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {
        MainDashboard wd = new MainDashboard();
        wd.setVisible(true);
        this.dispose();
    }

    private void aboutus_btnActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }

    private void dashboard_btnMouseEntered(java.awt.event.MouseEvent evt) {
        dashboard_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void dashboard_btnMouseExited(java.awt.event.MouseEvent evt) {
        dashboard_btn.setForeground(java.awt.Color.WHITE);
    }

    private void aboutus_btnMouseEntered(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void aboutus_btnMouseExited(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(java.awt.Color.WHITE);
    }

    private void logout_btnMouseEntered(java.awt.event.MouseEvent evt) {
        logout_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void logout_btnMouseExited(java.awt.event.MouseEvent evt) {
        logout_btn.setForeground(java.awt.Color.WHITE);
    }

    /* ================= ADD FENCE ================= */

    /* ================= MAIN ================= */

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new UpdateFenceWL().setVisible(true));
    }

    // Variables declaration - do not modify
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton logout_btn;
    // End of variables declaration

}
