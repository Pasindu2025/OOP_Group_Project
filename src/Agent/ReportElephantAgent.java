
package Agent;

import Agent.Dashboard;
import Main.AboutUs;
import Main.Home;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import java.awt.*;
import java.awt.geom.Point2D;
import static java.lang.System.exit;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class ReportElephantAgent extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private JComboBox<String> comboProvince, comboDistrict, comboVillage;
    private Connection con;
    private Set<GeoPosition> activeZones = new HashSet<>();
    private JTextArea alertFeed;

    //private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReportElephantUser.class.getName());


    public ReportElephantAgent() {
        initComponents();
        //connectToDatabase();
        setupControls();
        initMapViewer();
        //loadProvinces();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

//    private void connectToDatabase() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3307/elephantdb", "root", "");
//        } catch (Exception e) {
//            System.err.println("Database Error: " + e.getMessage());
//        }
//    }

    private void addRedZone(GeoPosition pos) {
        activeZones.add(pos);

        // Show in alert feed (optional)
//    alertFeed.append("Red Zone Activated at:\n" +
//            pos.getLatitude() + ", " +
//            pos.getLongitude() + "\n----------------------\n");

        // Refresh map UI
        refreshMapOverlay();
    }

    private void initMapViewer() {

        mapViewer = new JXMapViewer();

        System.setProperty("http.agent", "GajaSafeApp/1.0");

        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafeApp",
                "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        GeoPosition sriLankaCenter = new GeoPosition(7.8731, 80.7718);
        mapViewer.setAddressLocation(sriLankaCenter);
        mapViewer.setZoom(10);

        // LEFT + RIGHT CLICK HANDLING
        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                Point2D pt = e.getPoint();
                Rectangle rect = mapViewer.getViewportBounds();
                Point2D actualPt = new Point2D.Double(
                        pt.getX() + rect.getX(),
                        pt.getY() + rect.getY()
                );

                GeoPosition clickedPos =
                        mapViewer.getTileFactory().pixelToGeo(actualPt, mapViewer.getZoom());

                // LEFT DOUBLE CLICK → ADD ZONE
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    int choice = JOptionPane.showConfirmDialog(
                            null,
                            "Activate Red Zone here?",
                            "Broadcast",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        addRedZone(clickedPos);
                    }
                }

                // RIGHT CLICK → REMOVE ZONE
                if (SwingUtilities.isRightMouseButton(e)) {

                    GeoPosition toRemove = null;

                    for (GeoPosition pos : activeZones) {
                        double dist = Math.sqrt(
                                Math.pow(pos.getLatitude() - clickedPos.getLatitude(), 2) +
                                        Math.pow(pos.getLongitude() - clickedPos.getLongitude(), 2)
                        );

                        if (dist < 0.05) {
                            toRemove = pos;
                            break;
                        }
                    }

                    if (toRemove != null) {
                        int confirm = JOptionPane.showConfirmDialog(
                                null,
                                "Remove this alert?",
                                "Safety Check",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            removeSpecificZone(toRemove);
                        }
                    }
                }
            }
        });

        mapViewer.addMouseWheelListener(
                new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer)
        );

        mapViewer.addMouseMotionListener(
                new org.jxmapviewer.input.PanMouseInputListener(mapViewer)
        );

        jPanel1.add(mapViewer, new AbsoluteConstraints(40, 120, 1030, 630));

        refreshMapOverlay();
    }

//    n pos) {
//        String name = getNearestVillage(pos.getLatitude(), pos.getLongitude());
//        activeZones.add(pos);
//
//        // Save to DB with the name
//        saveAlertToDB(pos.getLatitude(), pos.getLongitude(), name);
//        loadExistingAlerts();
//    }

//    private void sendNotifications(GeoPosition pos) {
//        // This simln("NOTIFY: Red Zone active at " + pos.getLatitude() + ", " + pos.getLongitude());
//        // In a real appulates sending SMS/Push to users in this specific area
//        System.out.println("NOTIFY: Red Zone active at " + pos.getLatitude() + ", " + pos.getLongitude());
//        // In a real app, you would query registered users near these coordinates here
//    }

//    private void loadExistingAlerts() {
//        try {
//            activeZones.clear();
//            alertFeed.setText("");
//
//            Statement stmt = con.createStatement();
//            // Fetch the place_name column
//            ResultSet rs = stmt.executeQuery("SELECT latitude, longitude, place_name, DATE_FORMAT(added_at, '%Y-%m-%d %H:%i:%s') as time_sent FROM active_alerts ORDER BY added_at DESC");
//
//            while (rs.next()) {
//                activeZones.add(new GeoPosition(rs.getDouble("latitude"), rs.getDouble("longitude")));
//
//                String time = rs.getString("time_sent");
//                String name = rs.getString("place_name");
//
//                // CLEAN LOG DISPLAY: Show Name and Time only
//                alertFeed.append("[" + time + "]\nVillage: " + name + "\n----------------------------\n");
//            }
//            refreshMapOverlay();
//        } catch (Exception e) { e.printStackTrace(); }
//    }

    private void clearAllZones() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear ALL active red zones?", "Clear Alerts", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                con.createStatement().executeUpdate("DELETE FROM active_alerts");
                activeZones.clear();
                refreshMapOverlay();
                JOptionPane.showMessageDialog(this, "All areas marked as SAFE.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    private void setupControls() {
        // 1. INITIALIZE the variable FIRST to avoid NullPointerException
        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setBackground(new Color(245, 245, 245));
        alertFeed.setFont(new Font("Monospaced", Font.PLAIN, 12));



        // 4. Setup the Scrollable Feed Display
        JLabel lblFeed = new JLabel("LIVE ALERT LOG (Date & Time):");
        lblFeed.setFont(new Font("Arial", Font.BOLD, 14));
        jPanel1.add(lblFeed, new AbsoluteConstraints(1120, 120, -1, -1));

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        jPanel1.add(scrollFeed, new AbsoluteConstraints(1120, 150, 350, 600));


    }

    private void refreshMapOverlay() {
        // 1. Create Waypoints for all active zones
        Set<Waypoint> waypoints = new HashSet<>();
        for (GeoPosition pos : activeZones) {
            waypoints.add(new DefaultWaypoint(pos));
        }
        WaypointPainter<Waypoint> wpPainter = new WaypointPainter<>();
        wpPainter.setWaypoints(waypoints);

        // 2. Multi-Circle Painter
        Painter<JXMapViewer> multiDangerPainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();

            for (GeoPosition pos : activeZones) {
                Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                g.setColor(new Color(255, 0, 0, 100)); // Transparent red
                g.fillOval(x - 40, y - 40, 80, 80);
                g.setColor(Color.RED);
                g.drawOval(x - 40, y - 40, 80, 80);
            }
            g.dispose();
        };

        CompoundPainter<JXMapViewer> compound = new CompoundPainter<>(multiDangerPainter, wpPainter);
        mapViewer.setOverlayPainter(compound);
    }

