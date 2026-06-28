package fun.derjxnnik.backpack;

import fun.derjxnnik.misc.Colors;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BackpackManager {
   private final File backpackFolder;
   private final boolean allowShulkers;
   private final Map backpackCache = new HashMap();

   public BackpackManager(File dataFolder, boolean allowShulkers) {
      this.allowShulkers = allowShulkers;
      this.backpackFolder = new File(dataFolder, "backpacks");
      if (!this.backpackFolder.exists()) {
         this.backpackFolder.mkdir();
      }

   }

   private File findBackpackFileByUUID(UUID uuid) {
      File[] files = this.backpackFolder.listFiles();
      if (files == null) {
         return null;
      } else {
         for(File file : files) {
            if (file.getName().endsWith(".yml") && file.getName().contains(uuid.toString())) {
               return file;
            }
         }

         return null;
      }
   }

   public Inventory getBackpackByUUID(UUID uuid, int size) {
      File file = this.findBackpackFileByUUID(uuid);
      if (file != null && file.exists()) {
         String name = file.getName().split("_")[0];
         Inventory bp = Bukkit.createInventory((InventoryHolder)null, size, Colors.DARK_AQUA + name + "'s Backpack");
         YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

         for(int i = 0; i < size; ++i) {
            if (config.contains("slot." + i)) {
               bp.setItem(i, config.getItemStack("slot." + i));
            }
         }

         return bp;
      } else {
         return null;
      }
   }

   public Inventory getBackpack(Player p, int size) {
      if (this.backpackCache.containsKey(p.getUniqueId())) {
         return (Inventory)this.backpackCache.get(p.getUniqueId());
      } else {
         File file = this.getBackpackFile(p);
         String var10002 = Colors.DARK_AQUA;
         Inventory bp = Bukkit.createInventory((InventoryHolder)null, size, var10002 + p.getName() + "'s Backpack");
         if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for(int i = 0; i < size; ++i) {
               if (config.contains("slot." + i)) {
                  bp.setItem(i, config.getItemStack("slot." + i));
               }
            }
         }

         this.backpackCache.put(p.getUniqueId(), bp);
         return bp;
      }
   }

   public boolean restoreBackpack(UUID from, Player to) {
      File sourceFile = this.findBackpackFileByUUID(from);
      if (sourceFile != null && sourceFile.exists()) {
         YamlConfiguration sourceConfig = YamlConfiguration.loadConfiguration(sourceFile);
         File targetFile = this.getBackpackFile(to);
         YamlConfiguration targetConfig = new YamlConfiguration();

         for(String key : sourceConfig.getKeys(false)) {
            targetConfig.set(key, sourceConfig.get(key));
         }

         try {
            targetConfig.save(targetFile);
            return true;
         } catch (IOException e) {
            Bukkit.getLogger().severe("Could not restore backpack: " + e.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   public void saveBackpack(Player p, Inventory inventory) {
      File file = this.getBackpackFile(p);
      YamlConfiguration config = new YamlConfiguration();

      for(int i = 0; i < inventory.getSize(); ++i) {
         ItemStack item = inventory.getItem(i);
         if (item != null) {
            if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
               List<String> lore = item.getItemMeta().getLore();
               if (lore != null && lore.stream().anyMatch((l) -> l.contains("Click to set color to"))) {
                  continue;
               }
            }

            config.set("slot." + i, item);
         }
      }

      try {
         config.save(file);
      } catch (IOException e) {
         Logger var10000 = Bukkit.getLogger();
         String var10001 = p.getName();
         var10000.severe("Failed to save backpack for player " + var10001 + ": " + e.getMessage());
      }

   }

   public void unloadBackpack(Player p) {
      this.backpackCache.remove(p.getUniqueId());
   }

   public boolean isAllowed(ItemStack item) {
      if (item == null) {
         return true;
      } else if (this.allowShulkers) {
         return true;
      } else {
         return !item.getType().name().endsWith("SHULKER_BOX");
      }
   }

   public File getBackpackFile(Player p) {
      String var10000 = p.getName();
      String fileName = var10000 + "_" + String.valueOf(p.getUniqueId()) + ".yml";
      return new File(this.backpackFolder, fileName);
   }
}
