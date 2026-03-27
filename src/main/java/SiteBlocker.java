import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class SiteBlocker extends JFrame {

    private final SiteBlockerPanel panel;
    private final HostsManager     hostsManager;

    public SiteBlocker() {
        super("Site Blocker");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setIconImage(
                new ImageIcon(getClass().getResource("/logo.png")).getImage()
        );

        hostsManager = new HostsManager();
        panel        = new SiteBlockerPanel();

        // Eylem bağlama
        panel.setOnAdd(this::addSite);
        panel.setOnRemove(this::removeSite);

        // Çift tıklama → redirect IP düzenleme dialogu
        panel.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editRedirect();
                }
            }
        });

        setContentPane(panel);

        loadBlockedSites();
    }

    // -------------------------------------------------------------------
    // Eylemler
    // -------------------------------------------------------------------

    private void addSite() {
        String raw = panel.getInputText();

        if (raw.isEmpty()) {
            panel.setStatus("⚠ Lütfen bir site adresi girin.", Theme.ORANGE);
            return;
        }

        // Protokol ve www. önekini temizle
        String site = raw
                .replaceAll("^https?://", "")
                .replaceAll("^www\\.", "")
                .split("/")[0];

        if (panel.containsSite(site)) {
            panel.setStatus("! Bu site zaten engellenmiş.", Theme.ORANGE);
            return;
        }

        try {
            hostsManager.blockSite(site);
            panel.addRow(site, HostsManager.REDIRECT_IP);
            panel.clearInput();
            panel.setStatus("✔ " + site + " engellendi.", Theme.GREEN);
        } catch (Exception ex) {
            panel.setStatus("✖ Hata: " + ex.getMessage(), Theme.RED);
        }
    }

    private void removeSite() {
        String site = panel.getSelectedSite();

        if (site == null) {
            panel.setStatus("! Lütfen listeden bir site seçin.", Theme.ORANGE);
            return;
        }

        try {
            hostsManager.unblockSite(site);
            panel.removeSelectedRow();
            panel.setStatus("✔ " + site + " engeli kaldırıldı.", Theme.GREEN);
        } catch (Exception ex) {
            panel.setStatus("✖ Hata: " + ex.getMessage(), Theme.RED);
        }
    }

    private void editRedirect() {
        int row = panel.getTable().getSelectedRow();
        if (row < 0) return;

        String site      = panel.getSelectedSite();
        String currentIp = panel.getTable().getValueAt(row, 2).toString();

        RedirectEditDialog dialog = new RedirectEditDialog(this, site, currentIp);
        dialog.setVisible(true); // modal — burası kapanana kadar bloklar

        String newIp = dialog.getResult();
        if (newIp == null) return; // kullanıcı iptal etti

        try {
            hostsManager.changeRedirectIp(site, newIp);
            panel.updateRowIp(row, newIp);
            panel.setStatus("✔ " + site + " → " + newIp + " olarak güncellendi.", Theme.GREEN);
        } catch (Exception ex) {
            panel.setStatus("✖ Hata: " + ex.getMessage(), Theme.RED);
        }
    }

    private void loadBlockedSites() {
        try {
            Map<String, String> sites = hostsManager.loadBlockedSites();
            for (Map.Entry<String, String> entry : sites.entrySet()) {
                panel.addRow(entry.getKey(), entry.getValue());
            }
            if (!sites.isEmpty()) {
                panel.setStatus(sites.size() + " engellenen site yüklendi.", Theme.GREEN);
            }
        } catch (Exception ex) {
            panel.setStatus("!!!! hosts dosyası okunamadı: " + ex.getMessage(), Theme.ORANGE);
        }
    }
}