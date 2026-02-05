
package Agent;

import Main.AboutUs;
import Main.Home;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import java.awt.*;
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

    public ReportElephantAgent() {
    initComponents();
    setupControls(); 
    initMapViewer(); 
    this.setLocationRelativeTo(null);
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
}


    private void initMapViewer() {
        mapViewer = new JXMapViewer();
        System.setProperty("http.agent", "GajaSafeApp/1.0");
        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafeApp", "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        GeoPosition sriLankaCenter = new GeoPosition(7.8731, 80.7718);
        mapViewer.setAddressLocation(sriLankaCenter);
        mapViewer.setZoom(10);
        
        mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addMouseMotionListener(new org.jxmapviewer.input.PanMouseInputListener(mapViewer));
        jPanel1.add(mapViewer, new AbsoluteConstraints(40, 120, 1030, 630));

    }
    
//    private void addRedZone(GeoPosition pos) {
//        String name = getNearestVillage(pos.getLatitude(), pos.getLongitude());
//        activeZones.add(pos);
//
//         Save to DB with the name
//        saveAlertToDB(pos.getLatitude(), pos.getLongitude(), name);
//        loadExistingAlerts(); 
//    }

    private void sendNotifications(GeoPosition pos) {
        // This simln("NOTIFY: Red Zone active at " + pos.getLatitude() + ", " + pos.getLongitude());
        // In a real appulates sending SMS/Push to users in this specific area
        System.out.println("NOTIFY: Red Zone active at " + pos.getLatitude() + ", " + pos.getLongitude());
        // In a real app, you would query registered users near these coordinates here
    }

    

    private void setupControls() {
        // 1. INITIALIZE the variable FIRST to avoid NullPointerException
        alertFeed = new JTextArea(); 
        alertFeed.setEditable(false);
        alertFeed.setBackground(new Color(245, 245, 245));
        alertFeed.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // 2. Setup Dropdowns
        comboProvince = new JComboBox<>();
        comboDistrict = new JComboBox<>(new String[]{"Select District"});
        comboVillage = new JComboBox<>(new String[]{"Select Village"});

        // 3. Add UI components to Panel
        jPanel1.add(new JLabel("PROVINCE:"), new AbsoluteConstraints(1120, 120, -1, -1));
        jPanel1.add(comboProvince, new AbsoluteConstraints(1120, 140, 350, 40));
        jPanel1.add(new JLabel("DISTRICT:"), new AbsoluteConstraints(820, 200, -1, -1));
        jPanel1.add(comboDistrict, new AbsoluteConstraints(1120, 220, 350, 40));
        jPanel1.add(new JLabel("VILLAGE:"), new AbsoluteConstraints(820, 280, -1, -1));
        jPanel1.add(comboVillage, new AbsoluteConstraints(1120, 300, 350, 40));

        JButton btnAlert = new JButton("ACTIVATE RED ZONE");
        btnAlert.setBackground(new Color(204, 0, 0));
        btnAlert.setForeground(Color.WHITE);
        btnAlert.setFont(new Font("Arial", Font.BOLD, 16));
        jPanel1.add(btnAlert, new AbsoluteConstraints(1120, 380, 350, 60));

        // 4. Setup the Scrollable Feed Display
        JLabel lblFeed = new JLabel("LIVE ALERT LOG (Date & Time):");
        lblFeed.setFont(new Font("Arial", Font.BOLD, 14));
        jPanel1.add(lblFeed, new AbsoluteConstraints(1120, 460, -1, -1));

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        jPanel1.add(scrollFeed, new AbsoluteConstraints(1120, 480, 350, 270));

    }
    
        
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        homebtn = new javax.swing.JButton();
        dashboard_btn = new javax.swing.JButton();
        aboutus_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        homebtn.setBackground(new java.awt.Color(0, 0, 0));
        homebtn.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        homebtn.setForeground(new java.awt.Color(255, 255, 255));
        homebtn.setText("HOME");
        homebtn.setBorder(null);
        homebtn.setContentAreaFilled(false);
        homebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homebtnActionPerformed(evt);
            }
        });
        jPanel2.add(homebtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

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

        jTextField2.setBackground(new java.awt.Color(0, 0, 0));
        jTextField2.setFont(new java.awt.Font("Script MT Bold", 1, 48)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jTextField2.setText("Efind");
        jTextField2.setBorder(null);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanel2.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1520, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homebtnActionPerformed
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_homebtnActionPerformed

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

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReportElephantAgent().setVisible(true));
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JButton homebtn;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton logout_btn;
    // End of variables declaration//GEN-END:variables
}
