import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

/**
 * Uygulamada kullanılan renk sabitleri ve UI fabrika metodları.
 * FlatLaf'ın renderer'ını kullanır — paintComponent override yok.
 */
public class Theme {

    // Arkaplan
    public static final Color BG         = new Color(18, 18, 24);
    public static final Color PANEL      = new Color(28, 28, 38);
    public static final Color ROW_ODD    = new Color(32, 32, 44);
    public static final Color ROW_EVEN   = new Color(24, 24, 34);

    // Vurgu
    public static final Color ACCENT     = new Color(220, 50, 50);

    // Metin
    public static final Color TEXT_MAIN  = new Color(230, 230, 235);
    public static final Color TEXT_SUB   = new Color(140, 140, 155);
    public static final Color TEXT_FAINT = new Color(70, 70, 90);

    // Durum renkleri
    public static final Color GREEN      = new Color(80, 200, 120);
    public static final Color ORANGE     = Color.ORANGE;
    public static final Color RED        = new Color(220, 70, 70);

    // Kenarlık
    public static final Color BORDER     = new Color(55, 55, 75);

    // Buton arka planı (kaldır butonu)
    public static final Color BTN_MUTED  = new Color(55, 55, 72);

    // -------------------------------------------------------------------
    // Başlatma — Main.java'da UIManager satırı yerine çağrılmalı
    // -------------------------------------------------------------------

    public static void setup() {
        FlatDarkLaf.setup();
        customizeFlatLaf();
    }

    // -------------------------------------------------------------------
    // FlatLaf renk override'ları
    // -------------------------------------------------------------------

    private static void customizeFlatLaf() {
        // Genel
        UIManager.put("Panel.background",              BG);
        UIManager.put("RootPane.background",           BG);
        UIManager.put("OptionPane.background",         PANEL);

        // TextField
        UIManager.put("TextField.background",          PANEL);
        UIManager.put("TextField.foreground",          TEXT_MAIN);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("TextField.selectionBackground", ACCENT);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        // Tablo
        UIManager.put("Table.background",              ROW_ODD);
        UIManager.put("Table.foreground",              TEXT_MAIN);
        UIManager.put("Table.alternateRowColor",       ROW_EVEN);
        UIManager.put("Table.selectionBackground",     new Color(60, 40, 40));
        UIManager.put("Table.selectionForeground",     Color.WHITE);
        UIManager.put("Table.gridColor",               BG);
        UIManager.put("Table.showHorizontalLines",     false);
        UIManager.put("Table.showVerticalLines",       false);

        // Tablo başlığı
        UIManager.put("TableHeader.background",        new Color(22, 22, 30));
        UIManager.put("TableHeader.foreground",        TEXT_SUB);
        UIManager.put("TableHeader.separatorColor",    BORDER);
        UIManager.put("TableHeader.font",              new Font("Segoe UI", Font.BOLD, 12));

        // ScrollPane / ScrollBar
        UIManager.put("ScrollPane.background",         BG);
        UIManager.put("ScrollBar.background",          BG);
        UIManager.put("ScrollBar.thumb",               BORDER);
        UIManager.put("ScrollBar.track",               BG);
        UIManager.put("ScrollBar.width",               8);

        // Buton — FlatLaf hover/press efektleri artık çalışır
        UIManager.put("Button.background",             BTN_MUTED);
        UIManager.put("Button.foreground",             TEXT_SUB);
        UIManager.put("Button.hoverBackground",        new Color(70, 70, 90));
        UIManager.put("Button.pressedBackground",      new Color(40, 40, 55));
        UIManager.put("Button.arc",                    6);
        UIManager.put("Button.font",                   new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Button.focusedBorderColor",     ACCENT);
        UIManager.put("Button.borderWidth",            0);
    }

    // -------------------------------------------------------------------
    // Fabrika — paintComponent override YOK, FlatLaf render eder
    // -------------------------------------------------------------------

    public static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect"); // FlatLaf yuvarlak köşe
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}