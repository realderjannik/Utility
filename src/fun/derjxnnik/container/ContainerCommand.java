package fun.derjxnnik.container;

import fun.derjxnnik.misc.Colors;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContainerCommand implements CommandExecutor {
   private final LockManager lockManager;
   private final Map pendingActions = new HashMap();
   private final Map transferTargets = new HashMap();

   public ContainerCommand(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (args.length == 0) {
            this.sendHelp(player);
            return true;
         } else {
            switch (args[0].toLowerCase()) {
               case "lock" -> this.handleLock(player);
               case "unlock" -> this.handleUnlock(player);
               case "trust" -> this.handleTrust(player, args);
               case "untrust" -> this.handleUntrust(player, args);
               case "transfer" -> this.handleTransfer(player, args);
               case "info" -> this.handleInfo(player);
               default -> player.sendMessage(Colors.PREFIX + Colors.RED + "Unknown subcommand. Use /" + label + " for help.");
            }

            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   private void handleLock(Player player) {
      this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.LOCK);
      player.sendMessage(Colors.PREFIX + Colors.GRAY + "Lock mode activated. " + Colors.YELLOW + "Right-click" + Colors.GRAY + " a container to secure it.");
   }

   private void handleUnlock(Player player) {
      this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.UNLOCK);
      player.sendMessage(Colors.PREFIX + Colors.GRAY + "Unlock mode activated. " + Colors.YELLOW + "Right-click" + Colors.GRAY + " a locked container to unlock it.");
   }

   private void handleTrust(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Usage: " + Colors.YELLOW + "/container trust <player>");
      } else {
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Please " + Colors.YELLOW + "right-click" + Colors.GRAY + " a locked container to trust " + Colors.YELLOW + args[1] + Colors.GRAY + ".");
         this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.TRUST);
         this.transferTargets.put(player.getUniqueId(), args[1]);
      }
   }

   private void handleUntrust(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Usage: " + Colors.YELLOW + "/container untrust <player>");
      } else {
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Please " + Colors.YELLOW + "right-click" + Colors.GRAY + " a locked container to untrust " + Colors.YELLOW + args[1] + Colors.GRAY + ".");
         this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.UNTRUST);
         this.transferTargets.put(player.getUniqueId(), args[1]);
      }
   }

   private void handleTransfer(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Usage: " + Colors.YELLOW + "/container transfer <player>");
      } else {
         this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.TRANSFER);
         this.transferTargets.put(player.getUniqueId(), args[1]);
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Transfer mode activated. " + Colors.YELLOW + "Right-click" + Colors.GRAY + " a container to transfer ownership to " + Colors.YELLOW + args[1] + Colors.GRAY + ".");
      }
   }

   private void handleInfo(Player player) {
      this.pendingActions.put(player.getUniqueId(), ContainerCommand.LockAction.INFO);
      player.sendMessage(Colors.PREFIX + Colors.GRAY + "Right-click a container to view its information.");
   }

   private void sendHelp(Player player) {
      player.sendMessage(Colors.PREFIX + Colors.GRAY + "Container commands:");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container lock " + Colors.GRAY + "→ Lock a container.");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container unlock " + Colors.GRAY + "→ Unlock a container.");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container trust <player> " + Colors.GRAY + "→ Allow player access.");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container untrust <player> " + Colors.GRAY + "→ Revoke player access.");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container transfer <player> " + Colors.GRAY + "→ Transfer ownership.");
      player.sendMessage(Colors.GRAY + " - " + Colors.YELLOW + "/container info " + Colors.GRAY + "→ Show container information.");
   }

   public boolean hasPendingAction(UUID uuid) {
      return this.pendingActions.containsKey(uuid);
   }

   public LockAction getPendingAction(UUID uuid) {
      return (LockAction)this.pendingActions.get(uuid);
   }

   public String getTarget(UUID uuid) {
      return (String)this.transferTargets.get(uuid);
   }

   public void clearPendingAction(UUID uuid) {
      this.pendingActions.remove(uuid);
      this.transferTargets.remove(uuid);
   }

   public static enum LockAction {
      LOCK,
      UNLOCK,
      TRANSFER,
      TRUST,
      UNTRUST,
      INFO;

      // $FF: synthetic method
      private static LockAction[] $values() {
         return new LockAction[]{LOCK, UNLOCK, TRANSFER, TRUST, UNTRUST, INFO};
      }
   }
}
