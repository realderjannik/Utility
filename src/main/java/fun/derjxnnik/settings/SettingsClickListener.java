package fun.derjxnnik.settings;

import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.misc.Messages;
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

   private final Map<Material, Material> dyeToBundle = new LinkedHashMap<Material, Material>() {{
      put(Material.WHITE_DYE,       Material.WHITE_BUNDLE);
      put(Material.ORANGE_DYE,      Material.ORANGE_BUNDLE);
      put(Material.MAGENTA_DYE,     Material.MAGENTA_BUNDLE);
      put(Material.LIGHT_BLUE_DYE,  Material.LIGHT_BLUE_BUNDLE);
      put(Material.YELLOW_DYE,      Material.YELLOW_BUNDLE);
      put(Material.LIME_DYE,        Material.LIME_BUNDLE);
      put(Material.PINK_DYE,        Material.PINK_BUNDLE);
      put(Material.GRAY_DYE,        Material.GRAY_BUNDLE);
      put(Material.LIGHT_GRAY_DYE,  Material.LIGHT_GRAY_BUNDLE);
      put(Material.CYAN_DYE,        Material.CYAN_BUNDLE);
      put(Material.PURPLE_DYE,      Material.PURPLE_BUNDLE);
      put(Material.BLUE_DYE,        Material.BLUE_BUNDLE);
      put(Material.BROWN_DYE,       Material.BROWN_BUNDLE);
      put(Material.GREEN_DYE,       Material.GREEN_BUNDLE);
      put(Material.RED_DYE,         Material.RED_BUNDLE);
      put(Material.BLACK_DYE,       Material.BLACK_BUNDLE);
   }};

   public SettingsClickListener(SettingsManager settings, BackpackItemManager backpackManager) {
      this.settings = settings;
      this.backpackManager = backpackManager;
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      HumanEntity clicker = e.getWhoClicked();
      if (!(clicker instanceof Player p)) return;
      if (e.getView().getTitle() == null) return;

      String stripped = ChatColor.stripColor(e.getView().getTitle());
      if (stripped.equals("Einstellungen") || stripped.equals("Rucksack-Farbe")) {
         e.setCancelled(true);
         e.setResult(Result.DENY);
      }

      String title = stripped;
      ItemStack clicked = e.getCurrentItem();
      if (clicked == null || !clicked.hasItemMeta()) return;
      String name = strip(clicked.getItemMeta().getDisplayName());

      if (title.equals("Einstellungen")) {
         e.setCancelled(true);

         if (name.startsWith("Private Nachrichten")) {
            boolean current = settings.isMsgEnabled(p);
            settings.setMsgEnabled(p, !current);
            p.sendActionBar(!current ? Messages.SETTINGS_MSG_AN : Messages.SETTINGS_MSG_AUS);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, !current ? 1.4F : 0.8F);
            Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> p.performCommand("settings"), 2L);

         } else if (name.startsWith("Sitzen")) {
            boolean current = settings.isSittingEnabled(p);
            settings.setSittingEnabled(p, !current);
            p.sendActionBar(!current ? Messages.SETTINGS_SITZEN_AN : Messages.SETTINGS_SITZEN_AUS);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.2F);
            Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> p.performCommand("settings"), 2L);

         } else if (name.startsWith("Rucksack-Item")) {
            boolean current = settings.hasBackpackEnabled(p);
            boolean newState = !current;
            settings.setBackpackEnabled(p, newState);
            p.sendActionBar(newState ? Messages.SETTINGS_RUCKSACK_AN : Messages.SETTINGS_RUCKSACK_AUS);
            if (newState) {
               backpackManager.giveBackpackItem(p);
            } else {
               backpackManager.removeBackpackItem(p);
            }
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, newState ? 1.4F : 0.8F);
            Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> p.performCommand("settings"), 2L);

         } else if (name.startsWith("Rucksack-Farbe")) {
            if (!settings.hasBackpackEnabled(p)) {
               p.sendActionBar(Messages.SETTINGS_RUCKSACK_FARBE_NICHT_AKTIV);
               p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.8F);
               return;
            }
            openColorSelection(p);

         } else if (name.startsWith("Guthaben")) {
            boolean current = settings.isShowBalance(p);
            settings.setShowBalance(p, !current);
            p.sendActionBar(!current ? Messages.SETTINGS_GUTHABEN_AN : Messages.SETTINGS_GUTHABEN_AUS);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, !current ? 1.4F : 0.8F);
            Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> p.performCommand("settings"), 2L);
         }

      } else if (title.equals("Rucksack-Farbe")) {
         e.setCancelled(true);
         Material bundleMat = dyeToBundle.get(clicked.getType());
         if (bundleMat == null) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.7F, 0.8F);
            return;
         }
         settings.setBackpackColor(p, bundleMat.name());
         p.sendActionBar("§7Rucksack-Farbe auf §f" + pretty(bundleMat) + "§7 gesetzt.");
         p.playSound(p.getLocation(), Sound.ITEM_BUNDLE_INSERT, 1.0F, 1.1F);
         p.closeInventory();
         Bukkit.getScheduler().runTaskLater(Utility.getInstance(), () -> backpackManager.refreshBackpackItem(p), 2L);
      }
   }

   private void openColorSelection(Player p) {
      Inventory inv = Bukkit.createInventory((InventoryHolder) null, 27, "§8Rucksack-Farbe");
      int slot = 0;
      for (Material dye : dyeToBundle.keySet()) {
         ItemStack item = new ItemStack(dye);
         ItemMeta meta = item.getItemMeta();
         meta.setDisplayName("§f" + pretty(dye));
         meta.setLore(Arrays.asList("§7Klicken, um die Farbe auf §f" + pretty(dyeToBundle.get(dye)) + "§7 zu setzen."));
         item.setItemMeta(meta);
         inv.setItem(slot++, item);
         if (slot >= inv.getSize()) break;
      }
      p.openInventory(inv);
      p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
   }

   private String strip(String s) {
      return ChatColor.stripColor(s == null ? "" : s);
   }

   private String pretty(Material mat) {
      if (mat == null) return "Unbekannt";
      String n = mat.name().replace("_DYE", "").replace("_BUNDLE", "").replace('_', ' ').toLowerCase();
      StringBuilder sb = new StringBuilder();
      for (String part : n.split(" ")) {
         if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
      }
      return sb.toString().trim();
   }
}
