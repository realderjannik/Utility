package fun.derjxnnik.settings;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SettingsManager {
   private final File settingsFolder;

   public SettingsManager(File dataFolder) {
      this.settingsFolder = new File(dataFolder, "player_settings");
      if (!this.settingsFolder.exists()) {
         this.settingsFolder.mkdir();
      }

   }

   private File getPlayerFile(Player player) {
      File var10002 = this.settingsFolder;
      String var10003 = player.getName();
      return new File(var10002, "settings_" + var10003 + "_" + String.valueOf(player.getUniqueId()) + ".yml");
   }

   private FileConfiguration getConfig(Player player) {
      File file = this.getPlayerFile(player);
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   private void saveConfig(Player player, FileConfiguration config) {
      try {
         config.save(this.getPlayerFile(player));
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   public boolean isSittingEnabled(Player player) {
      return this.getConfig(player).getBoolean("sitting", true);
   }

   public void setSittingEnabled(Player player, boolean enabled) {
      FileConfiguration config = this.getConfig(player);
      config.set("sitting", enabled);
      this.saveConfig(player, config);
   }

   public boolean hasBackpackEnabled(Player player) {
      return this.getConfig(player).getBoolean("backpack", true);
   }

   public void setBackpackEnabled(Player player, boolean enabled) {
      FileConfiguration config = this.getConfig(player);
      config.set("backpack", enabled);
      this.saveConfig(player, config);
   }

   public String getBackpackColor(Player player) {
      return this.getConfig(player).getString("backpack-color", "BUNDLE");
   }

   public void setBackpackColor(Player player, String materialName) {
      FileConfiguration config = this.getConfig(player);
      config.set("backpack-color", materialName);
      this.saveConfig(player, config);
   }
}
