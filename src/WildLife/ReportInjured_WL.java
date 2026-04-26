
package WildLife;

import Main.AboutUs;
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
import Main.MainDashboard;

public class ReportInjured_WL extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private Connection con;
    private JTextArea alertFeed;
    private JTextArea statsArea;

    // Map to store GPS position as Key, and [Description, Severity] as Value
    private final Map<GeoPosition, String[]> injuredElephants = new HashMap<>();

    public ReportInjured_WL() {
        initComponents();
        setupProfessionalUI();
        initMapViewer();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }


    private void setupProfessionalUI() {
        // 1. Sidebar Feed (List of incidents)
        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        alertFeed.setBackground(new Color(245, 245, 245));

        Font largerTitleFont = new Font("Arial", Font.BOLD, 14);

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        scrollFeed.setBorder(BorderFactory.createTitledBorder(
                null, "Active Incident Feed", 0, 0, largerTitleFont, new Color(50, 50, 50)));
        jPanel1.add(scrollFeed, new AbsoluteConstraints(1180, 150, 300, 400));

        // 2. Stats Dashboard (Real-time counts)
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsArea.setBackground(new Color(230, 230, 250));

        JScrollPane scrollStats = new JScrollPane(statsArea);
        scrollStats.setBorder(BorderFactory.createTitledBorder(
                null, "Live Statistics", 0, 0, largerTitleFont, new Color(50, 50, 50)));
        jPanel1.add(scrollStats, new AbsoluteConstraints(1180, 560, 300, 190));
    }

    private void initMapViewer() {
        mapViewer = new JXMapViewer();
        System.setProperty("http.agent", "GajaSafeApp/1.0");
        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafe", "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setAddressLocation(new GeoPosition(7.8731, 80.7718)); // Sri Lanka
        mapViewer.setZoom(10);

        // Movement Listeners
        mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addMouseMotionListener(new org.jxmapviewer.input.PanMouseInputListener(mapViewer));

        // HOVER LOGIC: Show info on mouse move
        mapViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                GeoPosition mousePos = mapViewer.convertPointToGeoPosition(e.getPoint());
                boolean found = false;
                for (Map.Entry<GeoPosition, String[]> entry : injuredElephants.entrySet()) {
                    if (isNear(entry.getKey(), mousePos, 0.003)) {
                        mapViewer.setToolTipText("<html><b>INCIDENT:</b> " + entry.getValue()[0] +
                                "<br><b>SEVERITY:</b> " + entry.getValue()[1] + "</html>");
                        found = true; break;
                    }
                }
                if (!found) mapViewer.setToolTipText(null);
            }
        });

        // CLICK LOGIC: Double-Click Add / Right-Click Remove
        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                GeoPosition pos = mapViewer.convertPointToGeoPosition(e.getPoint());
                removeReportAt(pos);
            }

        });

        jPanel1.add(mapViewer, new AbsoluteConstraints(50, 150, 1100, 600));
    }

    private void removeReportAt(GeoPosition clickedPos) {
        GeoPosition toRemove = null;

        for (GeoPosition pos : injuredElephants.keySet()) {
            if (isNear(pos, clickedPos, 0.005)) {
                toRemove = pos;
                break;
            }
        }

        if (toRemove != null) {
            if (JOptionPane.showConfirmDialog(this, "Delete this report?", "Remove", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                injuredElephants.remove(toRemove);

                // rebuild sidebar
                alertFeed.setText("");
                for (String[] data : injuredElephants.values()) {
                    alertFeed.append("• [" + data[1] + "] " + data[0] + "\n");
                }

                updateStats();
                refreshMapOverlay();
                mapViewer.repaint();
            }
        }
    }

    private void loadInjuredData() {
        try {
            injuredElephants.clear();
            alertFeed.setText("");
            int crit = 0, gun = 0, limp = 0, dead = 0;

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM injured_elephants");
            while (rs.next()) {
                GeoPosition pos = new GeoPosition(rs.getDouble("latitude"), rs.getDouble("longitude"));
                String desc = rs.getString("description");
                String sev = rs.getString("severity");
                injuredElephants.put(pos, new String[]{desc, sev});

                alertFeed.append("• [" + sev + "] " + desc + "\n");
                if (sev.equals("Critical")) crit++;
                else if (sev.equals("Gunshot Wound")) gun++;
                else if (sev.equals("Limping")) limp++;
                else if (sev.equals("Deceased")) dead++;
            }

            statsArea.setText("--- STATUS COUNTS ---\n🚨 Critical: " + crit + "\n🔫 Gunshot: " + gun +
                    "\n🐘 Limping: " + limp + "\n💀 Deceased: " + dead + "\n---------------------");
            refreshMapOverlay();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshMapOverlay() {
        Painter<JXMapViewer> painter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            for (Map.Entry<GeoPosition, String[]> entry : injuredElephants.entrySet()) {
                Point2D pt = map.getTileFactory().geoToPixel(entry.getKey(), map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                String sev = entry.getValue()[1];
                if (sev.equals("Critical")) g.setColor(Color.YELLOW);
                else if (sev.equals("Deceased")) g.setColor(Color.BLACK);
                else if (sev.equals("Gunshot Wound")) g.setColor(Color.RED);
                else if (sev.equals("Limping")) g.setColor(Color.ORANGE);
                else if (sev.equals("Minor")) g.setColor(Color.GREEN);
                else g.setColor(Color.YELLOW);

                g.fillOval(x - 12, y - 12, 24, 24);
                g.setColor(Color.WHITE);
                g.drawOval(x - 12, y - 12, 24, 24);
            }
            g.dispose();
        };
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private boolean isNear(GeoPosition p1, GeoPosition p2, double tol) {
        return Math.abs(p1.getLatitude() - p2.getLatitude()) < tol && Math.abs(p1.getLongitude() - p2.getLongitude()) < tol;
    }

    private String getNearestVillage(double lat, double lon) {
        try {
            PreparedStatement pst = con.prepareStatement("SELECT village FROM sl_locations ORDER BY (ABS(latitude - ?) + ABS(longitude - ?)) LIMIT 1");
            pst.setDouble(1, lat); pst.setDouble(2, lon);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("village");
        } catch (Exception e) { e.printStackTrace(); }
        return "Unknown Location";
    }

    private void updateStats() {
        int crit = 0, gun = 0, limp = 0, dead = 0;

        for (String[] data : injuredElephants.values()) {
            String sev = data[1];

            if (sev.equals("Critical")) crit++;
            else if (sev.equals("Gunshot Wound")) gun++;
            else if (sev.equals("Limping")) limp++;
            else if (sev.equals("Deceased")) dead++;
        }

        statsArea.setText(
                "--- STATUS COUNTS ---\n" +
                        "🚨 Critical: " + crit +
                        "\n🔫 Gunshot: " + gun +
                        "\n🐘 Limping: " + limp +
                        "\n💀 Deceased: " + dead +
                        "\n---------------------"
        );
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
        aboutus_btn.setText("INJURED ELEPHANTS");
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
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 30, -1, -1));

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

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReportInjured_WL().setVisible(true));
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
