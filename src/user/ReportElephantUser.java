package User;

import Main.Home;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

public class ReportElephantUser extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private JComboBox<String> comboProvince, comboDistrict, comboVillage;
    private JTextArea alertFeed;

    private Set<GeoPosition> activePoints = new HashSet<>(); // Elephant Spots
    private List<List<GeoPosition>> savedAreas = new ArrayList<>(); // Danger Zones
    private Timer liveUpdateTimer;

    // 📍 1. Type එකත් එක්කම ස්ථානය සේව් කරන අලුත් Structure එක
    class SavedPlace {
        String name;
        GeoPosition position;
        String type; // "Home", "School", "Farm", "Other"

        public SavedPlace(String name, GeoPosition position, String type) {
            this.name = name;
            this.position = position;
            this.type = type;
        }
    }

    private List<SavedPlace> userSavedPlaces = new ArrayList<>();

    public ReportElephantUser() {
        initComponents();
        setupControls();
        initMapViewer();

        fetchLiveAlertsFromServer();
        startLiveMapUpdates();

        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initMapViewer() {
        mapViewer = new JXMapViewer();
        System.setProperty("http.agent", "GajaSafeApp/1.0");
        TileFactoryInfo info = new OSMTileFactoryInfo("GajaSafeApp", "https://tile.openstreetmap.org");
        mapViewer.setTileFactory(new DefaultTileFactory(info));

        mapViewer.setAddressLocation(new GeoPosition(7.8731, 80.7718));
        mapViewer.setZoom(9);

        mapViewer.addMouseWheelListener(new org.jxmapviewer.input.ZoomMouseWheelListenerCenter(mapViewer));

        org.jxmapviewer.input.PanMouseInputListener panListener = new org.jxmapviewer.input.PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);

        // 🎯 DOUBLE CLICK LOGIC WITH POP-UP OPTIONS
        // 🎯 MOUSE CLICK LOGIC: Double Click එකෙන් සේව් කිරීම සහ Right-Click එකෙන් ඩිලීට් කිරීම
        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                GeoPosition clickedPos = mapViewer.convertPointToGeoPosition(e.getPoint());
                Point2D clickPixel = e.getPoint();

                // 🟢 1. LEFT DOUBLE CLICK -> අලුත් තැනක් සේව් කිරීම
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    String placeName = JOptionPane.showInputDialog(null,
                            "Enter a name for this Location:\n(e.g., My Sweet Home, Village School)",
                            "1. Place Name",
                            JOptionPane.QUESTION_MESSAGE);

                    if (placeName == null || placeName.trim().isEmpty()) return;

                    String[] options = {"Home", "School", "Farm", "Other"};
                    String selectedType = (String) JOptionPane.showInputDialog(null,
                            "Select the type of this location to set Icon:",
                            "2. Select Location Type",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (selectedType == null) selectedType = "Other";

                    userSavedPlaces.add(new SavedPlace(placeName.trim(), clickedPos, selectedType));
                    JOptionPane.showMessageDialog(null, "'" + placeName + "' added successfully!");

                    checkProximityAndAlert();
                    refreshUserMapOverlay();
                }

                // 🔴 2. RIGHT CLICK -> මාර්ක් කරපු ස්ථානයක් ඩිලීට් කිරීම
                else if (SwingUtilities.isRightMouseButton(e)) {
                    SavedPlace placeToDelete = null;

                    // Click කරපු තැනට ළඟින්ම තියෙන සේව් කරපු ප්ලේස් එක හොයනවා
                    for (SavedPlace place : userSavedPlaces) {
                        Point2D pt = mapViewer.getTileFactory().geoToPixel(place.position, mapViewer.getZoom());
                        Rectangle rect = mapViewer.getViewportBounds();
                        int px = (int) pt.getX() - rect.x;
                        int py = (int) pt.getY() - rect.y;

                        // Pixel 15ක් ඇතුළත Click කරලා තියෙනවා නම් (Icon එක උඩම වැදිලා නම්)
                        if (Point2D.distance(clickPixel.getX(), clickPixel.getY(), px, py) < 15) {
                            placeToDelete = place;
                            break;
                        }
                    }

                    // ස්ථානයක් හමු වුණොත් ඩිලීට් කරන්නද අහනවා
                    if (placeToDelete != null) {
                        int confirm = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to delete '" + placeToDelete.name + "' from your Watch-List?",
                                "Delete Saved Place",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            userSavedPlaces.remove(placeToDelete); // ලිස්ට් එකෙන් අයින් කරනවා
                            JOptionPane.showMessageDialog(null, "Location Removed.");

                            fetchLiveAlertsFromServer(); // Sidebar එකයි සිතියමයි Refresh කරනවා
                        }
                    }
                }
            }
        });

        jPanel1.add(mapViewer, new AbsoluteConstraints(40, 120, 1050, 600));
    }

    private void startLiveMapUpdates() {
        liveUpdateTimer = new Timer(5000, e -> {
            fetchLiveAlertsFromServer();
            checkProximityAndAlert();
        });
        liveUpdateTimer.start();
    }

    private void fetchLiveAlertsFromServer() {
        try {
            activePoints.clear();
            savedAreas.clear();
            alertFeed.setText("--- LIVE SAFE MONITOR ---\n");
            alertFeed.append("⭐ Monitored Places: " + userSavedPlaces.size() + "\n\n");

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            // Elephant Spots
            java.net.URL urlPoints = new java.net.URL("http://localhost:8081/api/active-alerts/all");
            java.net.HttpURLConnection connPoints = (java.net.HttpURLConnection) urlPoints.openConnection();
            connPoints.setRequestMethod("GET");

            if (connPoints.getResponseCode() == 200) {
                com.fasterxml.jackson.databind.JsonNode rootPoints = mapper.readTree(connPoints.getInputStream());
                for (com.fasterxml.jackson.databind.JsonNode node : rootPoints) {
                    activePoints.add(new GeoPosition(node.get("latitude").asDouble(), node.get("longitude").asDouble()));
                }
            }

            // Danger Zones
            java.net.URL urlAreas = new java.net.URL("http://localhost:8081/api/danger-areas/all");
            java.net.HttpURLConnection connAreas = (java.net.HttpURLConnection) urlAreas.openConnection();
            connAreas.setRequestMethod("GET");

            if (connAreas.getResponseCode() == 200) {
                com.fasterxml.jackson.databind.JsonNode rootAreas = mapper.readTree(connAreas.getInputStream());
                for (com.fasterxml.jackson.databind.JsonNode node : rootAreas) {
                    if (node.has("coordinates")) {
                        String coordsString = node.get("coordinates").asText();
                        com.fasterxml.jackson.databind.JsonNode pointsArray = mapper.readTree(coordsString);
                        List<GeoPosition> areaPoints = new ArrayList<>();
                        for (com.fasterxml.jackson.databind.JsonNode ptNode : pointsArray) {
                            areaPoints.add(new GeoPosition(ptNode.get("lat").asDouble(), ptNode.get("lon").asDouble()));
                        }
                        savedAreas.add(areaPoints);
                    }
                }
            }

            refreshUserMapOverlay();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkProximityAndAlert() {
        double warningDistanceKM = 3.0;

        for (SavedPlace myPlace : userSavedPlaces) {
            for (GeoPosition elephantPos : activePoints) {
                double distance = calculateDistance(
                        myPlace.position.getLatitude(), myPlace.position.getLongitude(),
                        elephantPos.getLatitude(), elephantPos.getLongitude()
                );

                if (distance <= warningDistanceKM) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this,
                            "🚨 SECURITY ALERT near " + myPlace.type.toUpperCase() + "! 🚨\n\n" +
                                    "An Elephant is just " + String.format("%.2f", distance) + " km away from '" + myPlace.name + "'!\n" +
                                    "Take immediate safety actions.",
                            "GAJA-SAFE: " + myPlace.type.toUpperCase() + " IN DANGER!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void refreshUserMapOverlay() {
        // 1. Elephant Spots Painter
        Painter<JXMapViewer> circlePainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            for (GeoPosition pos : activePoints) {
                Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;
                g.setColor(new Color(255, 0, 0, 100));
                g.fillOval(x - 12, y - 12, 24, 24);
                g.setColor(Color.RED);
                g.fillOval(x - 4, y - 4, 8, 8);
            }
            g.dispose();
        };

        // 2. Danger Areas Painter
        Painter<JXMapViewer> polygonPainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            for (List<GeoPosition> area : savedAreas) {
                Polygon p = new Polygon();
                for (GeoPosition pos : area) {
                    Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                    p.addPoint((int) pt.getX() - rect.x, (int) pt.getY() - rect.y);
                }
                g.setColor(new Color(255, 165, 0, 60));
                g.fillPolygon(p);
                g.setColor(Color.ORANGE);
                g.drawPolygon(p);
            }
            g.dispose();
        };

        // 3. 🎨 DIFFERENT CUSTOM SHAPES/ICONS PAINTER FOR PLACES
        Painter<JXMapViewer> customPlacesPainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();

            for (SavedPlace place : userSavedPlaces) {
                Point2D pt = map.getTileFactory().geoToPixel(place.position, map.getZoom());
                int x = (int) pt.getX() - rect.x;
                int y = (int) pt.getY() - rect.y;

                // 🟢 වටේ තියෙන Safe Range රවුම
                g.setColor(new Color(0, 220, 100, 40));
                g.fillOval(x - 20, y - 20, 40, 40);

                // 🛠️ TYPE එක අනුව වෙනස්ම ICON එකක් GRAPHICS වලින් අඳිනවා
                switch (place.type) {
                    case "Home":
                        g.setColor(new Color(0, 102, 204)); // Blue Home
                        // ගෙදර බිත්ති කොටස
                        g.fillRect(x - 8, y, 16, 10);
                        // වහලය (Polygon/Triangle)
                        Polygon roof = new Polygon();
                        roof.addPoint(x, y - 8);
                        roof.addPoint(x - 11, y);
                        roof.addPoint(x + 11, y);
                        g.fillPolygon(roof);
                        break;

                    case "School":
                        g.setColor(new Color(153, 0, 153)); // Purple School
                        // ප්‍රධාන බිල්ඩින් එක
                        g.fillRect(x - 10, y - 4, 20, 14);
                        g.fillRect(x - 4, y - 10, 8, 6);
                        // පොඩි කොඩියක්
                        g.setColor(Color.RED);
                        g.drawLine(x, y - 10, x, y - 14);
                        g.fillRect(x, y - 14, 6, 4);
                        break;

                    case "Farm":
                        g.setColor(new Color(0, 128, 0)); // Dark Green Farm
                        // වගා බිමක කොටුවක්
                        g.setStroke(new BasicStroke(2));
                        g.drawRect(x - 9, y - 9, 18, 18);
                        g.setColor(new Color(0, 200, 0, 120));
                        g.fillRect(x - 7, y - 7, 15, 15);
                        // මැද ඉරි කෑලි (Grid rows)
                        g.setColor(new Color(0, 100, 0));
                        g.drawLine(x - 5, y - 3, x + 5, y - 3);
                        g.drawLine(x - 5, y + 3, x + 5, y + 3);
                        break;

                    default: // "Other" option එකට normal dot එකක්
                        g.setColor(new Color(100, 100, 100)); // Gray Dot
                        g.fillOval(x - 6, y - 6, 12, 12);
                        g.setColor(Color.WHITE);
                        g.setStroke(new BasicStroke(1.5f));
                        g.drawOval(x - 6, y - 6, 12, 12);
                        break;
                }

                // ස්ථානයේ නම ලියනවා
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g.drawString(place.name, x + 14, y + 5);
            }
            g.dispose();
        };

        CompoundPainter<JXMapViewer> compound = new CompoundPainter<>(circlePainter, polygonPainter, customPlacesPainter);
        mapViewer.setOverlayPainter(compound);
        mapViewer.repaint();
    }

    @Override
    public void dispose() {
        if (liveUpdateTimer != null) liveUpdateTimer.stop();
        super.dispose();
    }

    private void setupControls() {
        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setBackground(new Color(245, 245, 245));
        alertFeed.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        comboProvince = new JComboBox<>();
        comboDistrict = new JComboBox<>(new String[]{"Select District"});
        comboVillage = new JComboBox<>(new String[]{"Select Village"});

        JLabel lblFeed = new JLabel("GAJA-SAFE SECURITY PANEL:");
        lblFeed.setFont(new Font("Arial", Font.BOLD, 18));
        jPanel1.add(lblFeed, new AbsoluteConstraints(1150, 120, -1, -1));

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        jPanel1.add(scrollFeed, new AbsoluteConstraints(1150, 180, 350, 520));
    }

    @SuppressWarnings("unchecked")
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
        dashboard_btn.setFont(new java.awt.Font("Poppins", 1, 16));
        dashboard_btn.setForeground(new java.awt.Color(255, 255, 255));
        dashboard_btn.setText("DASHBOARD");
        dashboard_btn.setBorder(null);
        dashboard_btn.setContentAreaFilled(false);
        dashboard_btn.addActionListener(evt -> dashboard_btnActionPerformed(evt));
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 30, -1, -1));

        aboutus_btn.setBackground(new java.awt.Color(0, 0, 0));
        aboutus_btn.setFont(new java.awt.Font("Poppins", 1, 16));
        aboutus_btn.setForeground(new java.awt.Color(255, 255, 255));
        aboutus_btn.setText("ACTIVE ELEPHANTS");
        aboutus_btn.setBorder(null);
        aboutus_btn.setContentAreaFilled(false);
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 30, -1, -1));

        logout_btn.setBackground(new java.awt.Color(0, 0, 0));
        logout_btn.setFont(new java.awt.Font("Poppins", 1, 16));
        logout_btn.setForeground(new java.awt.Color(255, 255, 255));
        logout_btn.setText("LOGOUT");
        logout_btn.setBorder(null);
        logout_btn.setContentAreaFilled(false);
        logout_btn.addActionListener(evt -> logout_btnActionPerformed(evt));
        jPanel2.add(logout_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 30, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logo.png")));
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5.setFont(new java.awt.Font("Script MT Bold", 1, 45));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Efind");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 90));
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1550, 790));

        pack();
    }

    private void dashboard_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Main ud = new Main();
        ud.setVisible(true);
        this.dispose();
    }

    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {
        Home home = new Home();
        home.setVisible(true);
        this.dispose();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReportElephantUser().setVisible(true));
    }

    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton logout_btn;
}