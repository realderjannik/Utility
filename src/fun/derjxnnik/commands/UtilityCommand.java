package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UtilityCommand implements CommandExecutor {
   private final Map descriptions = new HashMap();

   public UtilityCommand() {
      this.descriptions.put("backpack", "Open your personal backpack");
      this.descriptions.put("craft", "Open a crafting table");
      this.descriptions.put("day", "Set time to day");
      this.descriptions.put("ec", "Open your ender chest");
      this.descriptions.put("head", "Browse decorative heads");
      this.descriptions.put("hat", "Wear the item in your hand as a hat");
      this.descriptions.put("home", "Manage your homes");
      this.descriptions.put("info", "Show player information");
      this.descriptions.put("invsee", "View another player’s inventory");
      this.descriptions.put("lastdeath", "Teleport to your last death location");
      this.descriptions.put("ping", "Check your ping");
      this.descriptions.put("playtime", "Show your total playtime");
      this.descriptions.put("rename", "Rename the item in your hand");
      this.descriptions.put("setspawn", "Set the global spawn point");
      this.descriptions.put("spawn", "Teleport to the spawn point");
      this.descriptions.put("uuid", "Show your unique player UUID");
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      Utility plugin = Utility.getInstance();
      FileConfiguration config = plugin.getConfig();
      if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
         if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!p.isOp()) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "You don't have permission to use this command.");
               return true;
            }
         }

         long start = System.currentTimeMillis();
         plugin.reloadConfig();
         long duration = System.currentTimeMillis() - start;
         sender.sendMessage(Colors.PREFIX + Colors.GREEN + "Configuration reloaded successfully " + Colors.GRAY + "(" + duration + " ms).");
         return true;
      } else if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
         int page = 1;
         if (args.length == 2) {
            try {
               page = Integer.parseInt(args[1]);
            } catch (NumberFormatException var11) {
            }
         }

         this.sendHelpPage(sender, config, page);
         return true;
      } else {
         sender.sendMessage("");
         sender.sendMessage(Colors.DARK_AQUA + Colors.BOLD + "Utility Plugin " + Colors.GRAY + "by DerJxnnik");
         String var10001 = Colors.GRAY;
         sender.sendMessage(var10001 + "Version: " + Colors.YELLOW + plugin.getDescription().getVersion());
         sender.sendMessage("");
         sender.sendMessage(Colors.GRAY + "Use " + Colors.YELLOW + "/utility help" + Colors.GRAY + " to view all available commands.");
         sender.sendMessage("");
         return true;
      }
   }

   private void sendHelpPage(CommandSender sender, FileConfiguration config, int page) {
      List<String> enabled = new ArrayList();

      for(String key : this.descriptions.keySet()) {
         if (config.getBoolean("use-command." + key, true)) {
            enabled.add(key);
         }
      }

      enabled.sort(String.CASE_INSENSITIVE_ORDER);
      int perPage = 8;
      int totalPages = (int)Math.ceil((double)enabled.size() / (double)perPage);
      if (page < 1) {
         page = 1;
      }

      if (page > totalPages) {
         page = totalPages;
      }

      sender.sendMessage("");
      sender.sendMessage(Colors.DARK_AQUA + Colors.BOLD + "Utility Commands " + Colors.GRAY + "(Page " + Colors.YELLOW + page + Colors.GRAY + "/" + Colors.YELLOW + totalPages + Colors.GRAY + ")");
      sender.sendMessage("");
      int start = (page - 1) * perPage;
      int end = Math.min(start + perPage, enabled.size());

      for(int i = start; i < end; ++i) {
         String cmd = (String)enabled.get(i);
         String desc = (String)this.descriptions.getOrDefault(cmd, "No description available.");
         sender.sendMessage(Colors.DARK_AQUA + "• " + Colors.YELLOW + "/" + cmd + Colors.GRAY + " – " + Colors.GRAY + desc);
      }

      if (sender instanceof Player p) {
         if (totalPages > 1) {
            ComponentBuilder nav = new ComponentBuilder("");
            if (page > 1) {
               TextComponent prev = new TextComponent(Colors.GRAY + "[← Prev]");
               prev.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/utility help " + (page - 1)));
               prev.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(Colors.YELLOW + "Go to page " + (page - 1))).create()));
               nav.append(prev);
            }

            nav.append("  ");
            if (page < totalPages) {
               TextComponent next = new TextComponent(Colors.GRAY + "[Next →]");
               next.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/utility help " + (page + 1)));
               next.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(Colors.YELLOW + "Go to page " + (page + 1))).create()));
               nav.append(next);
            }

            p.spigot().sendMessage(nav.create());
         }
      }

      sender.sendMessage("");
   }
}
