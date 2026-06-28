package fun.derjxnnik.resourcepack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackListener implements Listener {

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
        PlayerResourcePackStatusEvent.Status status = e.getStatus();
        if (status == PlayerResourcePackStatusEvent.Status.DECLINED
                || status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD
                || status == PlayerResourcePackStatusEvent.Status.INVALID_URL) {
            e.getPlayer().kick(
                    Component.text("Du musst das Resource Pack akzeptieren, um auf diesem Server zu spielen!", NamedTextColor.RED)
            );
        }
    }
}
