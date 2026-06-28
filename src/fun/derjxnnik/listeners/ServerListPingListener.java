package fun.derjxnnik.listeners;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import fun.derjxnnik.utility.Utility;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerListPingListener implements Listener {
   @EventHandler
   public void onPing(PaperServerListPingEvent e) {
      FileConfiguration config = Utility.getInstance().getConfig();
      String name = config.getString("server.name", "SMP");
      String website = config.getString("server.website", "");

      List<PaperServerListPingEvent.ListedPlayerInfo> listed = e.getListedPlayers();
      listed.clear();
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §c" + name, UUID.randomUUID()));
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eVersion: §f1.21.10+", UUID.randomUUID()));
      if (!website.isEmpty()) {
         listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eWebsite: §f" + website, UUID.randomUUID()));
      }
      listed.add(new PaperServerListPingEvent.ListedPlayerInfo("§7» §eFound a bug? §f/bugreport", UUID.randomUUID()));
   }
}
