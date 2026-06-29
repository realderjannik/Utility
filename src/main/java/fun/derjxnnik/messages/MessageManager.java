package fun.derjxnnik.messages;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {

    private final Map<UUID, UUID> lastPartner = new HashMap<>();

    public void setLastPartner(Player sender, Player receiver) {
        lastPartner.put(sender.getUniqueId(), receiver.getUniqueId());
        lastPartner.put(receiver.getUniqueId(), sender.getUniqueId());
    }

    public UUID getLastPartner(UUID playerUuid) {
        return lastPartner.get(playerUuid);
    }

    public void removePlayer(UUID playerUuid) {
        lastPartner.values().removeIf(v -> v.equals(playerUuid));
        lastPartner.remove(playerUuid);
    }
}
