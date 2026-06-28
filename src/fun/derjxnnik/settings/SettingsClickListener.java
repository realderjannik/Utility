package fun.derjxnnik.settings;

import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.utility.Utility;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsClickListener implements Listener {
   private final SettingsManager settings;
   private final BackpackItemManager backpackManager;
   private final Map dyeToBundle = new LinkedHashMap() {
      {
         this.put(Material.WHITE_DYE, Material.WHITE_BUNDLE);
         this.put(Material.ORANGE_DYE, Material.ORANGE_BUNDLE);
         this.put(Material.MAGENTA_DYE, Material.MAGENTA_BUNDLE);
         this.put(Material.LIGHT_BLUE_DYE, Material.LIGHT_BLUE_BUNDLE);
         this.put(Material.YELLOW_DYE, Material.YELLOW_BUNDLE);
         this.put(Material.LIME_DYE, Material.LIME_BUNDLE);
         this.put(Material.PINK_DYE, Material.PINK_BUNDLE);
         this.put(Material.GRAY_DYE, Material.GRAY_BUNDLE);
         this.put(Material.LIGHT_GRAY_DYE, Material.LIGHT_GRAY_BUNDLE);
         this.put(Material.CYAN_DYE, Material.CYAN_BUNDLE);
         this.put(Material.PURPLE_DYE, Material.PURPLE_BUNDLE);
         this.put(Material.BLUE_DYE, Material.BLUE_BUNDLE);
         this.put(Material.BROWN_DYE, Material.BROWN_BUNDLE);
         this.put(Material.GREEN_DYE, Material.GREEN_BUNDLE);
         this.put(Material.RED_DYE, Material.RED_BUNDLE);
         this.put(Material.BLACK_DYE, Material.BLACK_BUNDLE);
      }
   };
   private Inventory colorSelectionInv;

   public SettingsClickListener(SettingsManager settings, BackpackItemManager backpackManager) {
      this.settings = settings;
      this.backpackManager = backpackManager;
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         if (e.getView().getTitle() != null) {
            String stripped = ChatColor.stripColor(e.getView().getTitle());
            if (stripped.equals("Your Settings") || stripped.equals("Select Backpack Color")) {
               e.setCancelled(true);
               e.setResult(Result.DENY);
            }

            String title = this.strip(e.getView().getTitle());
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
               String name = this.strip(clicked.getItemMeta().getDisplayName());
               if (title.equals("Your Settings")) {
                  e.setCancelled(true);
                  if (name.startsWith("Sitting")) {
                     boolean current = this.settings.isSittingEnabled(p);
                     this.settings.setSittingEnabled(p, !current);
                     p.sendActionBar("§7Sitting has been " + (!current ? "§aenabled" : "§cdisabled") + "§7.");
                  } else if (name.startsWith("Backpack Item")) {
                     boolean current = this.settings.hasBackpackEnabled(p);
                     boolean newState = !current;
                     this.settings.setBackpackEnabled(p, newState);
                     p.sendActionBar("§7Backpack Item has been " + (newState ? "§aenabled" : "§cdisabled") + "§7.");
                     if (newState) {
                        this.backpackManager.giveBackpackItem(p);
                     } else {
                        this.backpackManager.removeBackpackItem(p);
                     }

                     p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, newState ? 1.4F : 0.8F);
                  } else if (name.startsWith("Backpack Color")) {
                     if (!this.settings.hasBackpackEnabled(p)) {
                        p.sendActionBar("§cYou must enable your backpack item before changing its color!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.8F);
                        return;
                     }

                     this.openColorSelection(p);
                     return;
                  }

                  p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.2F);
                  Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> p.performCommand("settings"), 2L);
               } else {
                  if (title.equals("Select Backpack Color")) {
                     e.setCancelled(true);
                     Material clickedMat = clicked.getType();
                     Material bundleMat = (Material)this.dyeToBundle.get(clickedMat);
                     if (bundleMat == null) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.7F, 0.8F);
                        return;
                     }

                     this.settings.setBackpackColor(p, bundleMat.name());
                     String var10001 = this.pretty(bundleMat);
                     p.sendActionBar("§7Your backpack color has been set to §f" + var10001 + "§7.");
                     p.playSound(p.getLocation(), Sound.ITEM_BUNDLE_INSERT, 1.0F, 1.1F);
                     p.closeInventory();
                     Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> this.backpackManager.refreshBackpackItem(p), 2L);
                  }

               }
            }
         }
      }
   }

   private void openColorSelection(Player p) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "§8Select Backpack Color");
      int slot = 0;

      for(Material dye : this.dyeToBundle.keySet()) {
         ItemStack item = new ItemStack(dye);
         ItemMeta meta = item.getItemMeta();
         String var10001 = this.pretty(dye);
         meta.setDisplayName("§f" + var10001);
         var10001 = new String[1];
         String var10004 = this.pretty((Material)this.dyeToBundle.get(dye));
         ((Object[])var10001)[0] = "§7Click to set color to §f" + var10004 + "§7.";
         meta.setLore(Arrays.asList(var10001));
         item.setItemMeta(meta);
         inv.setItem(slot++, item);
         if (slot >= inv.getSize()) {
            break;
         }
      }

      p.openInventory(inv);
      p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
   }

   private String strip(String s) {
      return ChatColor.stripColor(s == null ? "" : s);
   }

   private String pretty(Material mat) {
      if (mat == null) {
         return "unknown";
      } else {
         String n = mat.name();
         n = n.replace("_DYE", "").replace("_BUNDLE", "").replace('_', ' ').toLowerCase();
         StringBuilder sb = new StringBuilder();

         for(String part : n.split(" ")) {
            if (!part.isEmpty()) {
               sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
            }
         }

         return sb.toString().trim();
      }
   }
}
