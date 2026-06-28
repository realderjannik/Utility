package fun.derjxnnik.tpa;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TPAManager {
   private final Map pendingRequests = new HashMap();
   private final Map requestExpiry = new HashMap();
   private final Utility plugin;

   public TPAManager(Utility plugin) {
      this.plugin = plugin;
   }

   public void sendRequest(Player sender, Player target) {
      if (sender.equals(target)) {
         sender.sendMessage(Colors.PREFIX + Colors.RED + "You cannot send a TPA request to yourself!");
      } else {
         this.pendingRequests.put(target.getUniqueId(), sender.getUniqueId());
         this.requestExpiry.put(target.getUniqueId(), System.currentTimeMillis() + 1000L * (long)this.plugin.getConfig().getInt("tpa.request-expire", 60));
         String var10001 = Colors.PREFIX;
         sender.sendMessage(var10001 + Colors.GREEN + "Teleport request sent to " + Colors.YELLOW + target.getName() + Colors.GRAY + "!");
         var10001 = Colors.PREFIX;
         target.sendMessage(var10001 + Colors.YELLOW + sender.getName() + Colors.GRAY + " wants to teleport to you!");
         target.sendMessage(Colors.GRAY + "Type " + Colors.GREEN + "/tpaccept" + Colors.GRAY + " to accept or " + Colors.RED + "/tpdeny" + Colors.GRAY + " to deny.");
      }
   }

   public void acceptRequest(final Player target) {
      UUID senderId = (UUID)this.pendingRequests.get(target.getUniqueId());
      if (senderId == null) {
         target.sendMessage(Colors.PREFIX + Colors.RED + "You have no pending TPA requests.");
      } else {
         final Player sender = Bukkit.getPlayer(senderId);
         if (sender == null) {
            target.sendMessage(Colors.PREFIX + Colors.RED + "That player is no longer online.");
            this.cleanup(target);
         } else {
            long expiry = (Long)this.requestExpiry.getOrDefault(target.getUniqueId(), 0L);
            if (System.currentTimeMillis() > expiry) {
               target.sendMessage(Colors.PREFIX + Colors.RED + "That TPA request has expired.");
               this.cleanup(target);
            } else {
               this.cleanup(target);
               boolean instant = this.plugin.getConfig().getBoolean("tpa.instant", false);
               final int delay = this.plugin.getConfig().getInt("tpa.timer", 3);
               if (instant) {
                  sender.teleport(target.getLocation());
                  String var9 = Colors.PREFIX;
                  sender.sendMessage(var9 + Colors.GREEN + "Teleported instantly to " + Colors.YELLOW + target.getName() + Colors.GRAY + "!");
                  var9 = Colors.PREFIX;
                  target.sendMessage(var9 + Colors.GREEN + sender.getName() + Colors.GRAY + " teleported to you.");
               } else {
                  sender.sendMessage(Colors.PREFIX + Colors.GRAY + "Teleporting in " + Colors.YELLOW + delay + Colors.GRAY + " seconds... Don't move!");
                  String var10001 = Colors.PREFIX;
                  target.sendMessage(var10001 + Colors.GRAY + sender.getName() + " is teleporting to you...");
                  final Location startLocation = sender.getLocation().clone();
                  (new BukkitRunnable() {
                     int timer = delay;

                     public void run() {
                        if (sender.isOnline() && target.isOnline()) {
                           if (sender.getLocation().distanceSquared(startLocation) > 0.1) {
                              sender.sendMessage(Colors.PREFIX + Colors.RED + "Teleport cancelled because you moved!");
                              String var2 = Colors.PREFIX;
                              target.sendMessage(var2 + Colors.GRAY + sender.getName() + Colors.RED + " moved — teleport cancelled.");
                              this.cancel();
                           } else if (this.timer <= 0) {
                              sender.teleport(target.getLocation());
                              sender.getWorld().spawnParticle(Particle.PORTAL, sender.getLocation(), 50, (double)1.0F, (double)1.0F, (double)1.0F, 0.1);
                              String var10001 = Colors.PREFIX;
                              sender.sendMessage(var10001 + Colors.GREEN + "Teleported to " + Colors.YELLOW + target.getName() + Colors.GRAY + "!");
                              var10001 = Colors.PREFIX;
                              target.sendMessage(var10001 + Colors.GREEN + sender.getName() + Colors.GRAY + " teleported to you.");
                              this.cancel();
                           } else {
                              sender.getWorld().spawnParticle(Particle.PORTAL, sender.getLocation(), 15, (double)0.5F, (double)1.0F, (double)0.5F, 0.05);
                              --this.timer;
                           }
                        } else {
                           this.cancel();
                        }
                     }
                  }).runTaskTimer(this.plugin, 0L, 20L);
               }
            }
         }
      }
   }

   public void denyRequest(Player target) {
      if (!this.pendingRequests.containsKey(target.getUniqueId())) {
         target.sendMessage(Colors.PREFIX + Colors.RED + "You have no pending TPA requests.");
      } else {
         Player sender = Bukkit.getPlayer((UUID)this.pendingRequests.get(target.getUniqueId()));
         if (sender != null) {
            String var10001 = Colors.PREFIX;
            sender.sendMessage(var10001 + Colors.RED + "Your teleport request to " + Colors.YELLOW + target.getName() + Colors.RED + " was denied.");
         }

         target.sendMessage(Colors.PREFIX + Colors.GRAY + "Teleport request denied.");
         this.cleanup(target);
      }
   }

   private void cleanup(Player target) {
      this.pendingRequests.remove(target.getUniqueId());
      this.requestExpiry.remove(target.getUniqueId());
   }
}
