package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class HomeClickListener implements Listener {
   private final HomeManager homeManager;
   private final ChatInputListener chatListener;

   public HomeClickListener(HomeManager homeManager, ChatInputListener chatListener) {
      this.homeManager = homeManager;
      this.chatListener = chatListener;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (event.getView().getTitle() != null) {
            String rawTitle = event.getView().getTitle();
            String title = ChatColor.stripColor(rawTitle);
            ItemStack clicked = event.getCurrentItem();
            if (title.contains("Home") || title.contains("Delete")) {
               event.setCancelled(true);
               event.setResult(Result.DENY);
            }

            if (clicked != null && clicked.hasItemMeta()) {
               String display = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
               if (title.equalsIgnoreCase("Home Menu")) {
                  this.handleMainMenuClick(player, clicked);
               } else if (title.equalsIgnoreCase("Select a Home")) {
                  Material type = clicked.getType();
                  if (type != Material.BARRIER && type != Material.GRAY_STAINED_GLASS_PANE && type != Material.BOOK) {
                     this.teleportToHome(player, display);
                  } else {
                     player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1.2F);
                  }
               } else if (title.equalsIgnoreCase("Delete a Home")) {
                  Material type = clicked.getType();
                  if (type != Material.BARRIER && type != Material.GRAY_STAINED_GLASS_PANE && type != Material.BOOK) {
                     if (!this.homeManager.getHomeNames(player).contains(display)) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 0.8F);
                     } else {
                        this.openDeleteConfirmation(player, display);
                     }
                  } else {
                     player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1.2F);
                  }
               } else {
                  if (title.startsWith("Delete ") && title.endsWith("?")) {
                     this.handleDeleteConfirm(player, clicked, display, rawTitle);
                  }

               }
            }
         }
      }
   }

   private void handleMainMenuClick(Player player, ItemStack clicked) {
      Material type = clicked.getType();
      if (type == Material.RED_BED) {
         player.closeInventory();
         this.chatListener.waitForInput(player);
      } else if (type == Material.RECOVERY_COMPASS) {
         player.closeInventory();
         (new HomeListGUI(this.homeManager, false)).open(player);
      } else {
         if (type == Material.BARRIER) {
            player.closeInventory();
            (new HomeListGUI(this.homeManager, true)).open(player);
         }

      }
   }

   private void teleportToHome(Player player, String homeName) {
      FileConfiguration config = this.homeManager.getPlayerFile(player);
      String path = "homes." + homeName;
      if (!config.contains(path)) {
         player.sendMessage(Colors.PREFIX + Colors.RED + "That home doesn't exist!");
         player.closeInventory();
      } else {
         String world = config.getString(path + ".world");
         double x = config.getDouble(path + ".x");
         double y = config.getDouble(path + ".y");
         double z = config.getDouble(path + ".z");
         float yaw = (float)config.getDouble(path + ".yaw");
         float pitch = (float)config.getDouble(path + ".pitch");
         if (Bukkit.getWorld(world) == null) {
            player.sendMessage(Colors.PREFIX + Colors.RED + "World not found!");
         } else {
            Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            player.teleport(loc);
            player.sendMessage(Colors.PREFIX + Colors.GRAY + "Teleported to home " + Colors.DARK_AQUA + homeName + Colors.GRAY + ".");
            player.closeInventory();
         }
      }
   }

   private void openDeleteConfirmation(Player player, String homeName) {
      player.closeInventory();
      HomeDeleteGUI.open(player, homeName);
   }

   private void handleDeleteConfirm(Player player, ItemStack clicked, String display, String rawTitle) {
      String homeName = ChatColor.stripColor(rawTitle).replaceFirst("^Delete\\s+", "").replace("?", "").trim();
      if (clicked.getType() == Material.BARRIER) {
         player.closeInventory();
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Deletion cancelled.");
      } else {
         if (clicked.getType() == Material.EMERALD_BLOCK) {
            boolean success = this.homeManager.deleteHome(player, homeName);
            player.closeInventory();
            if (success) {
               player.sendMessage(Colors.PREFIX + Colors.GRAY + "Home '" + Colors.YELLOW + homeName + Colors.GRAY + "' deleted.");
            } else {
               player.sendMessage(Colors.PREFIX + Colors.RED + "Could not delete home '" + Colors.YELLOW + homeName + Colors.RED + "'.");
            }

            (new HomeListGUI(this.homeManager, true)).open(player);
         }

      }
   }
}
