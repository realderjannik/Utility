package fun.derjxnnik.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import java.util.List;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {
   @EventHandler
   public void onPing(PaperServerListPingEvent e) {
      List<PaperServerListPingEvent.ListedPlayerInfo> listed = e.getListedPlayers();
      listed.clear();
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §cDerJxnnik§f.§cFUN", UUID.randomUUID()));
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eVersion: §f1.21.10+", UUID.randomUUID()));
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eWebsite: §fDerJxnnik§f.fun", UUID.randomUUID()));
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eFound a bug? §f/bugreport", UUID.randomUUID()));
   }
}
