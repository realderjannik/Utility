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

public class HeadsListGUI {
   private final HeadsManager mgr;
   private final String category;
   private final int page;
   private final List customList;
   private static final int PAGE_SIZE = 45;

   public HeadsListGUI(HeadsManager mgr, String category, int page) {
      this(mgr, category, page, (List)null);
   }

   public HeadsListGUI(HeadsManager mgr, String category, int page, List customList) {
      this.mgr = mgr;
      this.category = category;
      this.page = page;
      this.customList = customList;
   }

   public void open(Player p) {
      List<HeadsManager.HeadEntry> all = this.customList != null ? this.customList : this.mgr.getHeadsInCategory(this.category);
      int from = this.page * 45;
      int to = Math.min(all.size(), from + 45);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, Colors.BLUE + "Heads: " + Colors.YELLOW + this.category + Colors.GRAY + " (Page " + (this.page + 1) + ")");

      for(int i = from; i < to; ++i) {
         HeadsManager.HeadEntry e = (HeadsManager.HeadEntry)all.get(i);
         ItemStack skull = SkullUtil.fromBase64(e.texture, Colors.YELLOW + e.name);
         inv.addItem(new ItemStack[]{skull});
      }

      int maxPage = Math.max(1, (all.size() + 45 - 1) / 45);
      inv.setItem(45, this.item(Material.ARROW, Colors.GRAY + "Previous"));
      inv.setItem(46, this.item(Material.SPECTRAL_ARROW, Colors.YELLOW + "Back"));
      inv.setItem(49, this.item(Material.PAPER, Colors.GRAY + "Page " + Colors.YELLOW + (this.page + 1) + Colors.GRAY + " / " + Colors.YELLOW + maxPage));
      inv.setItem(50, this.item(Material.NAME_TAG, Colors.GOLD + "Search Heads"));
      inv.setItem(53, this.item(Material.ARROW, Colors.GRAY + "Next"));
      p.openInventory(inv);
   }

   private ItemStack item(Material mat, String name) {
      ItemStack it = new ItemStack(mat);
      ItemMeta meta = it.getItemMeta();
      meta.setDisplayName(name);
      it.setItemMeta(meta);
      return it;
   }
}
