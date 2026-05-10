
package Agent;

//import Main.AboutUs;
import Main.Home;
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

public class UpdateFenceAgent extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private Connection con;
    private JTextArea alertFeed;
    private JTextField searchField;


    // Map to store GPS position as Key, and [PlaceName, Status] as Value
    private final Map<GeoPosition, String[]> activeFences = new HashMap<>();
    private GeoPosition selectedFence = null;

    public UpdateFenceAgent() {

        // 🔥 1. CONNECT DATABASE FIRST
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/oop_project",
                    "root",
                    ""
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Connection Failed!");
            return; // stop if no DB
        }

        // 🔥 2. INIT UI
        initComponents();
        setupCustomComponents();
        initMapViewer();

        // 🔥 3. LOAD DATA
        loadExistingAlerts();

        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    private void setupCustomComponents() {
        // Search Field
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Search Village Name (Press Enter)"));
        jPanel1.add(searchField, new AbsoluteConstraints(1100, 180, 350, 50));
        searchField.addActionListener(e -> {
            String text = searchField.getText();
            searchFence(text);
        });


        // Alert Feed
        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setFont(new Font("Monospaced", Font.PLAIN, 12));
        alertFeed.setBackground(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(alertFeed);
        scroll.setBorder(BorderFactory.createTitledBorder("Active Fence Status"));
        jPanel1.add(scroll, new AbsoluteConstraints(1100, 260, 350, 500));
    }

    private void initMapViewer() {
        // 1. MUST INITIALIZE THE OBJECT FIRST
        mapViewer = new JXMapViewer();

        System.setProperty("http.agent", "GajaSafeApp/1.0");
        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafe", "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        // Start center: Sri Lanka
        mapViewer.setAddressLocation(new GeoPosition(7.8731, 80.7718));
        mapViewer.setZoom(10);

        // 2. NOW ADD LISTENERS (Object is no longer null)
        // Zoom using mouse wheel
        mapViewer.addMouseWheelListener(
                new org.jxmapviewer.input.ZoomMouseWheelListenerCursor(mapViewer)
        );

        // Drag / move map with mouse
        org.jxmapviewer.input.PanMouseInputListener panListener =
                new org.jxmapviewer.input.PanMouseInputListener(mapViewer);

        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);

        // HOVER LOGIC: Show name when cursor is over a marker
        // Inside initMapViewer, add this listener AFTER mapViewer = new JXMapViewer();
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                GeoPosition mousePos = mapViewer.convertPointToGeoPosition(e.getPoint());
                boolean found = false;

                for (Map.Entry<GeoPosition, String[]> entry : activeFences.entrySet()) {
                    double latDiff = Math.abs(entry.getKey().getLatitude() - mousePos.getLatitude());
                    double lonDiff = Math.abs(entry.getKey().getLongitude() - mousePos.getLongitude());

                    if (latDiff < 0.002 && lonDiff < 0.002) {
                        // Shows the custom name you entered
                        mapViewer.setToolTipText("Fence: " + entry.getValue()[0] + " (" + entry.getValue()[1] + ")");
                        found = true;
                        break;
                    }
                }
                if (!found) mapViewer.setToolTipText(null);
            }

        });

        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Point2D pt = e.getPoint();
                Rectangle rect = mapViewer.getViewportBounds();
                Point2D actualPt = new Point2D.Double(pt.getX() + rect.getX(), pt.getY() + rect.getY());
                GeoPosition clickedPos = mapViewer.getTileFactory().pixelToGeo(actualPt, mapViewer.getZoom());

                // Trigger the Update Logic on Click
                GeoPosition clicked = mapViewer.convertPointToGeoPosition(e.getPoint());

                for (Map.Entry<GeoPosition, String[]> entry : activeFences.entrySet()) {

                    double latDiff = Math.abs(entry.getKey().getLatitude() - clicked.getLatitude());
                    double lonDiff = Math.abs(entry.getKey().getLongitude() - clicked.getLongitude());

                    if (latDiff < 0.003 && lonDiff < 0.003) {

                        selectedFence = entry.getKey();

                        String name = entry.getValue()[0];
                        String status = entry.getValue()[1];

                        showFenceOptions(name, status);
                        break;
                    }
                }
            }
        });

        // 3. ADD TO PANEL LAST
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

    private void refreshMapOverlay() {
        Painter<JXMapViewer> fencePainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();

            for (Map.Entry<GeoPosition, String[]> entry : activeFences.entrySet()) {

                Point2D pt = map.getTileFactory().geoToPixel(entry.getKey(), map.getZoom());

                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                String statusValue = entry.getValue()[1];
                Color color = getStatusColor(statusValue);

                int r = 20;

                // ================= SMALL FENCE AREA =================

                g.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        40
                ));

                g.fillOval(x - r, y - r, r * 2, r * 2);

                g.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        120
                ));

                g.setStroke(new BasicStroke(2));
                g.drawOval(x - r, y - r, r * 2, r * 2);

                // ================= GOOGLE MAP STYLE MARKER =================

                // Marker shadow
                g.setColor(new Color(0, 0, 0, 50));
                g.fillOval(x - 8, y + 2, 16, 8);

                // Pin body
                g.setColor(color);
                g.fillOval(x - 10, y - 28, 20, 20);

                // Pin tail
                Polygon pin = new Polygon();
                pin.addPoint(x, y);
                pin.addPoint(x - 6, y - 14);
                pin.addPoint(x + 6, y - 14);

                g.fillPolygon(pin);

                // Small center circle
                g.setColor(Color.white);
                g.fillOval(x - 4, y - 22, 8, 8);

                // Small border
                g.setColor(Color.white);
                g.drawOval(x - 4, y - 22, 8, 8);

                // Outer border
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1));
                g.drawOval(x - 10, y - 28, 20, 20);
            }

            g.dispose();
        };
        mapViewer.setOverlayPainter(fencePainter);
    }

    private void showFenceOptions(String name, String status) {

        String[] options = {"UPDATE STATUS", "DELETE", "CANCEL"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Fence: " + name + "\nStatus: " + status,
                "Manage Fence",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            updateFenceStatus();
        } else if (choice == 1) {
            deleteFence();
        }
    }

    private void updateFenceStatus() {

        if (selectedFence == null) return;

        String[] statuses = {"OK", "BROKEN", "ELECTRIC FAILURE", "DAMAGE"};

        String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Select new status:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                statuses[0]
        );

        if (newStatus != null) {

            try {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE fence SET status=? WHERE latitude=? AND longitude=?"
                );

                ps.setString(1, newStatus);
                ps.setDouble(2, selectedFence.getLatitude());
                ps.setDouble(3, selectedFence.getLongitude());

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }

            loadExistingAlerts(); // 🔥 refresh UI
            JOptionPane.showMessageDialog(this, "Status Updated!");
        }
    }

    private void deleteFence() {

        if (selectedFence == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this fence?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM fence WHERE latitude=? AND longitude=?"
                );

                ps.setDouble(1, selectedFence.getLatitude());
                ps.setDouble(2, selectedFence.getLongitude());

                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }

            loadExistingAlerts(); // 🔥 refresh
            JOptionPane.showMessageDialog(this, "Fence Deleted!");
        }
    }

   /* private void searchFence(String query) {
        if (query.isEmpty()) return;
        boolean found = false;
        for (Map.Entry<GeoPosition, String[]> entry : activeFences.entrySet()) {
            if (entry.getValue()[0].toLowerCase().contains(query.toLowerCase())) {
                mapViewer.setAddressLocation(entry.getKey());
                mapViewer.setZoom(5);
                JOptionPane.showMessageDialog(this, "FOUND: " + entry.getValue()[0] + "\nStatus: " + entry.getValue()[1]);
                found = true;
                break;
            }
        }
        if (!found) JOptionPane.showMessageDialog(this, "Village not found in active fences.");
    }*/
    private void searchFence(String query) {

        if (query == null || query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a village name!");
            return;
        }

        boolean found = false;

        for (Map.Entry<GeoPosition, String[]> entry : activeFences.entrySet()) {

            String place = entry.getValue()[0];

            if (place.toLowerCase().contains(query.toLowerCase())) {

                mapViewer.setAddressLocation(entry.getKey());
                mapViewer.setZoom(5);

                JOptionPane.showMessageDialog(this,
                        "FOUND:\n" + place + "\nStatus: " + entry.getValue()[1]);

                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Village not found.");
        }
    }


    private String getNearestVillage(double lat, double lon) {
        String village = "Unknown";
        try {
            PreparedStatement pst = con.prepareStatement("SELECT village FROM sl_locations " +
                    "ORDER BY (ABS(latitude - ?) + ABS(longitude - ?)) LIMIT 1");
            pst.setDouble(1, lat);
            pst.setDouble(2, lon);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) village = rs.getString("village");
        } catch (Exception e) { e.printStackTrace(); }
        return village;
    }

    private void loadExistingAlerts() {
        try {
            activeFences.clear();
            alertFeed.setText(""); // This "removes" deleted items from the side panel automatically

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM fence");
            while (rs.next()) {
                GeoPosition pos = new GeoPosition(rs.getDouble("latitude"), rs.getDouble("longitude"));
                String name = rs.getString("place_name");
                String status = rs.getString("status");

                activeFences.put(pos, new String[]{name, status});

                // Re-build the sidebar text
                alertFeed.append("📍 " + name + " -> STATUS: " + status + "\n");
            }
            refreshMapOverlay();
            mapViewer.repaint();
        } catch (Exception e) { e.printStackTrace(); }
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
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

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
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 30, -1, -1));

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
        jPanel2.add(logout_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 30, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5.setFont(new java.awt.Font("Script MT Bold", 1, 45)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Efind");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 790));

        pack();
    }// </editor-fold>

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Dashboard db = new Dashboard();
        db.setVisible(true);
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

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new UpdateFenceAgent().setVisible(true));
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

