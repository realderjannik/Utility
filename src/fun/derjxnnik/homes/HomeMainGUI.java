package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeMainGUI {
   private final HomeManager manager;

   public HomeMainGUI(HomeManager manager) {
      this.manager = manager;
   }

   public void open(Player player) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Colors.DARK_AQUA + "Home Menu");
      ItemStack bed = new ItemStack(Material.RED_BED);
      ItemMeta bedMeta = bed.getItemMeta();
      bedMeta.setDisplayName(Colors.YELLOW + "Set a new Home");
      bed.setItemMeta(bedMeta);
      inv.setItem(11, bed);
      ItemStack compass = new ItemStack(Material.RECOVERY_COMPASS);
      ItemMeta compassMeta = compass.getItemMeta();
      compassMeta.setDisplayName(Colors.AQUA + "Teleport to a Home");
      compass.setItemMeta(compassMeta);
      inv.setItem(13, compass);
      ItemStack barrier = new ItemStack(Material.BARRIER);
      ItemMeta barrierMeta = barrier.getItemMeta();
      barrierMeta.setDisplayName(Colors.RED + "Delete a Home");
      barrier.setItemMeta(barrierMeta);
      inv.setItem(15, barrier);
      ItemStack info = new ItemStack(Material.BOOK);
      ItemMeta infoMeta = info.getItemMeta();
      infoMeta.setDisplayName(Colors.DARK_AQUA + "Home Overview");
      infoMeta.setLore(List.of(Colors.GRAY + "Manage your homes with style.", Colors.DARK_GRAY + "• " + Colors.YELLOW + "Set" + Colors.GRAY + " a new home", Colors.DARK_GRAY + "• " + Colors.AQUA + "Teleport" + Colors.GRAY + " to an existing one", Colors.DARK_GRAY + "• " + Colors.RED + "Delete" + Colors.GRAY + " old locations"));
      info.setItemMeta(infoMeta);
      inv.setItem(26, info);
      player.openInventory(inv);
   }
}
