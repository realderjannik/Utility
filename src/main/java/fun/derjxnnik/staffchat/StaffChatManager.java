package fun.derjxnnik.staffchat;

import fun.derjxnnik.misc.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffChatManager {

    private final Set<UUID> toggled = new HashSet<>();

    /** Toggles staff-chat mode. Returns true if now active, false if deactivated. */
    public boolean toggle(UUID uuid) {
        if (toggled.remove(uuid)) return false;
        toggled.add(uuid);
        return true;
    }

    public boolean isToggled(UUID uuid) {
        return toggled.contains(uuid);
    }

    public void remove(UUID uuid) {
        toggled.remove(uuid);
    }

    public void broadcast(String senderName, String message) {
        String line = Colors.DARK_GRAY + "[" + Colors.DARK_RED + "STAFF" + Colors.DARK_GRAY + "] "
                + Colors.GRAY + senderName + Colors.DARK_GRAY + ": " + Colors.WHITE + message;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("utility.staffchat")) {
                p.sendMessage(line);
            }
        }
        Bukkit.getConsoleSender().sendMessage(line);
    }
}
