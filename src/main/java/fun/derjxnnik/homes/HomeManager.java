package fun.derjxnnik.homes;

import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class HomeManager {
   private final File homesFolder;
   private final int maxHomes;

   public File getHomesFolder() {
      return this.homesFolder;
   }

   public HomeManager(File dataFolder, int maxHomes) {
      this.homesFolder = new File(dataFolder, "homes");
      this.maxHomes = maxHomes;
      if (!this.homesFolder.exists()) {
         this.homesFolder.mkdir();
      }

   }

   public FileConfiguration getPlayerFile(Player player) {
      File file = new File(this.homesFolder, player.getName() + ".yml");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   public File getHomeFile(Player p) {
      String fileName = p.getName() + ".yml";
      return new File(this.homesFolder, fileName);
   }

   public void savePlayerFile(Player player, FileConfiguration config) {
      File file = new File(this.homesFolder, player.getName() + ".yml");

      try {
         config.save(file);
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public int getMaxHomes() {
      return this.maxHomes;
   }

   /** Returns the home limit for the given player, respecting rank-based config. -1 = unlimited. */
   public int getMaxHomes(Player player) {
      FileConfiguration config = Utility.getInstance().getConfig();
      RankManager rm = Utility.getInstance().getRankManager();
      if (rm != null && rm.isAvailable()) {
         String group = rm.getPlayerGroup(player);
         int rankLimit = config.getInt("homes.rank-homes." + group, Integer.MIN_VALUE);
         if (rankLimit != Integer.MIN_VALUE) return rankLimit;
      }
      return config.getInt("homes.max-homes", this.maxHomes);
   }

   public void setHome(Player p, String home) {
      FileConfiguration config = this.getPlayerFile(p);
      Location loc = p.getLocation();
      config.set("homes." + home + ".world", loc.getWorld().getName());
      config.set("homes." + home + ".x", loc.getX());
      config.set("homes." + home + ".y", loc.getY());
      config.set("homes." + home + ".z", loc.getZ());
      config.set("homes." + home + ".yaw", loc.getYaw());
      config.set("homes." + home + ".pitch", loc.getPitch());
      config.set("homes." + home + ".env", loc.getWorld().getEnvironment().name());
      this.savePlayerFile(p, config);
   }

   public boolean deleteHome(Player player, String name) {
      FileConfiguration config = this.getPlayerFile(player);
      String path = "homes." + name;
      if (!config.contains(path)) {
         return false;
      } else {
         config.set(path, (Object)null);
         this.savePlayerFile(player, config);
         return true;
      }
   }

   public int getHomeCount(Player player) {
      FileConfiguration config = this.getPlayerFile(player);
      return !config.contains("homes") ? 0 : config.getConfigurationSection("homes").getKeys(false).size();
   }

   public Set getHomeNames(Player p) {
      File file = new File(this.homesFolder, p.getName() + ".yml");
      if (!file.exists()) {
         return Set.of();
      } else {
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         return !config.contains("homes") ? Set.of() : config.getConfigurationSection("homes").getKeys(false);
      }
   }
}
