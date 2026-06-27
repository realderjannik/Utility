package fun.derjxnnik.heads;

import fun.derjxnnik.misc.Colors;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HeadsCategoryGUI {
   public static final String TITLE;
   private final HeadsManager mgr;

   public HeadsCategoryGUI(HeadsManager mgr) {
      this.mgr = mgr;
   }

   public void open(Player p) {
      int catCount = this.mgr.getCategories().size();
      int size = ((catCount - 1) / 9 + 1) * 9;
      size = Math.min(size, 54);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, TITLE);
      int slot = 0;

      for(String cat : this.mgr.getCategories()) {
         ItemStack book = new ItemStack(Material.BOOK);
         ItemMeta meta = book.getItemMeta();
         meta.setDisplayName(Colors.YELLOW + cat);
         meta.setLore(List.of(Colors.GRAY + "Click to browse heads"));
         book.setItemMeta(meta);
         inv.setItem(slot++, book);
         if (slot >= size - 1) {
            break;
         }
      }

      ItemStack info = new ItemStack(Material.WRITABLE_BOOK);
      ItemMeta im = info.getItemMeta();
      im.setDisplayName(Colors.BLUE + "Info");
      String var13 = Colors.GRAY + "Categories: " + Colors.YELLOW + this.mgr.getCategories().size();
      String var10002 = Colors.GRAY;
      im.setLore(List.of(var13, var10002 + "Total heads: " + Colors.YELLOW + this.mgr.getTotalCount()));
      info.setItemMeta(im);
      inv.setItem(size - 1, info);
      p.openInventory(inv);
   }

   static {
      TITLE = Colors.BLUE + "Head Categories";
   }
}
