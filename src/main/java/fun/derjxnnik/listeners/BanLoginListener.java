package fun.derjxnnik.listeners;

import fun.derjxnnik.ban.BanEntry;
import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.misc.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class BanLoginListener implements Listener {

    private final BanManager banManager;

    public BanLoginListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        BanEntry entry = banManager.getBan(uuid);
        if (entry == null) return;

        String kickMsg = entry.isPermanent()
                ? Messages.banKickNachrichtPermanent(entry.getReason(), entry.getBannerName())
                : Messages.banKickNachricht(entry.getReason(), entry.getExpiryString(), entry.getBannerName());

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMsg);
    }
}
