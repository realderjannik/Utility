package fun.derjxnnik.backpack;

import fun.derjxnnik.settings.SettingsManager;
import fun.derjxnnik.utility.Utility;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackpackItemManager {
   public static final String BACKPACK_TAG = "§7[§6Backpack§7]";
   private final Utility plugin;
   private final SettingsManager settings;

   public BackpackItemManager(Utility plugin, SettingsManager settings) {
      this.plugin = plugin;
      this.settings = settings;
   }

   public ItemStack createBackpackItem(Player player) {
      String matName = this.settings.getBackpackColor(player);

      Material mat;
      try {
         mat = Material.valueOf(matName);
         if (!mat.name().endsWith("_BUNDLE") && mat != Material.BUNDLE) {
            mat = Material.BUNDLE;
         }
      } catch (IllegalArgumentException var6) {
         mat = Material.BUNDLE;
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName("§6Backpack");
      meta.setLore(List.of("§7Click to open your backpack", "§7[§6Backpack§7]"));
      item.setItemMeta(meta);
      return item;
   }

   public void giveBackpackItem(Player player) {
      if (this.settings.hasBackpackEnabled(player)) {
         Inventory inv = player.getInventory();
         ItemStack backpack = this.createBackpackItem(player);
         ItemStack slotItem = inv.getItem(9);
         if (slotItem != null && slotItem.getType() != Material.AIR) {
            int empty = inv.firstEmpty();
            if (empty == -1) {
               player.sendMessage("§cYour inventory is too full to receive your backpack!");
            } else {
               inv.setItem(empty, slotItem);
               inv.setItem(9, backpack);
               player.updateInventory();
            }
         } else {
            inv.setItem(9, backpack);
         }
      }
   }

   public void removeBackpackItem(Player player) {
      Inventory inv = player.getInventory();

      for(int i = 0; i < inv.getSize(); ++i) {
         ItemStack it = inv.getItem(i);
         if (isBackpackItem(it)) {
            inv.setItem(i, (ItemStack)null);
            player.updateInventory();
            return;
         }
      }

   }

   public void refreshBackpackItem(Player player) {
      if (player != null) {
         if (player.isOnline() && !player.isDead()) {
            if (player.getOpenInventory() != null) {
               String t = ChatColor.stripColor(player.getOpenInventory().getTitle()).toLowerCase();
               if (t.contains("select backpack color") || t.contains("your settings")) {
                  return;
               }
            }

            String rawTitle = player.getOpenInventory().getTitle();
            String title = ChatColor.stripColor(rawTitle == null ? "" : rawTitle);
            if (!title.toLowerCase().contains("settings") && !title.toLowerCase().contains("color")) {
               this.removeBackpackItem(player);
               if (this.settings.hasBackpackEnabled(player)) {
                  this.giveBackpackItem(player);
               }

            }
         }
      }
   }

   public static boolean isBackpackItem(ItemStack item) {
      if (item != null && item.hasItemMeta()) {
         ItemMeta meta = item.getItemMeta();
         return meta.hasLore() && meta.getLore().contains("§7[§6Backpack§7]");
      } else {
         return false;
      }
   }
}
