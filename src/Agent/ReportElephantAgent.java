package Agent;

import Main.Home;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportElephantAgent extends javax.swing.JFrame {

    private JXMapViewer mapViewer;
    private JTextArea alertFeed;

    private JRadioButton radPointMode;
    private JRadioButton radAreaMode;
    private JRadioButton radEraseMode;
    private JButton btnClearDraw;

    private Set<GeoPosition> activePoints = new HashSet<>();
    private List<DangerZone> savedAreas = new ArrayList<>();
    private List<GeoPosition> currentDrawingArea = new ArrayList<>();

    class DangerZone {
        Long id;
        List<GeoPosition> points;
        public DangerZone(Long id, List<GeoPosition> points) {
            this.id = id;
            this.points = points;
        }
    }

    public ReportElephantAgent() {
        initComponents();
        setupModernLayout();

        loadExistingPoints();
        loadExistingAreas();

        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setupModernLayout() {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        // 1. Top Navigation Bar (පරණ Colors සහ Fonts එක්ක)
        setupTopNavigationBar();

        // 2. Main Body
        JPanel bodyPanel = new JPanel(new BorderLayout());
        getContentPane().add(bodyPanel, BorderLayout.CENTER);

        // 3. Right Sidebar
        JPanel rightSidebar = new JPanel(new BorderLayout(0, 15));
        rightSidebar.setPreferredSize(new Dimension(380, 0));
        rightSidebar.setBackground(new Color(245, 245, 248));
        rightSidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 3.1 Controls Panel (BoxLayout පාවිච්චි කළා Button එක යටින් ගන්න)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);

        JLabel lblMode = new JLabel("🎯 DRAWING CONTROLS");
        lblMode.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        radPointMode = new JRadioButton("🔴 Point Mode (Double-Click)", true);
        radAreaMode = new JRadioButton("🔲 Area Mode (Draw Zone)");
        radEraseMode = new JRadioButton("🗑️ Erase Mode (Click to Delete)");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(radPointMode);
        modeGroup.add(radAreaMode);
        modeGroup.add(radEraseMode);

        Font radFont = new Font("Segoe UI", Font.BOLD, 14);
        radPointMode.setFont(radFont); radPointMode.setOpaque(false); radPointMode.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radAreaMode.setFont(radFont); radAreaMode.setOpaque(false); radAreaMode.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radEraseMode.setFont(radFont); radEraseMode.setOpaque(false); radEraseMode.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Area Mode එකට යටින් එන්න ඕන Cancel Button එක
        btnClearDraw = new JButton("Cancel Current Drawing");
        btnClearDraw.setBackground(new Color(108, 117, 125));
        btnClearDraw.setForeground(Color.WHITE);
        btnClearDraw.setFocusPainted(false);
        btnClearDraw.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearDraw.setVisible(false); // මුලින්ම පේන්නෙ නෑ

        // Button එක ටිකක් ඇතුලට වෙන්න ලස්සනට තියන්න Panel එකක්
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        cancelPanel.setOpaque(false);
        cancelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelPanel.add(btnClearDraw);

        btnClearDraw.addActionListener(e -> {
            currentDrawingArea.clear();
            refreshMapOverlay();
        });

        // Radio Buttons Click කරද්දී Button එක පේන එක පාලනය කිරීම
        radAreaMode.addActionListener(e -> btnClearDraw.setVisible(true));
        radPointMode.addActionListener(e -> btnClearDraw.setVisible(false));
        radEraseMode.addActionListener(e -> btnClearDraw.setVisible(false));

        controlsPanel.add(lblMode);
        controlsPanel.add(Box.createVerticalStrut(15));
        controlsPanel.add(radPointMode);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(radAreaMode);
        controlsPanel.add(Box.createVerticalStrut(5));
        controlsPanel.add(cancelPanel); // Area Mode එකට යටින් Button එක
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(radEraseMode);

        rightSidebar.add(controlsPanel, BorderLayout.NORTH);

        // 3.2 Live Feed
        JPanel feedPanel = new JPanel(new BorderLayout(0, 10));
        feedPanel.setOpaque(false);

        JLabel lblFeed = new JLabel("📋 LIVE ALERT LOG");
        lblFeed.setFont(new Font("Segoe UI", Font.BOLD, 16));

        alertFeed = new JTextArea();
        alertFeed.setEditable(false);
        alertFeed.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        alertFeed.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollFeed = new JScrollPane(alertFeed);
        feedPanel.add(lblFeed, BorderLayout.NORTH);
        feedPanel.add(scrollFeed, BorderLayout.CENTER);

        rightSidebar.add(feedPanel, BorderLayout.CENTER);

        bodyPanel.add(rightSidebar, BorderLayout.EAST);

        // 4. Map Setup
        initMapViewer();
        bodyPanel.add(mapViewer, BorderLayout.CENTER);

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void setupTopNavigationBar() {
        jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(0, 0, 0)); // පරණ කළු පාට
        jPanel2.setPreferredSize(new Dimension(1920, 90));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8 = new JLabel(new ImageIcon(getClass().getResource("/image/logo.png")));
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 90, 90));

        jLabel5 = new JLabel("Efind");
        jLabel5.setFont(new Font("Script MT Bold", Font.BOLD, 45));
        jLabel5.setForeground(Color.WHITE);
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 210, -1));

        // පරණ Hover Colors සහ Poppins Font එක ආයෙත් දැම්මා
        dashboard_btn = createNavButton("DASHBOARD");
        dashboard_btn.addActionListener(evt -> { Dashboard db = new Dashboard(); db.setVisible(true); this.dispose(); });
        jPanel2.add(dashboard_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 30, -1, -1));

        aboutus_btn = createNavButton("ACTIVE ELEPHANTS");
        jPanel2.add(aboutus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 30, -1, -1));

        logout_btn = createNavButton("LOGOUT");
        logout_btn.addActionListener(evt -> { Home home = new Home(); home.setVisible(true); this.dispose(); });
        jPanel2.add(logout_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 30, -1, -1));

        getContentPane().add(jPanel2, BorderLayout.NORTH);
    }

    // Nav Bar Buttons හදන එක ලේසි වෙන්න වෙනම Method එකක් ලිව්වා
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 0, 0));
        btn.setFont(new Font("Poppins", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect එක (පරණ Color Code එක: 255,153,0)
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setForeground(new Color(255, 153, 0));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
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
        org.jxmapviewer.input.PanMouseInputListener panListener = new org.jxmapviewer.input.PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeoPosition clickedPos = mapViewer.convertPointToGeoPosition(e.getPoint());
                Point2D clickPixel = e.getPoint();

                if (radPointMode.isSelected()) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                        if (JOptionPane.showConfirmDialog(null, "Activate Elephant Spot here?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            savePointToBackend(clickedPos);
                        }
                    }
                }
                else if (radAreaMode.isSelected()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        currentDrawingArea.add(clickedPos);
                        refreshMapOverlay();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        if (currentDrawingArea.size() >= 3) {
                            if (JOptionPane.showConfirmDialog(null, "Save this Danger Area?", "Save Area", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                saveAreaToBackend(new ArrayList<>(currentDrawingArea));
                                currentDrawingArea.clear();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Click at least 3 points to create an area.");
                            currentDrawingArea.clear();
                            refreshMapOverlay();
                        }
                    }
                }
                else if (radEraseMode.isSelected() && SwingUtilities.isLeftMouseButton(e)) {
                    boolean itemDeleted = false;
                    DangerZone zoneToDelete = null;

                    for (DangerZone zone : savedAreas) {
                        Polygon p = new Polygon();
                        for (GeoPosition pos : zone.points) {
                            Point2D pt = mapViewer.getTileFactory().geoToPixel(pos, mapViewer.getZoom());
                            Rectangle rect = mapViewer.getViewportBounds();
                            p.addPoint((int) pt.getX() - rect.x, (int) pt.getY() - rect.y);
                        }
                        if (p.contains(clickPixel.getX(), clickPixel.getY())) {
                            zoneToDelete = zone;
                            break;
                        }
                    }

                    if (zoneToDelete != null) {
                        if (JOptionPane.showConfirmDialog(null, "Delete this Danger Area?", "Erase", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            removeAreaFromBackend(zoneToDelete.id);
                        }
                        itemDeleted = true;
                    }

                    if (!itemDeleted) {
                        GeoPosition pointToDelete = null;
                        for (GeoPosition pos : activePoints) {
                            Point2D pt = mapViewer.getTileFactory().geoToPixel(pos, mapViewer.getZoom());
                            Rectangle rect = mapViewer.getViewportBounds();
                            int px = (int) pt.getX() - rect.x;
                            int py = (int) pt.getY() - rect.y;

                            if (Point2D.distance(clickPixel.getX(), clickPixel.getY(), px, py) < 15) {
                                pointToDelete = pos;
                                break;
                            }
                        }

                        if (pointToDelete != null) {
                            if (JOptionPane.showConfirmDialog(null, "Delete this Elephant Spot?", "Erase", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                removePoint(pointToDelete);
                            }
                        }
                    }
                }
            }
        });

        mapViewer.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        refreshMapOverlay();
    }

    private void refreshMapOverlay() {
        Set<Waypoint> waypoints = new HashSet<>();
        for (GeoPosition pos : activePoints) {
            waypoints.add(new DefaultWaypoint(pos));
        }
        WaypointPainter<Waypoint> wpPainter = new WaypointPainter<>();
        wpPainter.setWaypoints(waypoints);

        Painter<JXMapViewer> circlePainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();
            for (GeoPosition pos : activePoints) {
                Point2D centerPt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                double offsetInDegrees = 100.0 / 111320.0;
                GeoPosition edgePos = new GeoPosition(pos.getLatitude() + offsetInDegrees, pos.getLongitude());
                Point2D edgePt = map.getTileFactory().geoToPixel(edgePos, map.getZoom());
                int r = (int) Math.max(8, Math.abs(centerPt.getY() - edgePt.getY()));
                int x = (int) centerPt.getX() - rect.x;
                int y = (int) centerPt.getY() - rect.y;

                g.setColor(new Color(255, 0, 0, 100));
                g.fillOval(x - r, y - r, r * 2, r * 2);
                g.setColor(Color.RED);
                g.drawOval(x - r, y - r, r * 2, r * 2);
            }
            g.dispose();
        };

        Painter<JXMapViewer> polygonPainter = (Graphics2D g, JXMapViewer map, int w, int h) -> {
            g = (Graphics2D) g.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rect = map.getViewportBounds();

            for (DangerZone zone : savedAreas) {
                Polygon p = new Polygon();
                for (GeoPosition pos : zone.points) {
                    Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                    p.addPoint((int) pt.getX() - rect.x, (int) pt.getY() - rect.y);
                }
                g.setColor(new Color(255, 165, 0, 80));
                g.fillPolygon(p);
                g.setColor(Color.ORANGE);
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawPolygon(p);
            }

            if (!currentDrawingArea.isEmpty()) {
                Polygon p = new Polygon();
                for (GeoPosition pos : currentDrawingArea) {
                    Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
                    int px = (int) pt.getX() - rect.x;
                    int py = (int) pt.getY() - rect.y;
                    p.addPoint(px, py);

                    g.setColor(Color.RED);
                    g.fillOval(px - 4, py - 4, 8, 8);
                }
                g.setColor(new Color(255, 0, 0, 100));
                g.fillPolygon(p);

                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8}, 0));
                g.drawPolygon(p);
            }
            g.dispose();
        };

        CompoundPainter<JXMapViewer> compound = new CompoundPainter<>(circlePainter, polygonPainter, wpPainter);
        mapViewer.setOverlayPainter(compound);
    }

    private void savePointToBackend(GeoPosition pos) {
        String villageName = getNearestVillage(pos.getLatitude(), pos.getLongitude());
        try {
            java.net.URL url = new java.net.URL("http://localhost:8081/api/active-alerts/add");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String json = String.format("{\"latitude\": %f, \"longitude\": %f, \"placeName\": \"%s\"}", pos.getLatitude(), pos.getLongitude(), villageName);
            conn.getOutputStream().write(json.getBytes("utf-8"));
            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                loadExistingPoints();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removePoint(GeoPosition pos) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8081/api/active-alerts/remove?lat=" + pos.getLatitude() + "&lon=" + pos.getLongitude());
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            if (conn.getResponseCode() == 200) {
                loadExistingPoints();
                JOptionPane.showMessageDialog(this, "Elephant Spot Removed!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadExistingPoints() {
        try {
            activePoints.clear();
            alertFeed.setText("--- LIVE LOG ---\n\n");
            java.net.URL url = new java.net.URL("http://localhost:8081/api/active-alerts/all");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(conn.getInputStream());
                for (com.fasterxml.jackson.databind.JsonNode node : root) {
                    activePoints.add(new GeoPosition(node.get("latitude").asDouble(), node.get("longitude").asDouble()));
                    String name = node.has("placeName") ? node.get("placeName").asText() : "Unknown";
                    alertFeed.append("🔴 Spot: " + name + "\n");
                }
                refreshMapOverlay();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveAreaToBackend(List<GeoPosition> points) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8081/api/danger-areas/add");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            StringBuilder coordsJson = new StringBuilder("[");
            for (int i = 0; i < points.size(); i++) {
                coordsJson.append(String.format("{\"lat\": %f, \"lon\": %f}", points.get(i).getLatitude(), points.get(i).getLongitude()));
                if (i < points.size() - 1) coordsJson.append(",");
            }
            coordsJson.append("]");

            String json = String.format("{\"areaName\": \"Danger Zone\", \"coordinates\": \"%s\"}", coordsJson.toString().replace("\"", "\\\""));
            conn.getOutputStream().write(json.getBytes("utf-8"));

            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                loadExistingAreas();
                JOptionPane.showMessageDialog(this, "Danger Area Saved!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removeAreaFromBackend(Long id) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8081/api/danger-areas/remove/" + id);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            if (conn.getResponseCode() == 200) {
                loadExistingAreas();
                JOptionPane.showMessageDialog(this, "Danger Area Removed!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadExistingAreas() {
        try {
            savedAreas.clear();
            java.net.URL url = new java.net.URL("http://localhost:8081/api/danger-areas/all");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(conn.getInputStream());
                int count = 1;
                for (com.fasterxml.jackson.databind.JsonNode node : root) {
                    if(node.has("coordinates")) {
                        Long id = node.get("id").asLong();
                        String coordsString = node.get("coordinates").asText();
                        com.fasterxml.jackson.databind.JsonNode pointsArray = new com.fasterxml.jackson.databind.ObjectMapper().readTree(coordsString);

                        List<GeoPosition> area = new ArrayList<>();
                        for(com.fasterxml.jackson.databind.JsonNode ptNode : pointsArray) {
                            area.add(new GeoPosition(ptNode.get("lat").asDouble(), ptNode.get("lon").asDouble()));
                        }
                        savedAreas.add(new DangerZone(id, area));
                        alertFeed.append("🔲 Danger Zone " + count + " Loaded\n");
                        count++;
                    }
                }
                refreshMapOverlay();
            }
        } catch (Exception e) { System.out.println("No areas loaded."); }
    }

    private String getNearestVillage(double lat, double lon) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:8081/api/locations/nearest?lat=" + lat + "&lon=" + lon);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String v = in.readLine();
                in.close();
                return (v != null && !v.isEmpty()) ? v : "Unknown";
            }
        } catch (Exception e) {}
        return "Unknown";
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // මේක NetBeans වල Auto Generate වෙන නිසා හිස්ව තියන්නේ (Layout එක අපි Code එකෙන් හැදුවා)
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        java.awt.EventQueue.invokeLater(() -> new ReportElephantAgent().setVisible(true));
    }

    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton dashboard_btn;
    private javax.swing.JButton aboutus_btn;
    private javax.swing.JButton logout_btn;
}