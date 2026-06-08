package WildLife;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;
import Main.Home;

public class AgentDirectory extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger
            = java.util.logging.Logger.getLogger(AgentDirectory.class.getName());


    public void fetchAgentsFromAPI() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8081/api/agents"))
                            .GET().build();
                    java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        masterData.clear();
                        org.json.JSONArray agents = new org.json.JSONArray(response.body());
                        for (int i = 0; i < agents.length(); i++) {
                            org.json.JSONObject obj = agents.getJSONObject(i);
                            masterData.add(new Object[]{
                                    obj.get("id"), obj.getString("fullName"), obj.getString("contactNumber"),
                                    obj.getString("email"), obj.getString("address"), obj.getString("assignedArea"), "Action"
                            });
                        }
                    }
                } catch (Exception e) { System.err.println("API Error: " + e.getMessage()); }
                return null;
            }
            @Override
            protected void done() { renderPage(); }
        };
        worker.execute();
    }

    // ── Pagination state ──
    private int    currentPage = 1;
    private int    pageSize    = 10;
    private String searchText  = "";

    // Full master data — never touched by filter/pagination
    private final List<Object[]> masterData = new ArrayList<>();

    // The model the JTable renders
    private DefaultTableModel tableModel;

    // Footer / pagination widgets
    private JLabel  footerLabel;
    private JButton prevBtn, nextBtn;
    private JLabel  pageLabel;

    public AgentDirectory() {
        initComponents();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);

        // ── Table appearance ──
        tablescroll.getViewport().setBackground(new Color(0, 67, 67));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setBackground(new Color(0, 73, 73));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 90, 90));
        table.setRowHeight(50);

        // ── Model ──
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Full Name", "Contact Number", "Email", "Address", "Area", "Action"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        table.setModel(tableModel);
        table.getColumnModel().getColumn(6).setPreferredWidth(220);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), table));


        fetchAgentsFromAPI();

        // ── Footer label ──
        footerLabel = new JLabel("Showing 0 to 0 of 0 entries");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // ── Pagination buttons ──
        prevBtn   = new JButton("< Prev");
        nextBtn   = new JButton("Next >");
        pageLabel = new JLabel("Page 1");
        pageLabel.setForeground(Color.WHITE);
        pageLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        styleNavBtn(prevBtn);
        styleNavBtn(nextBtn);

        prevBtn.addActionListener(e -> {
            if (currentPage > 1) { currentPage--; renderPage(); }
        });
        nextBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) getFilteredData().size() / pageSize);
            if (currentPage < totalPages) { currentPage++; renderPage(); }
        });

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        navPanel.setOpaque(false);
        navPanel.add(prevBtn);
        navPanel.add(pageLabel);
        navPanel.add(nextBtn);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 20, 10, 20));
        footerPanel.add(footerLabel, BorderLayout.WEST);
        footerPanel.add(navPanel,    BorderLayout.EAST);

        tablepanel.add(footerPanel, BorderLayout.SOUTH);

        // ── Show-entries combo listener ──
        jComboBox1.addActionListener(e -> {
            pageSize    = Integer.parseInt((String) jComboBox1.getSelectedItem());
            currentPage = 1;
            renderPage();
        });

        // ── Search placeholder styling ──
        searchfield.setForeground(Color.GRAY);
        searchfield.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchfield.getText().equals("Search by Name,ID,Area...")) {
                    searchfield.setText("");
                    searchfield.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchfield.getText().trim().isEmpty()) {
                    searchfield.setText("Search by Name,ID,Area...");
                    searchfield.setForeground(Color.GRAY);
                }
            }
        });

        // ── Live search listener ──
        searchfield.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void removeUpdate(DocumentEvent e)  { onSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearch(); }
            private void onSearch() {
                String t = searchfield.getText().trim();
                searchText  = t.equals("Search by Name,ID,Area...") ? "" : t;
                currentPage = 1;
                renderPage();
            }
        });

        // ── Initial render ──
        renderPage();
    }

    // ── Returns master rows that match searchText ──
    private List<Object[]> getFilteredData() {
        if (searchText.isEmpty()) return new ArrayList<>(masterData);
        String lower = searchText.toLowerCase();
        List<Object[]> result = new ArrayList<>();
        for (Object[] row : masterData) {
            for (int c = 0; c <= 5; c++) {
                if (String.valueOf(row[c]).toLowerCase().contains(lower)) {
                    result.add(row);
                    break;
                }
            }
        }
        return result;
    }

    // ── Renders the correct page slice into the table ──
    private void renderPage() {
        List<Object[]> filtered   = getFilteredData();
        int total      = filtered.size();
        int totalPages = (total == 0) ? 1 : (int) Math.ceil((double) total / pageSize);
        currentPage    = Math.max(1, Math.min(currentPage, totalPages));

        int fromIdx = (currentPage - 1) * pageSize;
        int toIdx   = Math.min(fromIdx + pageSize, total);

        tableModel.setRowCount(0);
        for (int i = fromIdx; i < toIdx; i++) {
            tableModel.addRow(filtered.get(i));
        }

        if (total == 0) {
            footerLabel.setText("No entries found");
        } else {
            footerLabel.setText(
                    "Showing " + (fromIdx + 1) + " to " + toIdx + " of " + total + " entries");
        }

        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);
    }

    private void styleNavBtn(JButton b) {
        b.setBackground(new Color(0, 100, 80));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(80, 28));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        headerpanel    = new javax.swing.JPanel();
        headerRpanel   = new javax.swing.JPanel();
        dashboardbtn   = new javax.swing.JButton();   // was: homebtn    → now: DASHBOARD
        viewagentbtn   = new javax.swing.JButton();   // was: dashbtn    → now: VIEW AGENT
        logoutbtn      = new javax.swing.JButton();
        filler1        = new javax.swing.Box.Filler(new java.awt.Dimension(6,  0), new java.awt.Dimension(6,  0), new java.awt.Dimension(6,  32767));
        filler4        = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        filler5        = new javax.swing.Box.Filler(new java.awt.Dimension(0,  0), new java.awt.Dimension(0,  0), new java.awt.Dimension(32767, 0));
        filler6        = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        logopanel      = new javax.swing.JPanel();
        jLabel6        = new javax.swing.JLabel();
        titlepanel     = new javax.swing.JPanel();
        jLabel4        = new javax.swing.JLabel();
        centerpanel    = new javax.swing.JPanel();
        jLabel1        = new javax.swing.JLabel();
        tablepanel     = new javax.swing.JPanel();
        searchpanel    = new javax.swing.JPanel();
        leftsearch     = new javax.swing.JPanel();
        searchfield    = new javax.swing.JTextField();
        rightpanel     = new javax.swing.JPanel();
        jComboBox1     = new javax.swing.JComboBox<>();
        tablecontainer = new javax.swing.JPanel();
        tablescroll    = new javax.swing.JScrollPane();
        table          = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // ── Header ──
        headerpanel.setBackground(new java.awt.Color(0, 0, 0));
        headerpanel.setPreferredSize(new java.awt.Dimension(0, 100));
        headerpanel.setLayout(new java.awt.BorderLayout());

        headerRpanel.setBackground(new java.awt.Color(0, 0, 0));
        headerRpanel.setPreferredSize(new java.awt.Dimension(350, 0));

        // ── DASHBOARD button (was HOME) ──
        dashboardbtn.setFont(new java.awt.Font("Poppins", 1, 16));
        dashboardbtn.setForeground(java.awt.Color.WHITE);
        dashboardbtn.setText("DASHBOARD");                          // ← changed text
        dashboardbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 1, 1, 1));
        dashboardbtn.setBorderPainted(false);
        dashboardbtn.setContentAreaFilled(false);
        dashboardbtn.setFocusPainted(false);
        dashboardbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dashboardbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardbtn.setForeground(new java.awt.Color(255, 153, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardbtn.setForeground(java.awt.Color.WHITE);
            }
        });
        // ── Click: open Dashboard from main package ──
        dashboardbtn.addActionListener(e -> {
            new Main.MainDashboard().setVisible(true);                  // ← links to main.Dashboard
            this.dispose();
        });
        headerRpanel.add(dashboardbtn);
        headerRpanel.add(filler4);
        headerRpanel.add(filler5);

        // ── VIEW AGENT button (was DASHBOARD) ──
        viewagentbtn.setFont(new java.awt.Font("Poppins", 1, 16));
        viewagentbtn.setForeground(java.awt.Color.WHITE);
        viewagentbtn.setText("VIEW AGENT");                         // ← changed text
        viewagentbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 10, 1, 0));
        viewagentbtn.setBorderPainted(false);
        viewagentbtn.setContentAreaFilled(false);
        viewagentbtn.setFocusPainted(false);
        viewagentbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewagentbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                viewagentbtn.setForeground(new java.awt.Color(255, 153, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                viewagentbtn.setForeground(java.awt.Color.WHITE);
            }
        });
        // VIEW AGENT = current page, no navigation needed (or link elsewhere if needed)
        viewagentbtn.addActionListener(e -> {
            // Already on Agent Directory — highlight or do nothing
            // If you have a separate ViewAgent form, use:
            // new main.ViewAgent().setVisible(true); this.dispose();
        });
        headerRpanel.add(viewagentbtn);
        headerRpanel.add(filler1);
        headerRpanel.add(filler4);

        // ── LOG OUT button ──
        logoutbtn.setFont(new java.awt.Font("Poppins", 1, 16));
        logoutbtn.setForeground(java.awt.Color.WHITE);
        logoutbtn.setText("LOG OUT");
        logoutbtn.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 10, 1, 1));
        logoutbtn.setBorderPainted(false);
        logoutbtn.setContentAreaFilled(false);
        logoutbtn.setFocusPainted(false);
        logoutbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logoutbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutbtn.setForeground(new java.awt.Color(255, 153, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutbtn.setForeground(java.awt.Color.WHITE);
            }
        });
        // ── Click: open Home from main package ──
        logoutbtn.addActionListener(e -> {
            new Main.Home().setVisible(true);                       // ← links to main.Home
            this.dispose();
        });
        headerRpanel.add(logoutbtn);

        headerRpanel.add(filler1);
        headerRpanel.add(filler4);
        headerRpanel.add(filler5);
        headerRpanel.add(filler6);
        headerpanel.add(headerRpanel, java.awt.BorderLayout.EAST);

        // ── Logo ──
        logopanel.setBackground(new java.awt.Color(0, 0, 0));
        logopanel.setPreferredSize(new java.awt.Dimension(100, 0));
        logopanel.setLayout(new java.awt.BorderLayout());
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Agent/pictures/logo.png")));
        logopanel.add(jLabel6, java.awt.BorderLayout.CENTER);
        headerpanel.add(logopanel, java.awt.BorderLayout.WEST);

        // ── Title ──
        titlepanel.setBackground(new java.awt.Color(0, 0, 0));
        titlepanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 20));
        jLabel4.setFont(new java.awt.Font("Script MT Bold", 1, 45));
        jLabel4.setForeground(java.awt.Color.WHITE);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Efind");
        titlepanel.add(jLabel4);
        headerpanel.add(titlepanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(headerpanel, java.awt.BorderLayout.NORTH);


        // ── Centre panel ──
        centerpanel.setBackground(new java.awt.Color(0, 67, 67));
        centerpanel.setPreferredSize(new java.awt.Dimension(100, 75));
        centerpanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 32));
        jLabel1.setForeground(java.awt.Color.WHITE);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Agent Directory");
        jLabel1.setPreferredSize(new java.awt.Dimension(0, 50));
        centerpanel.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        // ── Table panel ──
        tablepanel.setBackground(new java.awt.Color(0, 67, 67));
        tablepanel.setOpaque(false);
        tablepanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        tablepanel.setLayout(new java.awt.BorderLayout());

        // Search row
        searchpanel.setMinimumSize(new java.awt.Dimension(1200, 70));
        searchpanel.setOpaque(false);
        searchpanel.setPreferredSize(new java.awt.Dimension(1200, 70));
        searchpanel.setLayout(new java.awt.BorderLayout());

        leftsearch.setOpaque(false);
        leftsearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 245, 25));
        searchfield.setText("Search by Name,ID,Area...");
        searchfield.setPreferredSize(new java.awt.Dimension(250, 35));
        leftsearch.add(searchfield);
        searchpanel.add(leftsearch, java.awt.BorderLayout.LINE_START);

        rightpanel.setOpaque(false);
        rightpanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 25));

        JLabel showLabel = new JLabel("Show");
        showLabel.setForeground(Color.WHITE);
        showLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rightpanel.add(showLabel);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"10", "25", "50", "100"}));
        jComboBox1.setPreferredSize(new java.awt.Dimension(72, 28));
        rightpanel.add(jComboBox1);

        JLabel entriesLabel = new JLabel("entries");
        entriesLabel.setForeground(Color.WHITE);
        entriesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rightpanel.add(entriesLabel);

        rightpanel.add(new javax.swing.Box.Filler(
                new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767)));

        searchpanel.add(rightpanel, java.awt.BorderLayout.LINE_END);
        tablepanel.add(searchpanel, java.awt.BorderLayout.PAGE_START);

        // Table scroll
        tablecontainer.setOpaque(false);
        tablecontainer.setPreferredSize(new java.awt.Dimension(1200, 500));
        tablecontainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 20));

        tablescroll.setPreferredSize(new java.awt.Dimension(1050, 450));
        table.setBackground(new java.awt.Color(0, 67, 67));
        table.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
        table.setForeground(java.awt.Color.WHITE);
        table.setGridColor(new java.awt.Color(153, 153, 153));
        table.setRowHeight(45);
        table.setSelectionForeground(java.awt.Color.WHITE);
        table.setShowGrid(true);
        tablescroll.setViewportView(table);
        tablecontainer.add(tablescroll);
        tablepanel.add(tablecontainer, java.awt.BorderLayout.CENTER);

        centerpanel.add(tablepanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(centerpanel, java.awt.BorderLayout.CENTER);

        pack();
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new AgentDirectory().setVisible(true));
    }

    // ── Variables declaration ──
    private javax.swing.JPanel      centerpanel;
    private javax.swing.JButton     dashboardbtn;    // was homebtn   → DASHBOARD
    private javax.swing.JButton     viewagentbtn;    // was dashbtn   → VIEW AGENT
    private javax.swing.JButton     logoutbtn;
    private javax.swing.Box.Filler  filler1, filler4, filler5, filler6;
    private javax.swing.JPanel      headerRpanel;
    private javax.swing.JPanel      headerpanel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel      jLabel1, jLabel4, jLabel6;
    private javax.swing.JPanel      leftsearch;
    private javax.swing.JPanel      logopanel;
    private javax.swing.JPanel      rightpanel;
    private javax.swing.JTextField  searchfield;
    private javax.swing.JPanel      searchpanel;
    private javax.swing.JTable      table;
    private javax.swing.JPanel      tablecontainer;
    private javax.swing.JPanel      tablepanel;
    private javax.swing.JScrollPane tablescroll;
    private javax.swing.JPanel      titlepanel;
}

