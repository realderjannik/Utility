package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeListGUI {
   private final HomeManager manager;
   private final boolean deleteMode;

   public HomeListGUI(HomeManager manager, boolean deleteMode) {
      this.manager = manager;
      this.deleteMode = deleteMode;
   }

   public void open(Player player) {
      int size = 27;
      String title = Colors.DARK_AQUA + (this.deleteMode ? "Delete a Home" : "Select a Home");
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, title);
      Set<String> homeNames = this.manager.getHomeNames(player);
      int maxHomes = this.manager.getMaxHomes();
      int freeSlots = Math.max(0, maxHomes - homeNames.size());
      ItemStack info = new ItemStack(Material.BOOK);
      ItemMeta infoMeta = info.getItemMeta();
      infoMeta.setDisplayName(Colors.DARK_AQUA + "Home Overview");
      String var10001 = Colors.GRAY;
      infoMeta.setLore(List.of(var10001 + "You have " + Colors.YELLOW + homeNames.size() + Colors.GRAY + " / " + Colors.YELLOW + maxHomes + Colors.GRAY + " homes."));
      info.setItemMeta(infoMeta);
      inv.setItem(26, info);

      for(String home : homeNames) {
         ItemStack item = this.buildHomeItem(player, home);
         inv.addItem(new ItemStack[]{item});
      }

      if (freeSlots > 0) {
         String homeWord = freeSlots == 1 ? "home" : "homes";
         int slot = 0;

         for(int placed = 0; slot < inv.getSize() && placed < freeSlots; ++slot) {
            if (inv.getItem(slot) == null) {
               ItemStack barrier = new ItemStack(Material.BARRIER, 1);
               ItemMeta meta = barrier.getItemMeta();
               if (meta != null) {
                  meta.setDisplayName(Colors.DARK_GRAY + "⛔ " + Colors.GRAY + "Free Home Slot");
                  meta.setLore(List.of(Colors.GRAY + "You can still set " + Colors.YELLOW + freeSlots + Colors.GRAY + " " + homeWord + "."));
                  barrier.setItemMeta(meta);
               }

               inv.setItem(slot, barrier);
               ++placed;
            }
         }
      }

      for(int i = 0; i < inv.getSize(); ++i) {
         if (inv.getItem(i) == null) {
            ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = pane.getItemMeta();
            if (meta != null) {
               meta.setDisplayName(" ");
               pane.setItemMeta(meta);
            }

            inv.setItem(i, pane);
         }
      }

      player.openInventory(inv);
   }

   private ItemStack buildHomeItem(Player player, String home) {
      File file = new File(this.manager.getHomesFolder(), player.getName() + ".yml");
      FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
      String base = "homes." + home;
      String worldName = cfg.getString(base + ".world", "world");
      double x = cfg.getDouble(base + ".x");
      double y = cfg.getDouble(base + ".y");
      double z = cfg.getDouble(base + ".z");
      String envStr = cfg.getString(base + ".env", (String)null);
      Material icon = this.iconFor(envStr, worldName);
      ItemStack it = new ItemStack(icon);
      ItemMeta meta = it.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(Colors.YELLOW + home);
         String biomeText = "Unknown";
         World w = Bukkit.getWorld(worldName);
         if (w != null) {
            Location loc = new Location(w, x, y, z);
            Biome biome = loc.getBlock().getBiome();
            NamespacedKey key = Registry.BIOME.getKey(biome);
            if (key != null) {
               biomeText = key.getKey().replace("_", " ");
               String var10000 = biomeText.substring(0, 1).toUpperCase();
               biomeText = var10000 + biomeText.substring(1);
            }
         }

         List<String> lore = new ArrayList();
         lore.add(Colors.DARK_GRAY + "World: " + Colors.GRAY + worldName);
         lore.add(Colors.DARK_GRAY + "XYZ: " + Colors.GRAY + (int)x + " / " + (int)y + " / " + (int)z);
         lore.add(Colors.DARK_GRAY + "Biome: " + Colors.GRAY + biomeText);
         lore.add(this.deleteMode ? Colors.RED + "Click to delete" : Colors.GREEN + "Click to teleport");
         meta.setLore(lore);
         it.setItemMeta(meta);
      }

      return it;
   }

   private Material iconFor(String envStr, String worldName) {
      if (envStr != null) {
         switch (envStr) {
            case "NETHER" -> {
               return Material.NETHERRACK;
            }
            case "THE_END" -> {
               return Material.END_STONE;
            }
            default -> {
               return Material.GRASS_BLOCK;
            }
         }
      } else {
         String n = worldName.toLowerCase();
         if (n.contains("nether")) {
            return Material.NETHERRACK;
         } else {
            return !n.contains("the_end") && !n.endsWith("_end") && !n.contains("end") ? Material.GRASS_BLOCK : Material.END_STONE;
         }
      }
   }
}
