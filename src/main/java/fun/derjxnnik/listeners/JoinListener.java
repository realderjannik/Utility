package fun.derjxnnik.listeners;

import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.resourcepack.ResourcePackManager;
import fun.derjxnnik.utility.Utility;
import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

   private final Utility plugin;
   private final BackpackItemManager backpackItemManager;

   public JoinListener(Utility plugin, BackpackItemManager backpackItemManager) {
      this.plugin = plugin;
      this.backpackItemManager = backpackItemManager;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent e) {
      Player player = e.getPlayer();
      String pName = player.getName();
      e.setJoinMessage(Colors.GRAY + "[" + Colors.BOLD_GREEN + "+" + Colors.GRAY + "] " + Colors.GREEN + pName);
      ScoreboardManager.set(player);

      ResourcePackManager rp = plugin.getResourcePackManager();
      if (rp != null && rp.isReady()) {
         player.setResourcePack(rp.getUrl(), rp.getHash(), true,
                 Component.text(plugin.getConfig().getString("resource-pack.prompt",
                         "Bitte akzeptiere das Resource Pack!"), NamedTextColor.YELLOW));
      }

      plugin.getColorManager().loadPlayer(player);
      RankManager rankManager = plugin.getRankManager();
      rankManager.refreshPlayer(player);

      if (!rankManager.isLuckPermsPresent() && player.isOp()) {
         player.sendMessage(buildLuckPermsWarning());
         plugin.getLogger().warning("[Ranks] LuckPerms nicht gefunden! Prefixe werden nicht angezeigt. Download: https://luckperms.net/download");
      }

      Bukkit.getScheduler().runTaskLater(this.plugin, this::updateTablistForAll, 1L);
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.backpackItemManager.refreshBackpackItem(player), 10L);
   }

   private Component buildLuckPermsWarning() {
      return Component.text()
              .append(Component.text("[Utility] ", NamedTextColor.DARK_AQUA))
              .append(Component.text("LuckPerms ist nicht installiert! Prefixe werden nicht angezeigt. ", NamedTextColor.RED))
              .append(Component.text("[Download]")
                      .color(NamedTextColor.AQUA)
                      .decorate(TextDecoration.UNDERLINED)
                      .clickEvent(ClickEvent.openUrl("https://luckperms.net/download"))
                      .hoverEvent(HoverEvent.showText(Component.text("luckperms.net/download", NamedTextColor.YELLOW))))
              .build();
   }

   private void updateTablistForAll() {
      int online = Bukkit.getOnlinePlayers().size();
      int max = Bukkit.getMaxPlayers();

      Component header = Component.text("\n\n" + Icons.LOGO + "\n", NamedTextColor.WHITE);

      Component footer = Component.text("\n").append(
              MiniMessage.miniMessage().deserialize(
                      "<font:minecraft:small_caps><gradient:#00BFFF:#7B68EE>Online Players: " + online + "/" + max + "</gradient></font>"));

      for (Player p : Bukkit.getOnlinePlayers()) {
         p.sendPlayerListHeaderAndFooter(header, footer);
      }
   }
}
