package fun.derjxnnik.chat;

import fun.derjxnnik.settings.SettingsManager;
import org.bukkit.entity.Player;

import java.util.*;

public class ColorManager {

    private final SettingsManager settingsManager;
    private final Map<UUID, String> defaultColors = new HashMap<>();
    private final Map<UUID, Set<String>> activeFormats = new HashMap<>();

    public ColorManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String color = settingsManager.getChatColor(player);
        defaultColors.put(uuid, color);
        List<String> formats = settingsManager.getChatFormats(player);
        if (formats.isEmpty()) {
            activeFormats.remove(uuid);
        } else {
            activeFormats.put(uuid, new LinkedHashSet<>(formats));
        }
    }

    public void setColor(Player player, String sectionCode) {
        defaultColors.put(player.getUniqueId(), sectionCode);
        settingsManager.setChatColor(player, sectionCode);
    }

    /** Toggles a format. Returns true if now active, false if deactivated. */
    public boolean toggleFormat(Player player, String sectionCode) {
        UUID uuid = player.getUniqueId();
        Set<String> formats = activeFormats.computeIfAbsent(uuid, k -> new LinkedHashSet<>());
        boolean nowActive;
        if (formats.contains(sectionCode)) {
            formats.remove(sectionCode);
            if (formats.isEmpty()) activeFormats.remove(uuid);
            nowActive = false;
        } else {
            formats.add(sectionCode);
            nowActive = true;
        }
        settingsManager.setChatFormats(player, new ArrayList<>(activeFormats.getOrDefault(uuid, Collections.emptySet())));
        return nowActive;
    }

    public void reset(Player player) {
        UUID uuid = player.getUniqueId();
        defaultColors.remove(uuid);
        activeFormats.remove(uuid);
        settingsManager.resetChatStyle(player);
    }

    public String getDefaultColor(UUID uuid) {
        return defaultColors.getOrDefault(uuid, "");
    }

    public Set<String> getActiveFormats(UUID uuid) {
        return activeFormats.getOrDefault(uuid, Collections.emptySet());
    }

    /** Returns the §-coded prefix to prepend to the player's chat message. */
    public String buildPrefix(UUID uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(getDefaultColor(uuid));           // color first — color codes reset formatting
        for (String fmt : getActiveFormats(uuid)) sb.append(fmt);
        return sb.toString();
    }

    public boolean hasAnyDefault(UUID uuid) {
        return !getDefaultColor(uuid).isEmpty() || !getActiveFormats(uuid).isEmpty();
    }
}
