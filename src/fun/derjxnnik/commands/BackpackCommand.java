package fun.derjxnnik.commands;

import fun.derjxnnik.backpack.BackpackManager;
import fun.derjxnnik.misc.Colors;
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
   private final int size;

   public BackpackCommand(BackpackManager manager, int size) {
      this.manager = manager;
      this.size = size;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.bp", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length == 0) {
            Inventory pBackpack = this.manager.getBackpack(p, this.size);
            p.openInventory(pBackpack);
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
            p.sendActionBar(Colors.GREEN + "Backpack open!");
            return true;
         } else {
            switch (args[0].toLowerCase()) {
               case "open":
                  return this.handleOpenCommand(p, args);
               case "help":
                  return this.showHelp(p);
               case "backup":
                  if (args.length < 2) {
                     sender.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /bp backup <Player_Name>");
                     return true;
                  } else {
                     Player t = Bukkit.getPlayerExact(args[1]);
                     if (t != null && t.isOnline()) {
                        Inventory targetBackpack = this.manager.getBackpack(t, this.size);
                        this.manager.saveBackpack(t, targetBackpack);
                        String var19 = Colors.PREFIX;
                        p.sendMessage(var19 + Colors.GREEN + "Backup saved for " + Colors.WHITE + t.getName());
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                        return true;
                     }

                     sender.sendMessage(Colors.PREFIX + Colors.RED + "Player " + Colors.WHITE + "'" + args[1] + "'" + Colors.RED + " is not online.");
                     return true;
                  }
               case "restore":
                  if (args.length < 2) {
                     p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /bp restore <from_uuid> <to_player>");
                     return true;
                  } else {
                     UUID uuid;
                     try {
                        uuid = UUID.fromString(args[1]);
                     } catch (IllegalArgumentException var17) {
                        p.sendMessage(Colors.PREFIX + Colors.RED + "Invalid UUID format.");
                        return true;
                     }

                     if (args.length < 3) {
                        p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /bp restore <from_uuid> <to_player>");
                        return true;
                     } else {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                        if (!target.hasPlayedBefore() && !target.isOnline()) {
                           p.sendMessage(Colors.PREFIX + Colors.RED + "Player is currently offline or has never played");
                           return true;
                        } else {
                           if (target instanceof Player) {
                              Player to = (Player)target;
                              if (to.isOnline()) {
                                 boolean success = this.manager.restoreBackpack(uuid, to);
                                 if (success) {
                                    Inventory updatedBackpack = this.manager.getBackpack(to, this.size);
                                    this.manager.saveBackpack(to, updatedBackpack);
                                    String var10001 = Colors.PREFIX;
                                    p.sendMessage(var10001 + Colors.GREEN + "Backpack restored to " + to.getName());
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                                 } else {
                                    p.sendMessage(Colors.PREFIX + Colors.RED + "Failed to restore backpack.");
                                 }

                                 return true;
                              }
                           }

                           p.sendMessage(Colors.PREFIX + Colors.RED + "Player must be online to restore backpack");
                           return true;
                        }
                     }
                  }
               default:
                  p.sendMessage(Colors.PREFIX + Colors.RED + "Unknown Subcommand -> Use /bp help for further information.");
                  return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   private boolean handleOpenCommand(Player sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /bp open <Player_Name>");
         return true;
      } else {
         String targetName = args[1];
         OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
         if (target != null && (target.isOnline() || target.hasPlayedBefore())) {
            if (target.isOnline()) {
               Player onlineTarget = target.getPlayer();
               Inventory backpack = this.manager.getBackpack(onlineTarget, this.size);
               if (backpack == null) {
                  String var10001 = Colors.PREFIX;
                  sender.sendMessage(var10001 + Colors.RED + "Couldn't load backpack for " + onlineTarget.getName());
                  return true;
               } else {
                  sender.openInventory(backpack);
                  sender.playSound(sender.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
                  return true;
               }
            } else {
               Inventory offlineBackpack = this.manager.getBackpackByUUID(target.getUniqueId(), this.size);
               if (offlineBackpack == null) {
                  sender.sendMessage(Colors.PREFIX + Colors.RED + "Couldn't load backpack for player '" + args[1] + "'");
                  return true;
               } else {
                  sender.openInventory(offlineBackpack);
                  sender.playSound(sender.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
                  return true;
               }
            }
         } else {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Player is currently offline or has never played");
            return true;
         }
      }
   }

   private boolean showHelp(Player p) {
      p.sendMessage(Colors.DARK_AQUA + "/bp" + Colors.GRAY + ": " + Colors.WHITE + "Open your backpack\n" + Colors.DARK_AQUA + "/bp open <name>" + Colors.GRAY + ": " + Colors.WHITE + "Open another player's backpack\n" + Colors.DARK_AQUA + "/bp help" + Colors.GRAY + ": " + Colors.WHITE + "Show all subcommands\n" + Colors.DARK_AQUA + "/bp backup <name>" + Colors.GRAY + ": " + Colors.WHITE + "Save a backup of the given player\n" + Colors.DARK_AQUA + "/bp restore <uuid> <name>" + Colors.GRAY + ": " + Colors.WHITE + "Restore the backpack of the given player");
      return true;
   }
}
