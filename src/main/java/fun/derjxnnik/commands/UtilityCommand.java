package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.rank.RankPermissionSetup;
import fun.derjxnnik.utility.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UtilityCommand implements CommandExecutor {
   private final Map<String, String> descriptions = new HashMap<>();

   public UtilityCommand() {
      descriptions.put("backpack", "Open your personal backpack");
      descriptions.put("craft", "Open a crafting table");
      descriptions.put("day", "Set time to day");
      descriptions.put("ec", "Open your ender chest");
      descriptions.put("head", "Browse decorative heads");
      descriptions.put("hat", "Wear the item in your hand as a hat");
      descriptions.put("home", "Manage your homes");
      descriptions.put("info", "Show player information");
      descriptions.put("invsee", "View another player's inventory");
      descriptions.put("lastdeath", "Teleport to your last death location");
      descriptions.put("ping", "Check your ping");
      descriptions.put("playtime", "Show your total playtime");
      descriptions.put("rename", "Rename the item in your hand");
      descriptions.put("setspawn", "Set the global spawn point");
      descriptions.put("spawn", "Teleport to the spawn point");
      descriptions.put("uuid", "Show your unique player UUID");
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      Utility plugin = Utility.getInstance();
      FileConfiguration config = plugin.getConfig();

      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         if (sender instanceof Player p && !p.isOp()) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "You don't have permission to use this command.");
            return true;
         }
         long start = System.currentTimeMillis();
         plugin.reloadConfig();
         long duration = System.currentTimeMillis() - start;
         sender.sendMessage(Colors.PREFIX + Colors.GREEN + "Configuration reloaded successfully " + Colors.GRAY + "(" + duration + " ms).");
         return true;
      }

      if (args.length == 1 && args[0].equalsIgnoreCase("perms")) {
         if (sender instanceof Player p && !p.isOp()) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Du hast keine Berechtigung für diesen Befehl.");
            return true;
         }
         RankManager rm = plugin.getRankManager();
         if (!rm.isLuckPermsPresent()) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "LuckPerms ist nicht installiert.");
            return true;
         }
         sender.sendMessage(Colors.PREFIX + Colors.YELLOW + "Wende Permissions auf LP-Gruppen an...");
         new RankPermissionSetup(rm.getLuckPerms()).apply();
         sender.sendMessage(Colors.PREFIX + Colors.GREEN + "Fertig! Alle Rang-Permissions wurden gesetzt.");
         return true;
      }

      if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
         int page = 1;
         if (args.length == 2) {
            try {
               page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
         }
         sendHelpPage(sender, config, page);
         return true;
      }

      sender.sendMessage("");
      sender.sendMessage(Colors.DARK_AQUA + Colors.BOLD + "Utility Plugin " + Colors.GRAY + "by DerJxnnik");
      sender.sendMessage(Colors.GRAY + "Version: " + Colors.YELLOW + plugin.getDescription().getVersion());
      sender.sendMessage("");
      sender.sendMessage(Colors.GRAY + "Use " + Colors.YELLOW + "/utility help" + Colors.GRAY + " to view all available commands.");
      sender.sendMessage("");
      return true;
   }

   private void sendHelpPage(CommandSender sender, FileConfiguration config, int page) {
      List<String> enabled = new ArrayList<>();
      for (String key : descriptions.keySet()) {
         if (config.getBoolean("use-command." + key, true)) {
            enabled.add(key);
         }
      }
      enabled.sort(String.CASE_INSENSITIVE_ORDER);

      int perPage = 8;
      int totalPages = (int) Math.ceil((double) enabled.size() / perPage);
      page = Math.max(1, Math.min(page, totalPages));

      sender.sendMessage("");
      sender.sendMessage(Colors.DARK_AQUA + Colors.BOLD + "Utility Commands " + Colors.GRAY + "(Page " + Colors.YELLOW + page + Colors.GRAY + "/" + Colors.YELLOW + totalPages + Colors.GRAY + ")");
      sender.sendMessage("");

      int start = (page - 1) * perPage;
      int end = Math.min(start + perPage, enabled.size());
      for (int i = start; i < end; i++) {
         String command = enabled.get(i);
         String desc = descriptions.getOrDefault(command, "No description available.");
         sender.sendMessage(Colors.DARK_AQUA + "• " + Colors.YELLOW + "/" + command + Colors.GRAY + " – " + desc);
      }

      if (sender instanceof Player p && totalPages > 1) {
         Component nav = Component.empty();
         if (page > 1) {
            nav = nav.append(Component.text("[← Prev]")
               .color(NamedTextColor.GRAY)
               .clickEvent(ClickEvent.runCommand("/utility help " + (page - 1)))
               .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (page - 1)).color(NamedTextColor.YELLOW))));
         }
         if (page > 1 && page < totalPages) {
            nav = nav.append(Component.text("  "));
         }
         if (page < totalPages) {
            nav = nav.append(Component.text("[Next →]")
               .color(NamedTextColor.GRAY)
               .clickEvent(ClickEvent.runCommand("/utility help " + (page + 1)))
               .hoverEvent(HoverEvent.showText(Component.text("Go to page " + (page + 1)).color(NamedTextColor.YELLOW))));
         }
         p.sendMessage(nav);
      }

      sender.sendMessage("");
   }
}
