import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class SiteBlocker extends JFrame {

    private final SiteBlockerPanel panel;
    private final HostsManager     hostsManager;

    public SiteBlocker() {
        super("Site Blocker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(true);

        hostsManager = new HostsManager();
        panel        = new SiteBlockerPanel();

        // Eylem bağlama
        panel.setOnAdd(this::addSite);
        panel.setOnRemove(this::removeSite);

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
            panel.addRow(site);
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

    private void loadBlockedSites() {
        try {
            Set<String> sites = hostsManager.loadBlockedSites();
            for (String s : sites) panel.addRow(s);
            if (!sites.isEmpty()) {
                panel.setStatus(sites.size() + " engellenen site yüklendi.", Theme.GREEN);
            }
        } catch (Exception ex) {
            panel.setStatus("!!!! hosts dosyası okunamadı: " + ex.getMessage(), Theme.ORANGE);
        }
    }
}