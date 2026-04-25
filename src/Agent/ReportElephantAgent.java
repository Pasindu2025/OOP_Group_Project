
package Agent;

import Agent.Dashboard;
import Main.AboutUs;
import Main.Home;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import User.Main;

public class ReportElephantAgent extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private JComboBox<String> comboProvince, comboDistrict, comboVillage;
    //private Connection con;
    private Set<GeoPosition> activeZones = new HashSet<>();
    private JTextArea alertFeed;

    //private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReportElephantUser.class.getName());


    public ReportElephantAgent() {
        initComponents();
        setupControls();
        initMapViewer();
        this.setLocationRelativeTo(null);
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
        jPanel1.add(mapViewer, new AbsoluteConstraints(40, 120, 1050, 600));

    }


    private void sendNotifications(GeoPosition pos) {
        // This simulates sending SMS/Push to users in this specific area
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

        // 4. Setup the Scrollable Feed Display
        JLabel lblFeed = new JLabel("LIVE ALERT LOG (Date & Time):");
        lblFeed.setFont(new Font("Arial", Font.BOLD, 18));
        jPanel1.add(lblFeed, new AbsoluteConstraints(1150, 120, -1, -1));

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        jPanel1.add(scrollFeed, new AbsoluteConstraints(1150, 180, 350, 520));

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
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 30, -1, -1));

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

    /**
     * @param args the command line arguments
     */
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
