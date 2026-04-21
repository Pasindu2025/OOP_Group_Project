
package user;

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

public class UpdateFenceUser extends javax.swing.JFrame {
        private JXMapViewer mapViewer;
        //private Connection con;
        private JTextArea alertFeed;
        private JTextField searchField;
        
         // Map to store GPS position as Key, and [PlaceName, Status] as Value
    private final Map<GeoPosition, String[]> activeFences = new HashMap<>();

    
    public UpdateFenceUser() {
        initComponents();
        //connectToDatabase();
        setupCustomComponents(); // Initialize Search and Feed
        initMapViewer();
        //loadExistingAlerts();
        this.setLocationRelativeTo(null);
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
    mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));
    mapViewer.addMouseMotionListener(new org.jxmapviewer.input.PanMouseInputListener(mapViewer));

    
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

    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        home_btn = new javax.swing.JButton();
        dashboard_btn = new javax.swing.JButton();
        aboutus_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        home_btn.setBackground(new java.awt.Color(0, 0, 0));
        home_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        home_btn.setForeground(new java.awt.Color(255, 255, 255));
        home_btn.setText("HOME");
        home_btn.setBorder(null);
        home_btn.setContentAreaFilled(false);
        home_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_btnActionPerformed(evt);
            }
        });
        jPanel2.add(home_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

        dashboard_btn.setBackground(new java.awt.Color(0, 0, 0));
        dashboard_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        dashboard_btn.setForeground(new java.awt.Color(255, 255, 255));
        dashboard_btn.setText("DASHBOARD");
        dashboard_btn.setBorder(null);
        dashboard_btn.setContentAreaFilled(false);
        dashboard_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboard_btnActionPerformed(evt);
            }
        });
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 30, -1, -1));

        aboutus_btn.setBackground(new java.awt.Color(0, 0, 0));
        aboutus_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        aboutus_btn.setForeground(new java.awt.Color(255, 255, 255));
        aboutus_btn.setText("ABOUT US");
        aboutus_btn.setBorder(null);
        aboutus_btn.setContentAreaFilled(false);
        aboutus_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutus_btnActionPerformed(evt);
            }
        });
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 30, -1, -1));

        logout_btn.setBackground(new java.awt.Color(0, 0, 0));
        logout_btn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        logout_btn.setForeground(new java.awt.Color(255, 255, 255));
        logout_btn.setText("LOGOUT");
        logout_btn.setBorder(null);
        logout_btn.setContentAreaFilled(false);
        logout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_btnActionPerformed(evt);
            }
        });
        jPanel2.add(logout_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 30, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png"))); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jTextField4.setBackground(new java.awt.Color(0, 0, 0));
        jTextField4.setFont(new java.awt.Font("Script MT Bold", 1, 48)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(255, 255, 255));
        jTextField4.setText("Efind");
        jTextField4.setBorder(null);
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });
        jPanel2.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void home_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_home_btnActionPerformed
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_home_btnActionPerformed

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboard_btnActionPerformed
        //        Home home = new Home();
        //        home.setVisible(true);
        //        this.dispose();
    }//GEN-LAST:event_dashboard_btnActionPerformed

    private void aboutus_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutus_btnActionPerformed
        AboutUs aboutus = new AboutUs();
        aboutus.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_aboutus_btnActionPerformed

    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_btnActionPerformed
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logout_btnActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    /* ================= MAIN ================= */
public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new UpdateFenceUser().setVisible(true));
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JButton home_btn;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JButton logout_btn;
    // End of variables declaration//GEN-END:variables
}

