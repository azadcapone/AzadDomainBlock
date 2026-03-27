import javax.swing.*;
import java.io.FileOutputStream;

/**
 * Uygulama giriş noktası.
 * Admin yetkisi kontrolü ve gerekirse UAC ile yeniden başlatma burada yapılır.
 */
public class Main {

    public static void main(String[] args) {
        if (!isAdmin()) {
            try {
                relaunchAsAdmin();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Yönetici izni alınamadı.\n\n" + e.getMessage(),
                        "Yetki Hatası",
                        JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0); // yetkisiz süreç kapanır
            return;
        }

        // Admin olarak çalışıyor — UI başlat
        Theme.setup();

        SwingUtilities.invokeLater(() -> new SiteBlocker().setVisible(true));
    }

    /**
     * hosts dosyasına rw erişimi deneyerek yönetici yetkisi olup olmadığını sınar.
     */
    private static boolean isAdmin() {
        try {
            new FileOutputStream(HostsManager.HOSTS_PATH, true).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Uygulamayı PowerShell aracılığıyla UAC (Yönetici olarak çalıştır) ile yeniden başlatır.
     */
    private static void relaunchAsAdmin() throws Exception {
        String javaExe = ProcessHandle.current().info().command()
                .orElse(System.getProperty("java.home") + "\\bin\\java.exe");

        String classpath = System.getProperty("java.class.path");

        String psCommand = String.format(
                "Start-Process -FilePath '%s' -ArgumentList '-cp \"%s\" Main' -Verb RunAs -Wait",
                javaExe, classpath
        );

        new ProcessBuilder(
                "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", psCommand)
                .inheritIO()
                .start()
                .waitFor();
    }
}