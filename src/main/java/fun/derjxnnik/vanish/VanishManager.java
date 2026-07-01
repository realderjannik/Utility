package fun.derjxnnik.vanish;

import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final Set<UUID> vanished = new HashSet<>();

    /** Toggles vanish for the player. Returns true if now vanished, false if revealed. */
    public boolean toggle(Player player) {
        if (vanished.contains(player.getUniqueId())) {
            unvanish(player);
            return false;
        }
        vanish(player);
        return true;
    }

    public void vanish(Player player) {
        vanished.add(player.getUniqueId());
        Utility plugin = Utility.getInstance();
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (!observer.hasPermission("utility.vanish")) {
                observer.hidePlayer(plugin, player);
            }
        }
    }

    public void unvanish(Player player) {
        vanished.remove(player.getUniqueId());
        Utility plugin = Utility.getInstance();
        for (Player observer : Bukkit.getOnlinePlayers()) {
            observer.showPlayer(plugin, player);
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid);
    }

    public void remove(UUID uuid) {
        vanished.remove(uuid);
    }

    /** Applies current vanish state to a newly joining player. */
    public void applyToNewJoiner(Player newPlayer) {
        Utility plugin = Utility.getInstance();
        boolean newIsStaff = newPlayer.hasPermission("utility.vanish");
        for (UUID vid : vanished) {
            Player vPlayer = Bukkit.getPlayer(vid);
            if (vPlayer != null && !newIsStaff) {
                newPlayer.hidePlayer(plugin, vPlayer);
            }
        }
    }

    public Set<UUID> getVanished() {
        return Collections.unmodifiableSet(vanished);
    }
}
