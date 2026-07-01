package fun.derjxnnik.warn;

import fun.derjxnnik.ban.BanEntry;
import fun.derjxnnik.mute.MuteEntry;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarnManager {

    private final File warnFile;
    private FileConfiguration config;

    public WarnManager(File dataFolder) {
        this.warnFile = new File(dataFolder, "warns.yml");
        if (!warnFile.exists()) {
            try { warnFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(warnFile);
    }

    private void save() {
        try { config.save(warnFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addWarn(WarnEntry entry) {
        String path = "warns." + entry.getPlayerUuid();
        int next = config.getInt(path + ".count", 0) + 1;
        config.set(path + ".name",  entry.getPlayerName());
        config.set(path + ".count", next);
        config.set(path + ".entries." + next + ".reason",   entry.getReason());
        config.set(path + ".entries." + next + ".warnedBy", entry.getWarnedByName());
        config.set(path + ".entries." + next + ".warnedAt", entry.getWarnedAt());
        save();
    }

    public List<WarnEntry> getWarns(UUID uuid) {
        return getWarns(uuid.toString());
    }

    public List<WarnEntry> getWarns(String uuid) {
        List<WarnEntry> result = new ArrayList<>();
        String base = "warns." + uuid;
        if (!config.contains(base)) return result;
        String name = config.getString(base + ".name", "");
        ConfigurationSection section = config.getConfigurationSection(base + ".entries");
        if (section == null) return result;
        for (String key : section.getKeys(false)) {
            result.add(new WarnEntry(
                    name, uuid,
                    config.getString(base + ".entries." + key + ".reason",   ""),
                    config.getString(base + ".entries." + key + ".warnedBy", ""),
                    config.getLong(base + ".entries." + key + ".warnedAt",    0)
            ));
        }
        return result;
    }

    public int getWarnCount(UUID uuid) {
        return config.getInt("warns." + uuid + ".count", 0);
    }

    public void clearWarns(UUID uuid) {
        config.set("warns." + uuid, null);
        save();
    }

    /** Removes a single warn by 1-based display index. Returns true on success. */
    public boolean removeWarn(UUID uuid, int displayIndex) {
        String base = "warns." + uuid;
        ConfigurationSection section = config.getConfigurationSection(base + ".entries");
        if (section == null) return false;
        List<String> keys = new ArrayList<>(section.getKeys(false));
        if (displayIndex < 1 || displayIndex > keys.size()) return false;
        String key = keys.get(displayIndex - 1);
        config.set(base + ".entries." + key, null);
        int newCount = Math.max(0, config.getInt(base + ".count", 0) - 1);
        if (newCount == 0) {
            config.set(base, null);
        } else {
            config.set(base + ".count", newCount);
        }
        save();
        return true;
    }

    /** Checks thresholds and applies auto-mute/ban. Returns "mute", "ban", or null. */
    public String checkThresholds(UUID uuid, String playerName) {
        Utility plugin = Utility.getInstance();
        int count  = getWarnCount(uuid);
        int muteAt = plugin.getConfig().getInt("warns.threshold-mute", 3);
        int banAt  = plugin.getConfig().getInt("warns.threshold-ban",  5);

        if (count == banAt) {
            var bm = plugin.getBanManager();
            if (bm.getBan(uuid) == null) {
                long expires = System.currentTimeMillis() + 7L * 86_400_000L;
                bm.ban(new BanEntry(playerName, uuid.toString(),
                        "Zu viele Verwarnungen (" + count + ")",
                        "System", System.currentTimeMillis(), expires));
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.kick(net.kyori.adventure.text.Component.text(
                            fun.derjxnnik.misc.Messages.banKickNachricht(
                                    "Zu viele Verwarnungen", "7 Tage", "System")));
                }
                return "ban";
            }
        } else if (count == muteAt) {
            var mm = plugin.getMuteManager();
            if (mm.getMute(uuid) == null) {
                long expires = System.currentTimeMillis() + 24L * 60L * 60L * 1000L;
                mm.mute(new MuteEntry(playerName, uuid.toString(),
                        "Zu viele Verwarnungen (" + count + ")",
                        "System", System.currentTimeMillis(), expires));
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    target.sendMessage(fun.derjxnnik.misc.Messages.muteNachricht(
                            "Zu viele Verwarnungen", "24 Stunden", "System"));
                }
                return "mute";
            }
        }
        return null;
    }
}
