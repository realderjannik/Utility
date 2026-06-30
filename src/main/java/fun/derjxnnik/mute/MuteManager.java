package fun.derjxnnik.mute;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager {

    private final File muteFile;
    private FileConfiguration config;

    public MuteManager(File dataFolder) {
        this.muteFile = new File(dataFolder, "mutes.yml");
        if (!muteFile.exists()) {
            try { muteFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(muteFile);
    }

    private void save() {
        try { config.save(muteFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void mute(MuteEntry entry) {
        String path = "mutes." + entry.getPlayerUuid();
        config.set(path + ".name",       entry.getPlayerName());
        config.set(path + ".reason",     entry.getReason());
        config.set(path + ".muter",      entry.getMuterName());
        config.set(path + ".muted-at",   entry.getMutedAt());
        config.set(path + ".expires-at", entry.getExpiresAt());
        save();
    }

    public void unmute(String playerUuid) {
        config.set("mutes." + playerUuid, null);
        save();
    }

    public MuteEntry getMute(UUID uuid) {
        return getMute(uuid.toString());
    }

    public MuteEntry getMute(String uuid) {
        String path = "mutes." + uuid;
        if (!config.contains(path)) return null;
        MuteEntry entry = new MuteEntry(
                config.getString(path + ".name",       ""),
                uuid,
                config.getString(path + ".reason",     ""),
                config.getString(path + ".muter",      ""),
                config.getLong(path + ".muted-at",      0),
                config.getLong(path + ".expires-at",   -1)
        );
        if (entry.isExpired()) {
            unmute(uuid);
            return null;
        }
        return entry;
    }

    public MuteEntry getMuteByName(String name) {
        if (!config.contains("mutes")) return null;
        for (String uuid : config.getConfigurationSection("mutes").getKeys(false)) {
            if (name.equalsIgnoreCase(config.getString("mutes." + uuid + ".name", ""))) {
                return getMute(uuid);
            }
        }
        return null;
    }

    public boolean isMuted(UUID uuid) {
        return getMute(uuid) != null;
    }

    public List<String> getMutedNames() {
        List<String> names = new ArrayList<>();
        if (!config.contains("mutes")) return names;
        for (String uuid : config.getConfigurationSection("mutes").getKeys(false)) {
            MuteEntry entry = getMute(uuid);
            if (entry != null) names.add(entry.getPlayerName());
        }
        return names;
    }
}
