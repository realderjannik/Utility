package fun.derjxnnik.listeners;

import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.resourcepack.ResourcePackManager;
import fun.derjxnnik.utility.Utility;
import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import net.kyori.adventure.key.Key;
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

      // Apply current vanish state: hide vanished players from this joiner (if not staff)
      fun.derjxnnik.vanish.VanishManager vm = plugin.getVanishManager();
      if (vm != null) vm.applyToNewJoiner(player);
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

      boolean useTex = plugin.getConfig().getBoolean("scoreboard.use-textures", false);
      Component headerInner;
      if (useTex) {
         headerInner = Component.text(fun.derjxnnik.misc.Icons.HEADER_GENERAL);
      } else {
         String headerText = plugin.getConfig().getString("scoreboard.header.text", "dasumc.de");
         java.util.List<String> grad = plugin.getConfig().getStringList("scoreboard.header.gradient");
         String from = grad.size() >= 1 ? grad.get(0) : "#FF44FF";
         String to   = grad.size() >= 2 ? grad.get(1) : "#AA00CC";
         headerInner = MiniMessage.miniMessage().deserialize(
                 "<font:minecraft:small_caps><gradient:" + from + ":" + to + ">"
                         + headerText + "</gradient></font>");
      }
      Component header = Component.text("\n").append(headerInner).append(Component.text("\n"));

      Key sc = Key.key("minecraft", "small_caps");
      Component footerDiscord = Component.text(Icons.LABEL_DISCORD + " ", NamedTextColor.WHITE)
              .append(Component.text("dasumc.de/discord", NamedTextColor.GRAY).font(sc));
      Component footerTwitch = Component.text(Icons.LABEL_TWITCH + " ", NamedTextColor.WHITE)
              .append(Component.text("dasumc.de/twitch", NamedTextColor.GRAY).font(sc));
      Component footerWebsite = Component.text(Icons.LABEL_WEBSITE + " ", NamedTextColor.WHITE)
              .append(Component.text("dasumc.de", NamedTextColor.GRAY).font(sc));
      Component footer = Component.text("\n")
              .append(footerDiscord).append(Component.newline())
              .append(Component.newline())
              .append(footerTwitch).append(Component.newline())
              .append(Component.newline())
              .append(footerWebsite).append(Component.text("\n"));

      for (Player p : Bukkit.getOnlinePlayers()) {
         p.sendPlayerListHeaderAndFooter(header, footer);
      }
   }
}
