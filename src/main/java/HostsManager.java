import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Windows hosts dosyasını okuma ve yazma işlemlerini yönetir.
 * UI'dan tamamen bağımsızdır; doğrudan test edilebilir.
 */
public class HostsManager {

    public static final String HOSTS_PATH   = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    public static final String REDIRECT_IP  = "127.0.0.1";
    public static final String BLOCK_TAG    = "# [SiteBlocker]";

    /**
     * hosts dosyasından SiteBlocker tarafından engellenen siteleri okur.
     * www. öneki çıkarılmış, tekil, sıralı bir küme döner.
     */
    public Set<String> loadBlockedSites() throws IOException {
        List<String> lines = readLines();
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
        return seen;
    }




    // Verilen siteyi hosts dosyasına ekler (hem "site" hem "www.site" için).

    public void blockSite(String site) throws IOException {
        List<String> lines = readLines();

        boolean found = lines.stream().anyMatch(l -> l.contains("\t" + site + "\t"));
        if (!found) {
            lines.add(buildEntry(site));
            lines.add(buildEntry("www." + site));
            writeLines(lines);
        }
    }

    // Siteyi blocklamayı bırak
    public void unblockSite(String site) throws IOException {
        List<String> lines = readLines();
        lines.removeIf(l ->
                l.contains("\t" + site + "\t") ||
                        l.contains("\t" + "www." + site + "\t"));
        writeLines(lines);
    }

    // Yardımcılar

    private String buildEntry(String host) {
        return REDIRECT_IP + "\t" + host + "\t" + BLOCK_TAG;
    }

    private List<String> readLines() throws IOException {
        return new ArrayList<>(Files.readAllLines(Path.of(HOSTS_PATH)));
    }

    private void writeLines(List<String> lines) throws IOException {
        Files.write(Path.of(HOSTS_PATH), lines);
    }
}