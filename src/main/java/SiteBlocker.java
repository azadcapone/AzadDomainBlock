import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class SiteBlocker extends JFrame {

    private static final String HOSTS_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    private static final String REDIRECT_IP = "127.0.0.1";
    private static final String BLOCK_TAG = "# [SiteBlocker]";

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField siteInput;
    private JLabel statusLabel;

    public SiteBlocker() {
        setTitle("🛡️ Site Blocker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        loadBlockedSites();
    }

    private void initUI() {
        // Dark theme colors
        Color bg = new Color(18, 18, 24);
        Color panel = new Color(28, 28, 38);
        Color accent = new Color(220, 50, 50);
        Color accentHover = new Color(255, 70, 70);
        Color textMain = new Color(230, 230, 235);
        Color textSub = new Color(140, 140, 155);
        Color rowOdd = new Color(32, 32, 44);
        Color rowEven = new Color(24, 24, 34);
        Color border = new Color(55, 55, 75);

        getContentPane().setBackground(bg);
        setLayout(new BorderLayout(0, 0));

        // ---- TOP BAR ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(panel);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accent));
        topBar.setPreferredSize(new Dimension(680, 60));

        JLabel title = new JLabel("  🛡 Site Blocker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(textMain);

        JLabel subtitle = new JLabel("hosts dosyası üzerinden site engelleme  ");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(textSub);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(subtitle, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ---- CENTER: TABLE ----
        String[] cols = {"#", "Engellenen Site", "Yönlendirme", "Durum"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(rowOdd);
        table.setForeground(textMain);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(60, 40, 40));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(22, 22, 30));
        table.getTableHeader().setForeground(textSub);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, border));
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(120);
        table.getColumnModel().getColumn(3).setMaxWidth(90);

        // Row renderer - alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setOpaque(true);
                if (!sel) {
                    setBackground(row % 2 == 0 ? rowEven : rowOdd);
                    setForeground(textMain);
                }
                if (col == 3) {
                    setForeground(new Color(80, 200, 120));
                    setText("✔ Aktif");
                }
                if (col == 0) setForeground(textSub);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(bg);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));
        scroll.getViewport().setBackground(rowOdd);
        scroll.setPreferredSize(new Dimension(650, 280));
        add(scroll, BorderLayout.CENTER);

        // ---- BOTTOM PANEL ----
        JPanel bottom = new JPanel();
        bottom.setBackground(bg);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));

        // Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(bg);

        siteInput = new JTextField();
        siteInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        siteInput.setBackground(panel);
        siteInput.setForeground(textMain);
        siteInput.setCaretColor(accent);
        siteInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        siteInput.putClientProperty("JTextField.placeholderText", "örn: facebook.com");

        JButton addBtn = makeButton("+ Engelle", accent, Color.WHITE);
        addBtn.setPreferredSize(new Dimension(120, 36));
        addBtn.addActionListener(e -> addSite());
        siteInput.addActionListener(e -> addSite());

        JButton removeBtn = makeButton("✕ Kaldır", new Color(55, 55, 72), textSub);
        removeBtn.setPreferredSize(new Dimension(110, 36));
        removeBtn.addActionListener(e -> removeSite());

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGroup.setBackground(bg);
        btnGroup.add(addBtn);
        btnGroup.add(removeBtn);

        inputRow.add(siteInput, BorderLayout.CENTER);
        inputRow.add(btnGroup, BorderLayout.EAST);

        // Status bar
        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setBackground(bg);
        statusRow.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 2));

        statusLabel = new JLabel("Hazır.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(textSub);

        JLabel hostsPath = new JLabel(HOSTS_PATH);
        hostsPath.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hostsPath.setForeground(new Color(70, 70, 90));

        statusRow.add(statusLabel, BorderLayout.WEST);
        statusRow.add(hostsPath, BorderLayout.EAST);

        bottom.add(inputRow);
        bottom.add(statusRow);

        add(bottom, BorderLayout.SOUTH);
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addSite() {
        String raw = siteInput.getText().trim().toLowerCase();
        if (raw.isEmpty()) {
            setStatus("⚠ Lütfen bir site adresi girin.", Color.ORANGE);
            return;
        }
        // strip protocol/www
        raw = raw.replaceAll("^https?://", "").replaceAll("^www\\.", "").split("/")[0];

        // Check duplicate in table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).toString().equals(raw)) {
                setStatus("⚠ Bu site zaten engellenmiş.", Color.ORANGE);
                return;
            }
        }

        final String site = raw;
        // Write to hosts
        try {
            writeToHosts(site, true);
            int row = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{row, site, REDIRECT_IP, "✔ Aktif"});
            siteInput.setText("");
            setStatus("✔ " + site + " engellendi.", new Color(80, 200, 120));
        } catch (Exception ex) {
            setStatus("✖ Hata: " + ex.getMessage(), new Color(220, 70, 70));
        }
    }

    private void removeSite() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            setStatus("⚠ Lütfen listeden bir site seçin.", Color.ORANGE);
            return;
        }
        String site = tableModel.getValueAt(selected, 1).toString();
        try {
            writeToHosts(site, false);
            tableModel.removeRow(selected);
            // Renumber
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(i + 1, i, 0);
            }
            setStatus("✔ " + site + " engeli kaldırıldı.", new Color(80, 200, 120));
        } catch (Exception ex) {
            setStatus("✖ Hata: " + ex.getMessage(), new Color(220, 70, 70));
        }
    }

    private void writeToHosts(String site, boolean add) throws IOException {
        File hostsFile = new File(HOSTS_PATH);
        List<String> lines = new ArrayList<>(Files.readAllLines(hostsFile.toPath()));

        String entry = REDIRECT_IP + "\t" + site + "\t" + BLOCK_TAG;
        String entryWww = REDIRECT_IP + "\t" + "www." + site + "\t" + BLOCK_TAG;

        if (add) {
            // Check not already present
            boolean found = lines.stream().anyMatch(l -> l.contains("\t" + site + "\t"));
            if (!found) {
                lines.add(entry);
                lines.add(entryWww);
            }
        } else {
            lines.removeIf(l -> l.contains("\t" + site + "\t") || l.contains("\t" + "www." + site + "\t"));
        }

        Files.write(hostsFile.toPath(), lines);
    }

    private void loadBlockedSites() {
        try {
            File hostsFile = new File(HOSTS_PATH);
            List<String> lines = Files.readAllLines(hostsFile.toPath());
            Set<String> seen = new LinkedHashSet<>();
            for (String line : lines) {
                if (line.contains(BLOCK_TAG)) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 2) {
                        String site = parts[1].replaceAll("^www\\.", "");
                        seen.add(site);
                    }
                }
            }
            int i = 1;
            for (String s : seen) {
                tableModel.addRow(new Object[]{i++, s, REDIRECT_IP, "✔ Aktif"});
            }
            if (!seen.isEmpty()) setStatus(seen.size() + " engellenen site yüklendi.", new Color(80, 200, 120));
        } catch (Exception ex) {
            setStatus("⚠ hosts dosyası okunamadı: " + ex.getMessage(), Color.ORANGE);
        }
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ---- Admin kontrolü ----
    private static boolean isAdmin() {
        try {
            new FileOutputStream("C:\\Windows\\System32\\drivers\\etc\\hosts", true).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ---- Kendini PowerShell aracılığıyla UAC ile yeniden başlat ----
    private static void relaunchAsAdmin() throws Exception {
        // Çalışan java.exe'nin tam yolu
        String javaExe = ProcessHandle.current().info().command()
                .orElse(System.getProperty("java.home") + "\\bin\\java.exe");

        // Mevcut classpath (IntelliJ out/production veya Gradle build/classes)
        String classpath = System.getProperty("java.class.path");

        // PowerShell komutu: -Verb RunAs = UAC penceresi
        String psCommand = String.format(
                "Start-Process -FilePath '%s' -ArgumentList '-cp \"%s\" SiteBlocker' -Verb RunAs -Wait",
                javaExe, classpath
        );

        new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", psCommand)
                .inheritIO()
                .start()
                .waitFor();
    }

    public static void main(String[] args) {
        if (!isAdmin()) {
            try {
                relaunchAsAdmin();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Yönetici izni alınamadı.\n\n" + e.getMessage(),
                        "Yetki Hatası", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0); // eski (yetkisiz) süreç kapanır
            return;
        }

        // Admin olarak çalışıyor — UI'yi başlat
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SiteBlocker().setVisible(true));
    }
}