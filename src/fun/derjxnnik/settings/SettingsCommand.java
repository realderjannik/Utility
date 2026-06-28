package fun.derjxnnik.settings;

import fun.derjxnnik.misc.Colors;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsCommand implements CommandExecutor {
   private final SettingsManager settings;

   public SettingsCommand(SettingsManager settings) {
      this.settings = settings;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender instanceof Player p) {
         Inventory gui = Bukkit.createInventory((InventoryHolder)null, 9, "§8Your Settings");
         boolean sitEnabled = this.settings.isSittingEnabled(p);
         ItemStack sitItem = new ItemStack(Material.OAK_STAIRS);
         ItemMeta sitMeta = sitItem.getItemMeta();
         sitMeta.setDisplayName("§eSitting: " + (sitEnabled ? "§aON" : "§cOFF"));
         sitMeta.setLore(Arrays.asList("§7Click to " + (sitEnabled ? "§cdisable " : "§aenable ") + "§7sitting"));
         sitItem.setItemMeta(sitMeta);
         gui.setItem(2, sitItem);
         boolean bpEnabled = this.settings.hasBackpackEnabled(p);
         ItemStack bpItem = new ItemStack(Material.BUNDLE);
         ItemMeta bpMeta = bpItem.getItemMeta();
         bpMeta.setDisplayName("§eBackpack Item: " + (bpEnabled ? "§aON" : "§cOFF"));
         bpMeta.setLore(Arrays.asList("§7Click to " + (bpEnabled ? "§cdisable " : "§aenable ") + "§7the Backpack Item", "§7in your Inventory"));
         bpItem.setItemMeta(bpMeta);
         gui.setItem(4, bpItem);
         ItemStack colorItem = new ItemStack(Material.BLUE_DYE);
         ItemMeta colorMeta = colorItem.getItemMeta();
         colorMeta.setDisplayName("§eBackpack Color");
         String[] var10001 = new String[2];
         String var10004 = this.settings.getBackpackColor(p);
         var10001[0] = "§7Current: §f" + var10004;
         var10001[1] = "§7Click to change";
         colorMeta.setLore(Arrays.asList(var10001));
         colorItem.setItemMeta(colorMeta);
         gui.setItem(6, colorItem);
         p.openInventory(gui);
         p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
         return true;
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