//    private String getNearestVillage(double lat, double lon) {
//        String villageName = "Unknown Location";
//        try {
//            // Find the village with the smallest distance to the clicked point
//            String query = "SELECT village, " +
//                           "(ABS(latitude - ?) + ABS(longitude - ?)) AS distance " +
//                           "FROM sl_locations ORDER BY distance LIMIT 1";
//
//            PreparedStatement pst = con.prepareStatement(query);
//            pst.setDouble(1, lat);
//            pst.setDouble(2, lon);
//            ResultSet rs = pst.executeQuery();
//
//            if (rs.next()) {
//                villageName = rs.getString("village");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return villageName;
//    }


//    private void saveAlertToDB(double lat, double lon, String name) {
//        try {
//            String query = "INSERT INTO active_alerts (latitude, longitude, place_name) VALUES (?, ?, ?)";
//            PreparedStatement pst = con.prepareStatement(query);
//            pst.setDouble(1, lat);
//            pst.setDouble(2, lon);
//            pst.setString(3, name);
//            pst.executeUpdate();
//        } catch (Exception e) { e.printStackTrace(); }
//    }

//    private void loadProvinces() {
//        try {
//            ResultSet rs = con.createStatement().executeQuery("SELECT DISTINCT province FROM sl_locations");
//            comboProvince.addItem("Select Province");
//            while (rs.next()) comboProvince.addItem(rs.getString(1));
//        } catch (Exception e) { e.printStackTrace(); }
//    }

//    private void updateDropdown(String type, String filter, JComboBox target) {
//        if (filter == null || filter.startsWith("Select")) return;
//        try {
//            String query = type.equals("district") ?
//                "SELECT DISTINCT district FROM sl_locations WHERE province = ?" :
//                "SELECT village FROM sl_locations WHERE district = ?";
//            PreparedStatement pst = con.prepareStatement(query);
//            pst.setString(1, filter);
//            ResultSet rs = pst.executeQuery();
//            target.removeAllItems();
//            target.addItem("Select " + type.substring(0, 1).toUpperCase() + type.substring(1));
//            while (rs.next()) target.addItem(rs.getString(1));
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//

    private void markDangerZone() {
        String village = (String) comboVillage.getSelectedItem();
        if (village == null || village.startsWith("Select")) {
            JOptionPane.showMessageDialog(this, "Please select a village first!");
            return;
        }
        try {
            PreparedStatement pst = con.prepareStatement("SELECT latitude, longitude FROM sl_locations WHERE village = ?");
            pst.setString(1, village);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                GeoPosition pos = new GeoPosition(rs.getDouble("latitude"), rs.getDouble("longitude"));
                addRedZone(pos); // This adds to map, saves to DB, and updates the text feed
                mapViewer.setAddressLocation(pos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSpecificZone(GeoPosition pos) {
        activeZones.remove(pos);

//    alertFeed.append("Zone Removed:\n" +
//            pos.getLatitude() + ", " +
//            pos.getLongitude() + "\n----------------------\n");

        refreshMapOverlay();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dashboard_btn = new javax.swing.JButton();
        aboutus_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5.setFont(new java.awt.Font("Script MT Bold", 1, 45)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Efind");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

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
        aboutus_btn.setText("ACTIVE ELEPHANTS");
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

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1520, 790));

        pack();
    }// </editor-fold>

    private void dashboard_btnMouseEntered(java.awt.event.MouseEvent evt) {
        dashboard_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void dashboard_btnMouseExited(java.awt.event.MouseEvent evt) {
        dashboard_btn.setForeground(java.awt.Color.WHITE);
    }

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {
//                Dashboard db = new Dashboard();
//                db.setVisible(true);
//                this.dispose();
    }

    private void aboutus_btnMouseEntered(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void aboutus_btnMouseExited(java.awt.event.MouseEvent evt) {
        aboutus_btn.setForeground(java.awt.Color.WHITE);
    }

    private void aboutus_btnActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void logout_btnMouseEntered(java.awt.event.MouseEvent evt) {
        logout_btn.setForeground(new java.awt.Color(255,153,0));
    }

    private void logout_btnMouseExited(java.awt.event.MouseEvent evt) {
        logout_btn.setForeground(java.awt.Color.WHITE);
    }

    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }



    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReportElephantAgent().setVisible(true));
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
