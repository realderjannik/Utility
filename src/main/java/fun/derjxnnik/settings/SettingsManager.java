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
      return new File(settingsFolder, "settings_" + player.getName() + "_" + player.getUniqueId() + ".yml");
   }

   private FileConfiguration getConfig(Player player) {
      File file = getPlayerFile(player);
      if (!file.exists()) {
         try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
      }
      return YamlConfiguration.loadConfiguration(file);
   }

   private void saveConfig(Player player, FileConfiguration config) {
      try { config.save(getPlayerFile(player)); } catch (IOException e) { e.printStackTrace(); }
   }

   // ── Backpack item ───────────────────────────────────────────────────────────
   public boolean hasBackpackEnabled(Player player) {
      return getConfig(player).getBoolean("backpack", true);
   }
   public void setBackpackEnabled(Player player, boolean enabled) {
      FileConfiguration c = getConfig(player); c.set("backpack", enabled); saveConfig(player, c);
   }

   // ── Backpack color ──────────────────────────────────────────────────────────
   public String getBackpackColor(Player player) {
      return getConfig(player).getString("backpack-color", "BUNDLE");
   }
   public void setBackpackColor(Player player, String materialName) {
      FileConfiguration c = getConfig(player); c.set("backpack-color", materialName); saveConfig(player, c);
   }

   // ── Chat color ──────────────────────────────────────────────────────────────
   public String getChatColor(Player player) {
      return getConfig(player).getString("chat-color", "");
   }
   public void setChatColor(Player player, String sectionCode) {
      FileConfiguration c = getConfig(player); c.set("chat-color", sectionCode); saveConfig(player, c);
   }

   // ── Chat formats ────────────────────────────────────────────────────────────
   public java.util.List<String> getChatFormats(Player player) {
      return getConfig(player).getStringList("chat-formats");
   }
   public void setChatFormats(Player player, java.util.List<String> formats) {
      FileConfiguration c = getConfig(player); c.set("chat-formats", formats); saveConfig(player, c);
   }
   public void resetChatStyle(Player player) {
      FileConfiguration c = getConfig(player);
      c.set("chat-color", null); c.set("chat-formats", null); saveConfig(player, c);
   }

   // ── Private messages ────────────────────────────────────────────────────────
   public boolean isMsgEnabled(Player player) {
      return getConfig(player).getBoolean("msg-enabled", true);
   }
   public void setMsgEnabled(Player player, boolean enabled) {
      FileConfiguration c = getConfig(player); c.set("msg-enabled", enabled); saveConfig(player, c);
   }

   // ── Balance visibility ──────────────────────────────────────────────────────
   public boolean isShowBalance(Player player) {
      return getConfig(player).getBoolean("show-balance", true);
   }
   public void setShowBalance(Player player, boolean enabled) {
      FileConfiguration c = getConfig(player); c.set("show-balance", enabled); saveConfig(player, c);
   }

   // ── Info visibility (others can use /info on this player) ──────────────────
   public boolean isInfoVisible(Player player) {
      return getConfig(player).getBoolean("info-visible", true);
   }
   public void setInfoVisible(Player player, boolean visible) {
      FileConfiguration c = getConfig(player); c.set("info-visible", visible); saveConfig(player, c);
   }
}
