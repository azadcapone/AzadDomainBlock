import javax.swing.*;
import java.awt.*;

/**
 * Uygulamada kullanılan renk sabitleri ve UI fabrika metodları.
 */
public class Theme {

    // Arkaplan
    public static final Color BG          = new Color(18, 18, 24);
    public static final Color PANEL       = new Color(28, 28, 38);
    public static final Color ROW_ODD     = new Color(32, 32, 44);
    public static final Color ROW_EVEN    = new Color(24, 24, 34);

    // Vurgu
    public static final Color ACCENT      = new Color(220, 50, 50);

    // Metin
    public static final Color TEXT_MAIN   = new Color(230, 230, 235);
    public static final Color TEXT_SUB    = new Color(140, 140, 155);
    public static final Color TEXT_FAINT  = new Color(70, 70, 90);

    // Durum renkleri
    public static final Color GREEN       = new Color(80, 200, 120);
    public static final Color ORANGE      = Color.ORANGE;
    public static final Color RED         = new Color(220, 70, 70);

    // Kenarlık
    public static final Color BORDER      = new Color(55, 55, 75);

    // Buton arka planı (kaldır butonu)
    public static final Color BTN_MUTED   = new Color(55, 55, 72);


    // Fabrika
    public static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
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
}