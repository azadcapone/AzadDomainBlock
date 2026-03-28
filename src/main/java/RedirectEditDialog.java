import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedirectEditDialog extends JDialog {

    private final JTextField ipField;
    private String result = null;

    public RedirectEditDialog(Frame owner, String site, String currentIp) {
        super(owner, "Yönlendirme Düzenle", true);
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG);
        setContentPane(root);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Theme.PANEL);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT));
        top.setPreferredSize(new Dimension(400, 50));

        JLabel title = new JLabel("  " + site + " — Yönlendirme");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Theme.TEXT_MAIN);
        top.add(title, BorderLayout.WEST);
        root.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(18, 16, 10, 16));

        JLabel label = new JLabel("Yönlendirilecek IP adresi:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Theme.TEXT_SUB);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        ipField = new JTextField(currentIp);
        ipField.setFont(new Font("Segoe UI Mono", Font.PLAIN, 14));
        ipField.setBackground(Theme.PANEL);
        ipField.setForeground(Theme.TEXT_MAIN);
        ipField.setCaretColor(Theme.ACCENT);
        ipField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        ipField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        ipField.setAlignmentX(Component.LEFT_ALIGNMENT);
        SwingUtilities.invokeLater(() -> {
            ipField.requestFocusInWindow();
            ipField.selectAll();
        });

        JPanel suggestions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        suggestions.setBackground(Theme.BG);
        suggestions.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] presets = {"127.0.0.1", "0.0.0.0", "::1", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"};
        for (String ip : presets) {
            JButton btn = Theme.makeButton(ip, Theme.BTN_MUTED, Theme.TEXT_SUB);
            btn.setFont(new Font("Segoe UI Mono", Font.PLAIN, 11));
            btn.addActionListener(e -> ipField.setText(ip));
            suggestions.add(btn);
        }

        center.add(label);
        center.add(Box.createVerticalStrut(6));
        center.add(ipField);
        center.add(Box.createVerticalStrut(8));
        center.add(suggestions);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        bottom.setBackground(Theme.BG);

        JButton cancelBtn = Theme.makeButton("İptal", Theme.BTN_MUTED, Theme.TEXT_SUB);
        cancelBtn.setPreferredSize(new Dimension(80, 34));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = Theme.makeButton("Kaydet", Theme.ACCENT, Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(90, 34));
        saveBtn.addActionListener(e -> save());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);
        root.add(bottom, BorderLayout.SOUTH);

        ipField.addActionListener(e -> save());
        root.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void save() {
        String ip = ipField.getText().trim();
        if (!ip.isEmpty()) {
            result = ip;
        }
        dispose();
    }

    public String getResult() { return result; }
}