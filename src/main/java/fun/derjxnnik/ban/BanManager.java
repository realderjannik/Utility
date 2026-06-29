package fun.derjxnnik.ban;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager {

    private final File banFile;
    private FileConfiguration config;

    public BanManager(File dataFolder) {
        this.banFile = new File(dataFolder, "bans.yml");
        if (!banFile.exists()) {
            try { banFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(banFile);
    }

    private void save() {
        try { config.save(banFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void ban(BanEntry entry) {
        String path = "bans." + entry.getPlayerUuid();
        config.set(path + ".name",       entry.getPlayerName());
        config.set(path + ".reason",     entry.getReason());
        config.set(path + ".banner",     entry.getBannerName());
        config.set(path + ".banned-at",  entry.getBannedAt());
        config.set(path + ".expires-at", entry.getExpiresAt());
        save();
    }

    public void unban(String playerUuid) {
        config.set("bans." + playerUuid, null);
        save();
    }

    public BanEntry getBan(UUID uuid) {
        return getBan(uuid.toString());
    }

    public BanEntry getBan(String uuid) {
        String path = "bans." + uuid;
        if (!config.contains(path)) return null;
        BanEntry entry = new BanEntry(
                config.getString(path + ".name",   ""),
                uuid,
                config.getString(path + ".reason", ""),
                config.getString(path + ".banner", ""),
                config.getLong(path + ".banned-at", 0),
                config.getLong(path + ".expires-at", -1)
        );
        if (entry.isExpired()) {
            unban(uuid);
            return null;
        }
        return entry;
    }

    public BanEntry getBanByName(String name) {
        if (!config.contains("bans")) return null;
        for (String uuid : config.getConfigurationSection("bans").getKeys(false)) {
            if (name.equalsIgnoreCase(config.getString("bans." + uuid + ".name", ""))) {
                return getBan(uuid);
            }
        }
        return null;
    }

    public boolean isBanned(UUID uuid) {
        return getBan(uuid) != null;
    }

    public List<String> getBannedNames() {
        List<String> names = new ArrayList<>();
        if (!config.contains("bans")) return names;
        for (String uuid : config.getConfigurationSection("bans").getKeys(false)) {
            BanEntry entry = getBan(uuid);
            if (entry != null) names.add(entry.getPlayerName());
        }
        return names;
    }

    /** Parses a time string like "1h", "7d". Returns -1 for permanent. Throws IllegalArgumentException on bad format. */
    public static long parseTime(String time) {
        if (time == null || time.equalsIgnoreCase("permanent") || time.equalsIgnoreCase("perm")) {
            return -1;
        }
        try {
            char unit = Character.toLowerCase(time.charAt(time.length() - 1));
            long value = Long.parseLong(time.substring(0, time.length() - 1));
            if (value <= 0) throw new IllegalArgumentException();
            long multiplier = switch (unit) {
                case 'h' -> 3_600_000L;
                case 'd' -> 86_400_000L;
                default  -> throw new IllegalArgumentException();
            };
            return System.currentTimeMillis() + (value * multiplier);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültiges Format: " + time);
        }
    }

    /** Returns a human-readable duration until the ban expires, e.g. "7 Tag(e)" or "3 Stunde(n)". */
    public static String formatDuration(long expiresAt) {
        if (expiresAt == -1) return "Permanent";
        long diff = expiresAt - System.currentTimeMillis();
        if (diff <= 0) return "Abgelaufen";
        long hours = diff / 3_600_000L;
        long days = hours / 24;
        if (days > 0) return days + " Tag(e)";
        return Math.max(1, hours) + " Stunde(n)";
    }
}
