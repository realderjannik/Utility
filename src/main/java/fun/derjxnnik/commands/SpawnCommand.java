package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
   private final JavaPlugin plugin;

   public SpawnCommand(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
      if (sender instanceof final Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.spawn", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else {
            World overworld = Bukkit.getWorlds().stream().filter((w) -> w.getEnvironment() == Environment.NORMAL).findFirst().orElse(null);
            if (overworld == null) {
               p.sendMessage(Colors.PREFIX + Colors.RED + "Could not find the Overworld!");
               return true;
            } else {
               Location overworldSpawn = overworld.getSpawnLocation();
               final Location target = new Location(p.getWorld(), overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ(), overworldSpawn.getYaw(), overworldSpawn.getPitch());
               (new BukkitRunnable() {
                  int counter = 3;

                  public void run() {
                     if (this.counter == 0) {
                        p.sendActionBar(Colors.GRAY + "Teleporting...");
                        p.teleport(target);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        this.cancel();
                     } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                        p.sendActionBar(Colors.GRAY + "Teleporting in " + Colors.YELLOW + this.counter);
                        SpawnCommand.this.startParticleCircle(p, 3, (double)1.5F, Particle.FIREWORK);
                        --this.counter;
                     }

                  }
               }).runTaskTimer(this.plugin, 0L, 20L);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   public void startParticleCircle(final Player player, final int durationInSeconds, final double radius, final Particle particle) {
      (new BukkitRunnable() {
         int step = 0;

         public void run() {
            if (this.step >= durationInSeconds * 20 / 5) {
               this.cancel();
            } else {
               Location base = player.getLocation().clone().add((double)0.0F, 0.1 + (double)this.step * 0.2, (double)0.0F);

               for(int angle = 0; angle < 360; angle += 6) {
                  double radians = Math.toRadians((double)angle);
                  double x = Math.cos(radians) * radius;
                  double z = Math.sin(radians) * radius;
                  Location particleLoc = base.clone().add(x, (double)0.0F, z);
                  player.getWorld().spawnParticle(particle, particleLoc, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
               }

               ++this.step;
            }
         }
      }).runTaskTimer(this.plugin, 0L, 4L);
   }
}
