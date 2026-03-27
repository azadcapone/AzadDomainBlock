import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HostsManager {

    public static final String HOSTS_PATH  = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    public static final String REDIRECT_IP = "127.0.0.1";
    public static final String BLOCK_TAG   = "# [SiteBlocker]";

    public Map<String, String> loadBlockedSites() throws IOException {
        List<String> lines = readLines();
        Map<String, String> result = new LinkedHashMap<>();

        for (String line : lines) {
            if (line.contains(BLOCK_TAG)) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    String ip   = parts[0];
                    String site = parts[1].replaceAll("^www\\.", "");
                    result.putIfAbsent(site, ip);
                }
            }
        }
        return result;
    }

    public void blockSite(String site) throws IOException {
        blockSite(site, REDIRECT_IP);
    }

    public void blockSite(String site, String ip) throws IOException {
        List<String> lines = readLines();
        boolean found = lines.stream().anyMatch(l -> l.contains("\t" + site + "\t"));
        if (!found) {
            lines.add(buildEntry(ip, site));
            lines.add(buildEntry(ip, "www." + site));
            writeLines(lines);
        }
    }

    public void unblockSite(String site) throws IOException {
        List<String> lines = readLines();
        lines.removeIf(l ->
                l.contains("\t" + site + "\t") ||
                        l.contains("\t" + "www." + site + "\t"));
        writeLines(lines);
    }

    public void changeRedirectIp(String site, String newIp) throws IOException {
        List<String> lines = readLines();
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.contains(BLOCK_TAG) &&
                    (l.contains("\t" + site + "\t") || l.contains("\t" + "www." + site + "\t"))) {
                String[] parts = l.trim().split("\\s+");
                if (parts.length >= 2) {
                    lines.set(i, newIp + "\t" + parts[1] + "\t" + BLOCK_TAG);
                }
            }
        }
        writeLines(lines);
    }

    private String buildEntry(String ip, String host) {
        return ip + "\t" + host + "\t" + BLOCK_TAG;
    }

    private List<String> readLines() throws IOException {
        return new ArrayList<>(Files.readAllLines(Path.of(HOSTS_PATH)));
    }

    private void writeLines(List<String> lines) throws IOException {
        Files.write(Path.of(HOSTS_PATH), lines);
    }
}