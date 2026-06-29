package fun.derjxnnik.commands;

import fun.derjxnnik.backpack.BackpackManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BackpackCommand implements CommandExecutor {

   private final BackpackManager manager;

   public BackpackCommand(BackpackManager manager) {
      this.manager = manager;
   }

   private int getSlotsForPlayer(Player player) {
      FileConfiguration config = Utility.getInstance().getConfig();
      RankManager rm = Utility.getInstance().getRankManager();
      if (rm != null && rm.isAvailable()) {
         String group = rm.getPlayerGroup(player);
         int rankSlots = config.getInt("backpack.rank-slots." + group, Integer.MIN_VALUE);
         if (rankSlots != Integer.MIN_VALUE) return rankSlots;
      }
      return config.getInt("backpack.slots", 9);
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player p)) {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }

      FileConfiguration config = Utility.getInstance().getConfig();
      if (!config.getBoolean("use-command.bp", true)) {
         p.sendMessage(Colors.DISABLED);
         return true;
      }

      if (args.length == 0) {
         int slots = getSlotsForPlayer(p);
         Inventory pBackpack = manager.getBackpack(p, slots);
         p.openInventory(pBackpack);
         p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
         p.sendActionBar(Messages.BP_GEOEFFNET);
         return true;
      }

      switch (args[0].toLowerCase()) {
         case "open":
            return handleOpenCommand(p, args);
         case "help":
            return showHelp(p);
         case "backup":
            if (args.length < 2) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /bp backup <Spielername>");
               return true;
            }
            Player t = Bukkit.getPlayerExact(args[1]);
            if (t != null && t.isOnline()) {
               int tSlots = getSlotsForPlayer(t);
               Inventory targetBackpack = manager.getBackpack(t, tSlots);
               manager.saveBackpack(t, targetBackpack);
               p.sendMessage(Colors.PREFIX + Colors.GREEN + "Backup gespeichert für " + Colors.WHITE + t.getName());
               p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            } else {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Spieler " + Colors.WHITE + "'" + args[1] + "'" + Colors.RED + " ist nicht online.");
            }
            return true;
         case "restore":
            if (args.length < 3) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /bp restore <uuid> <Spieler>");
               return true;
            }
            UUID uuid;
            try {
               uuid = UUID.fromString(args[1]);
            } catch (IllegalArgumentException e) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Ungültiges UUID-Format.");
               return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Spieler ist offline oder hat noch nie gespielt.");
               return true;
            }
            if (target instanceof Player to && to.isOnline()) {
               boolean success = manager.restoreBackpack(uuid, to);
               if (success) {
                  int toSlots = getSlotsForPlayer(to);
                  manager.saveBackpack(to, manager.getBackpack(to, toSlots));
                  p.sendMessage(Colors.PREFIX + Colors.GREEN + "Rucksack wiederhergestellt für " + to.getName());
                  p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
               } else {
                  p.sendMessage(Colors.PREFIX + Colors.RED + "Wiederherstellung fehlgeschlagen.");
               }
            } else {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Spieler muss online sein für die Wiederherstellung.");
            }
            return true;
         default:
            p.sendMessage(Colors.PREFIX + Colors.RED + "Unbekannter Unterbefehl → Nutze /bp help für Hilfe.");
            return true;
      }
   }

   private boolean handleOpenCommand(Player sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /bp open <Spielername>");
         return true;
      }
      String targetName = args[1];
      @SuppressWarnings("deprecation")
      OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
      if (target == null || (!target.isOnline() && !target.hasPlayedBefore())) {
         sender.sendMessage(Colors.PREFIX + Colors.RED + "Spieler ist offline oder hat noch nie gespielt.");
         return true;
      }
      if (target.isOnline()) {
         Player onlineTarget = target.getPlayer();
         int slots = getSlotsForPlayer(onlineTarget);
         Inventory backpack = manager.getBackpack(onlineTarget, slots);
         sender.openInventory(backpack);
         sender.playSound(sender.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
      } else {
         int slots = Utility.getInstance().getConfig().getInt("backpack.slots", 9);
         Inventory offlineBackpack = manager.getBackpackByUUID(target.getUniqueId(), slots);
         if (offlineBackpack == null) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Kein Rucksack für Spieler '" + args[1] + "' gefunden.");
            return true;
         }
         sender.openInventory(offlineBackpack);
         sender.playSound(sender.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
      }
      return true;
   }

   private boolean showHelp(Player p) {
      FileConfiguration config = Utility.getInstance().getConfig();
      int mySlots = getSlotsForPlayer(p);
      p.sendMessage(Colors.DARK_AQUA + "/bp" + Colors.GRAY + ": " + Colors.WHITE + "Rucksack öffnen\n"
              + Colors.DARK_AQUA + "/bp open <Name>" + Colors.GRAY + ": " + Colors.WHITE + "Rucksack eines anderen Spielers öffnen\n"
              + Colors.DARK_AQUA + "/bp help" + Colors.GRAY + ": " + Colors.WHITE + "Alle Unterbefehle anzeigen\n"
              + Colors.DARK_AQUA + "/bp backup <Name>" + Colors.GRAY + ": " + Colors.WHITE + "Backup des Rucksacks speichern\n"
              + Colors.DARK_AQUA + "/bp restore <uuid> <Name>" + Colors.GRAY + ": " + Colors.WHITE + "Rucksack wiederherstellen\n"
              + Colors.GRAY + "Deine Slots: " + Colors.YELLOW + mySlots);
      return true;
   }
}
