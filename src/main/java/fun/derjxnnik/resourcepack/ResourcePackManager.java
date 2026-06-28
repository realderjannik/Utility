package fun.derjxnnik.resourcepack;

import com.sun.net.httpserver.HttpServer;
import fun.derjxnnik.misc.LogUtil;
import fun.derjxnnik.utility.Utility;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackManager {

    private final File packDir;
    private final File zipFile;
    private HttpServer httpServer;
    private String packUrl;
    private String packHash;

    public ResourcePackManager(File dataFolder) {
        this.packDir = new File(dataFolder, "resourcepack");
        this.zipFile = new File(dataFolder, "resourcepack.zip");
    }

    public void start() {
        if (!packDir.exists() || packDir.list() == null || packDir.list().length == 0) {
            LogUtil.info("[ResourcePack] Kein resourcepack/ Ordner in " + packDir.getParent() + " gefunden. Pack-Server deaktiviert.");
            return;
        }

        try {
            zipPack();
            this.packHash = sha1(zipFile);
            startHttpServer();
        } catch (Exception e) {
            LogUtil.warn("[ResourcePack] Fehler beim Starten: " + e.getMessage());
        }
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            LogUtil.info("[ResourcePack] HTTP-Server gestoppt.");
        }
    }

    private void zipPack() throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipDirectory(packDir, packDir, zos);
        }
    }

    private void zipDirectory(File root, File dir, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(root, file, zos);
            } else {
                String entryName = root.toURI().relativize(file.toURI()).getPath();
                zos.putNextEntry(new ZipEntry(entryName));
                Files.copy(file.toPath(), zos);
                zos.closeEntry();
            }
        }
    }

    private String sha1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) digest.update(buf, 0, n);
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : digest.digest()) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void startHttpServer() throws IOException {
        Utility plugin = Utility.getInstance();
        int port = plugin.getConfig().getInt("resource-pack.port", 8080);
        String serverIp = plugin.getConfig().getString("resource-pack.server-ip", "").trim();

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/resourcepack.zip", exchange -> {
            byte[] data = Files.readAllBytes(zipFile.toPath());
            exchange.getResponseHeaders().add("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        });
        httpServer.start();

        String host = serverIp.isEmpty() ? "DEINE-SERVER-IP" : serverIp;
        packUrl = "http://" + host + ":" + port + "/resourcepack.zip";

        LogUtil.info("[ResourcePack] HTTP-Server gestartet auf Port " + port);
        LogUtil.info("[ResourcePack] URL: " + packUrl);
        LogUtil.info("[ResourcePack] SHA-1: " + packHash);

        if (serverIp.isEmpty()) {
            LogUtil.warn("[ResourcePack] Keine Server-IP konfiguriert! Bitte 'resource-pack.server-ip' in config.yml setzen.");
        }
    }

    public boolean isReady() {
        return packUrl != null && packHash != null
                && !packUrl.contains("DEINE-SERVER-IP");
    }

    public String getUrl() { return packUrl; }
    public String getHash() { return packHash; }
}
