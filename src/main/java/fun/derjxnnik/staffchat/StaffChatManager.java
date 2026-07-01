package fun.derjxnnik.staffchat;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
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

    /** Broadcasts to all staff. Pass null for console as sender. */
    public void broadcast(Player sender, String message) {
        String rankPrefix = "";
        String name;

        if (sender != null) {
            name = sender.getName();
            RankManager rm = Utility.getInstance().getRankManager();
            if (rm != null && rm.isAvailable()) {
                String prefix = rm.getPrefix(sender);
                if (!prefix.isEmpty()) {
                    rankPrefix = prefix + Colors.DARK_GRAY + " | ";
                }
            }
        } else {
            name = "Konsole";
        }

        String line = Colors.DARK_GRAY + "[" + Colors.DARK_RED + "STAFF" + Colors.DARK_GRAY + "] "
                + rankPrefix + Colors.WHITE + name
                + Colors.DARK_GRAY + ": " + Colors.GRAY + message;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("utility.staffchat")) {
                p.sendMessage(line);
            }
        }
        Bukkit.getConsoleSender().sendMessage(line);
    }
}