// ── BUTTON RENDERER ───────────────────────────────────────────────────────────
class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton viewBtn   = new JButton("View");
    private final JButton editBtn   = new JButton("Edit");
    private final JButton deleteBtn = new JButton("Delete");

    public ButtonRenderer() {
        setLayout(new BorderLayout());
        setOpaque(true);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        inner.setOpaque(false);

        styleBtn(viewBtn,   new Color(0, 120, 180));
        styleBtn(editBtn,   new Color(180, 130, 0));
        styleBtn(deleteBtn, new Color(180, 40, 40));

        inner.add(viewBtn);
        inner.add(editBtn);
        inner.add(deleteBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        wrapper.add(inner, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private void styleBtn(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(68, 28));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }
}

// ── BUTTON EDITOR ─────────────────────────────────────────────────────────────
class ButtonEditor extends javax.swing.DefaultCellEditor {

    private final JPanel  panel     = new JPanel(new BorderLayout());
    private final JButton viewBtn   = new JButton("View");
    private final JButton editBtn   = new JButton("Edit");
    private final JButton deleteBtn = new JButton("Delete");

    private final JTable table;
    private int currentRow;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        setClickCountToStart(1);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        inner.setOpaque(false);

        styleBtn(viewBtn,   new Color(0, 120, 180));
        styleBtn(editBtn,   new Color(180, 130, 0));
        styleBtn(deleteBtn, new Color(180, 40, 40));

        inner.add(viewBtn);
        inner.add(editBtn);
        inner.add(deleteBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        wrapper.add(inner, BorderLayout.CENTER);
        panel.add(wrapper, BorderLayout.CENTER);
        panel.setOpaque(true);

        viewBtn.addActionListener(e -> {
            fireEditingStopped();
            showViewDialog(currentRow);
        });
        editBtn.addActionListener(e -> {
            fireEditingStopped();
            showEditDialog(currentRow);
        });
        deleteBtn.addActionListener(e -> {
            fireEditingStopped();
            int row = currentRow;
            String id = String.valueOf(table.getValueAt(row, 0));
            String name = String.valueOf(table.getValueAt(row, 1));

            int choice = JOptionPane.showConfirmDialog(table,
                    "Delete agent \"" + name + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                deleteAgentFromDatabase(id, row);

                ((DefaultTableModel) table.getModel()).removeRow(currentRow);
            }

        });

    }
    private void deleteAgentFromDatabase(String id, int row) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8081/api/agents/" + id))
                            .DELETE() // DELETE Method
                            .build();

                    java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                    return response.statusCode() == 200 || response.statusCode() == 204;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        // if deleted in databse then deleted from ui table
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                        JOptionPane.showMessageDialog(table, "Agent Deleted Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(table, "Failed to delete agent from database!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @Override
    public java.awt.Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentRow = row;
        return panel;
    }

    private void showViewDialog(int row) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        String info = "ID            : " + m.getValueAt(row, 0) + "\n"
                + "Full Name     : " + m.getValueAt(row, 1) + "\n"
                + "Contact       : " + m.getValueAt(row, 2) + "\n"
                + "Email         : " + m.getValueAt(row, 3) + "\n"
                + "Address       : " + m.getValueAt(row, 4) + "\n"
                + "Area          : " + m.getValueAt(row, 5);
        JOptionPane.showMessageDialog(table, info,
                "Agent Details — View Only", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditDialog(int row) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();

        javax.swing.JTextField tfName    = new javax.swing.JTextField(String.valueOf(m.getValueAt(row, 1)), 18);
        javax.swing.JTextField tfContact = new javax.swing.JTextField(String.valueOf(m.getValueAt(row, 2)), 18);
        javax.swing.JTextField tfEmail   = new javax.swing.JTextField(String.valueOf(m.getValueAt(row, 3)), 18);
        javax.swing.JTextField tfAddress = new javax.swing.JTextField(String.valueOf(m.getValueAt(row, 4)), 18);
        javax.swing.JTextField tfArea    = new javax.swing.JTextField(String.valueOf(m.getValueAt(row, 5)), 18);

        JPanel form = new JPanel(new java.awt.GridLayout(5, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new javax.swing.JLabel("Full Name:"));  form.add(tfName);
        form.add(new javax.swing.JLabel("Contact:"));    form.add(tfContact);
        form.add(new javax.swing.JLabel("Email:"));      form.add(tfEmail);
        form.add(new javax.swing.JLabel("Address:"));    form.add(tfAddress);
        form.add(new javax.swing.JLabel("Area:"));       form.add(tfArea);

        int result = JOptionPane.showConfirmDialog(table, form,
                "Edit Agent — ID: " + m.getValueAt(row, 0),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            m.setValueAt(tfName.getText().trim(),    row, 1);
            m.setValueAt(tfContact.getText().trim(), row, 2);
            m.setValueAt(tfEmail.getText().trim(),   row, 3);
            m.setValueAt(tfAddress.getText().trim(), row, 4);
            m.setValueAt(tfArea.getText().trim(),    row, 5);

        }
        if (result == JOptionPane.OK_OPTION) {
            // take the new details to update
            String id = String.valueOf(m.getValueAt(row, 0));
            String name = tfName.getText().trim();
            String contact = tfContact.getText().trim();
            String email = tfEmail.getText().trim();
            String address = tfAddress.getText().trim();
            String area = tfArea.getText().trim();


            updateAgentInDatabase(id, name, contact, email, address, area, row);
        }


    }
    private void updateAgentInDatabase(String id, String name, String contact, String email, String address, String area, int row) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {

                    org.json.JSONObject updatedData = new org.json.JSONObject();
                    updatedData.put("fullName", name);
                    updatedData.put("contactNumber", contact);
                    updatedData.put("email", email);
                    updatedData.put("address", address);
                    updatedData.put("assignedArea", area);
                    updatedData.put("employeeId", "EMP123");

                    // Make the HTTP Request for put
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8081/api/agents/" + id))
                            .header("Content-Type", "application/json")
                            .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(updatedData.toString()))
                            .build();

                    java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                    System.out.println("Status Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());
                    return response.statusCode() == 200 || response.statusCode() == 204;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        // if successful then update the table
                        DefaultTableModel m = (DefaultTableModel) table.getModel();
                        m.setValueAt(name, row, 1);
                        m.setValueAt(contact, row, 2);
                        m.setValueAt(email, row, 3);
                        m.setValueAt(address, row, 4);
                        m.setValueAt(area, row, 5);
                        JOptionPane.showMessageDialog(table, "Agent Updated Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(table, "Failed to update database!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }



    private void styleBtn(JButton b, Color bg) {
        b.setPreferredSize(new Dimension(68, 28));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    @Override
    public Object getCellEditorValue() { return ""; }



}