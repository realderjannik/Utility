package fun.derjxnnik.heads;

import fun.derjxnnik.misc.Colors;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class HeadsClickListener implements Listener {
   private final HeadsManager mgr;
   private final Plugin plugin;

   public HeadsClickListener(HeadsManager mgr, Plugin plugin) {
      this.mgr = mgr;
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof final Player p) {
         ItemStack clicked = e.getCurrentItem();
         if (clicked != null && clicked.hasItemMeta()) {
            String title = ChatColor.stripColor(e.getView().getTitle());
            String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            boolean inCategories = title.equalsIgnoreCase(ChatColor.stripColor(HeadsCategoryGUI.TITLE));
            boolean inHeadList = title.startsWith("Heads:");
            if (inCategories || inHeadList) {
               e.setCancelled(true);
               if (inCategories) {
                  if (clicked.getType() == Material.BOOK) {
                     (new HeadsListGUI(this.mgr, itemName, 0)).open(p);
                  }

               } else if (clicked.getType() == Material.PLAYER_HEAD) {
                  p.getInventory().addItem(new ItemStack[]{clicked.clone()});
                  p.sendMessage(Colors.PREFIX + Colors.GREEN + "You received " + Colors.YELLOW + itemName + Colors.GRAY + ".");
               } else {
                  final String category = title.substring("Heads: ".length()).split("\\(")[0].trim();
                  final int page = this.extractPage(title) - 1;
                  final List<HeadsManager.HeadEntry> all = this.mgr.getHeadsInCategory(category);
                  int maxPage = Math.max(1, (all.size() + 45 - 1) / 45);
                  Material type = clicked.getType();
                  if (type == Material.SPECTRAL_ARROW && itemName.equalsIgnoreCase("Back")) {
                     (new HeadsCategoryGUI(this.mgr)).open(p);
                  } else if (type != Material.ARROW) {
                     if (type == Material.NAME_TAG) {
                        p.closeInventory();
                        p.sendMessage(Colors.PREFIX + Colors.YELLOW + "Enter a search term for " + Colors.GOLD + category + Colors.YELLOW + " in chat.");
                        p.sendMessage(Colors.GRAY + "Type 'cancel' to abort.");
                        Bukkit.getPluginManager().registerEvents(new Listener() {
                           @EventHandler
                           public void onChat(AsyncPlayerChatEvent ev) {
                              if (ev.getPlayer().equals(p)) {
                                 ev.setCancelled(true);
                                 String msg = ev.getMessage().trim();
                                 if (msg.equalsIgnoreCase("cancel")) {
                                    HandlerList.unregisterAll(this);
                                    Bukkit.getScheduler().runTask(HeadsClickListener.this.plugin, () -> (new HeadsListGUI(HeadsClickListener.this.mgr, category, page)).open(p));
                                 } else {
                                    List<HeadsManager.HeadEntry> filtered = all.stream().filter((h) -> h.name != null && h.name.toLowerCase().contains(msg.toLowerCase())).toList();
                                    HandlerList.unregisterAll(this);
                                    Bukkit.getScheduler().runTask(HeadsClickListener.this.plugin, () -> {
                                       if (filtered.isEmpty()) {
                                          p.sendMessage(Colors.PREFIX + Colors.RED + "No heads found for '" + msg + "'.");
                                          (new HeadsListGUI(HeadsClickListener.this.mgr, category, page)).open(p);
                                       } else {
                                          String var10001 = Colors.PREFIX;
                                          p.sendMessage(var10001 + Colors.GREEN + "Found " + Colors.YELLOW + filtered.size() + Colors.GRAY + " heads for '" + msg + "'.");
                                          (new HeadsListGUI(HeadsClickListener.this.mgr, category, 0, filtered)).open(p);
                                       }

                                    });
                                 }
                              }
                           }
                        }, this.plugin);
                     }

                  } else {
                     if (itemName.equalsIgnoreCase("Previous") && page > 0) {
                        (new HeadsListGUI(this.mgr, category, page - 1)).open(p);
                     } else if (itemName.equalsIgnoreCase("Next") && page + 1 < maxPage) {
                        (new HeadsListGUI(this.mgr, category, page + 1)).open(p);
                     }

                  }
               }
            }
         }
      }
   }

   private int extractPage(String title) {
      try {
         int idx = title.lastIndexOf("Page ");
         if (idx == -1) {
            return 1;
         } else {
            String s = title.substring(idx + 5).replace(")", "").trim();
            return Integer.parseInt(s);
         }
      } catch (Exception var4) {
         return 1;
      }
   }
}
