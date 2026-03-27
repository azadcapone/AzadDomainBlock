import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Objects;

public class SiteBlockerPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JTextField        siteInput;
    private final JLabel            statusLabel;

    private Runnable onAdd;
    private Runnable onRemove;

    public SiteBlockerPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 0));

        String[] cols = {"#", "Engellenen Site", "Yönlendirme", "Durum"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable();

        statusLabel = new JLabel("Hazır.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Theme.TEXT_SUB);

        siteInput = buildInput();

        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildScrollPane(), BorderLayout.CENTER);
        add(buildBottom(),     BorderLayout.SOUTH);
    }

    public void setOnAdd(Runnable r)    { this.onAdd    = r; }
    public void setOnRemove(Runnable r) { this.onRemove = r; }

    public JTable getTable() { return table; }

    public void addRow(String site) {
        addRow(site, HostsManager.REDIRECT_IP);
    }

    public void addRow(String site, String ip) {
        int row = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{row, site, ip, "✔ Aktif"});
    }

    public void updateRowIp(int row, String newIp) {
        tableModel.setValueAt(newIp, row, 2);
    }

    public void removeSelectedRow() {
        int selected = table.getSelectedRow();
        if (selected < 0) return;
        tableModel.removeRow(selected);
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }
    }

    public String getSelectedSite() {
        int selected = table.getSelectedRow();
        if (selected < 0) return null;
        return tableModel.getValueAt(selected, 1).toString();
    }

    public boolean containsSite(String site) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).toString().equals(site)) return true;
        }
        return false;
    }

    public String getInputText() { return siteInput.getText().trim().toLowerCase(); }
    public void   clearInput()   { siteInput.setText(""); }

    public void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.PANEL);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT));
        topBar.setPreferredSize(new Dimension(680, 60));

        JLabel title = new JLabel("  Site Engelleyici");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.TEXT_MAIN);

        JLabel subtitle = new JLabel("hosts dosyası üzerinden site engelleme  ");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Theme.TEXT_SUB);

        topBar.add(title,    BorderLayout.WEST);
        topBar.add(subtitle, BorderLayout.EAST);
        return topBar;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel);
        t.setBackground(Theme.ROW_ODD);
        t.setForeground(Theme.TEXT_MAIN);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(60, 40, 40));
        t.setSelectionForeground(Color.WHITE);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(22, 22, 30));
        header.setForeground(Theme.TEXT_SUB);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        header.setReorderingAllowed(false);

        t.getColumnModel().getColumn(0).setMaxWidth(40);
        t.getColumnModel().getColumn(0).setMinWidth(40);
        t.getColumnModel().getColumn(2).setMaxWidth(120);
        t.getColumnModel().getColumn(3).setMaxWidth(90);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
                setOpaque(true);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Theme.ROW_EVEN : Theme.ROW_ODD);
                    setForeground(Theme.TEXT_MAIN);
                }
                if (col == 3) { setForeground(Theme.GREEN); setText("✔ Aktif"); }
                if (col == 0)   setForeground(Theme.TEXT_SUB);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        return t;
    }

    private JScrollPane buildScrollPane() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BG);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));
        scroll.getViewport().setBackground(Theme.ROW_ODD);
        scroll.setPreferredSize(new Dimension(650, 280));
        return scroll;
    }

    private JTextField buildInput() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(Theme.PANEL);
        tf.setForeground(Theme.TEXT_MAIN);
        tf.setCaretColor(Theme.ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.putClientProperty("JTextField.placeholderText", "örn: tiktok.com");
        tf.addActionListener(e -> { if (onAdd != null) onAdd.run(); });
        return tf;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(Theme.BG);

        JButton addBtn = Theme.makeButton("+ Engelle", Theme.ACCENT, Color.WHITE);
        addBtn.setPreferredSize(new Dimension(120, 36));
        addBtn.addActionListener(e -> { if (onAdd    != null) onAdd.run(); });

        JButton removeBtn = Theme.makeButton("✕ Kaldır", Theme.BTN_MUTED, Theme.TEXT_SUB);
        removeBtn.setPreferredSize(new Dimension(110, 36));
        removeBtn.addActionListener(e -> { if (onRemove != null) onRemove.run(); });

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGroup.setBackground(Theme.BG);
        btnGroup.add(addBtn);
        btnGroup.add(removeBtn);

        inputRow.add(siteInput, BorderLayout.CENTER);
        inputRow.add(btnGroup,  BorderLayout.EAST);

        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setBackground(Theme.BG);
        statusRow.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));

        JLabel hostsPath = new JLabel(HostsManager.HOSTS_PATH);
        hostsPath.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hostsPath.setForeground(Theme.TEXT_FAINT);

        statusRow.add(statusLabel, BorderLayout.WEST);
        statusRow.add(hostsPath,   BorderLayout.EAST);

        bottom.add(inputRow);
        bottom.add(statusRow);
        return bottom;
    }
}