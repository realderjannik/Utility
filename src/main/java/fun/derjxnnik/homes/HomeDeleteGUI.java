package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeDeleteGUI {
   public static void open(Player player, String homeName) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, Colors.RED + "Delete " + homeName + "?");
      ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
      ItemMeta confirmMeta = confirm.getItemMeta();
      confirmMeta.setDisplayName(Colors.GREEN + "Confirm Deletion");
      confirm.setItemMeta(confirmMeta);
      inv.setItem(3, confirm);
      ItemStack cancel = new ItemStack(Material.BARRIER);
      ItemMeta cancelMeta = cancel.getItemMeta();
      cancelMeta.setDisplayName(Colors.RED + "Cancel Deletion");
      cancel.setItemMeta(cancelMeta);
      inv.setItem(5, cancel);
      player.openInventory(inv);
   }
}
