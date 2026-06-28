package fun.derjxnnik.container;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.LogUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LockManager {
   private final File file;
   private final YamlConfiguration config;
   private final Map locks = new HashMap();

   public LockManager(JavaPlugin plugin) {
      this.file = new File(plugin.getDataFolder(), "container-locks.yml");
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
            LogUtil.info("Created new container-locks.yml");
         } catch (IOException e) {
            LogUtil.error("Failed to create container-locks.yml", e);
         }
      }

      this.config = YamlConfiguration.loadConfiguration(this.file);
      this.loadLocks();
   }

   private String key(Location loc) {
      int var10000 = loc.getBlockX();
      return var10000 + "," + loc.getBlockY() + "," + loc.getBlockZ();
   }

   private String worldKey(Location loc) {
      return loc.getWorld().getName().toLowerCase(Locale.ROOT);
   }

   private LockData getLockData(Location loc) {
      return (LockData)((Map)this.locks.getOrDefault(this.worldKey(loc), Collections.emptyMap())).get(this.key(loc));
   }

   public boolean isLocked(Location loc) {
      return ((Map)this.locks.getOrDefault(this.worldKey(loc), Collections.emptyMap())).containsKey(this.key(loc));
   }

   public boolean isOwner(Location loc, String player) {
      LockData data = this.getLockData(loc);
      return data != null && data.owner.equalsIgnoreCase(player);
   }

   public boolean isTrusted(Location loc, String player) {
      LockData data = this.getLockData(loc);
      return data != null && (data.owner.equalsIgnoreCase(player) || data.trusted.contains(player.toLowerCase(Locale.ROOT)));
   }

   public String getOwner(Location loc) {
      LockData data = this.getLockData(loc);
      return data != null ? data.owner : "Unknown";
   }

   public List getTrustedDisplay(Location loc) {
      LockData data = this.getLockData(loc);
      if (data != null && !data.trusted.isEmpty()) {
         List<String> out = new ArrayList(data.trusted.size());

         for(String lower : data.trusted) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(lower);
            String name = off != null && off.getName() != null ? off.getName() : lower;
            out.add(name);
         }

         return out;
      } else {
         return List.of();
      }
   }

   public synchronized void saveLocks() {
      try {
         this.config.set("locks", (Object)null);

         for(String world : this.locks.keySet()) {
            for(Map.Entry entry : ((Map)this.locks.get(world)).entrySet()) {
               LockData data = (LockData)entry.getValue();
               String path = "locks." + world + "." + (String)entry.getKey();
               this.config.set(path + ".owner", data.owner);
               this.config.set(path + ".trusted", new ArrayList(data.trusted));
            }
         }

         this.config.save(this.file);
      } catch (IOException e) {
         LogUtil.error("Failed to save container-locks.yml", e);
      }

   }

   private synchronized void loadLocks() {
      this.locks.clear();
      if (this.config.contains("locks")) {
         for(String world : this.config.getConfigurationSection("locks").getKeys(false)) {
            Map<String, LockData> map = new HashMap();

            for(String key : this.config.getConfigurationSection("locks." + world).getKeys(false)) {
               String owner = this.config.getString("locks." + world + "." + key + ".owner");
               List<String> trusted = this.config.getStringList("locks." + world + "." + key + ".trusted");
               map.put(key, new LockData(owner, new HashSet(trusted)));
            }

            this.locks.put(world.toLowerCase(Locale.ROOT), map);
         }

         LogUtil.info("Loaded container lock data (" + this.locks.size() + " worlds).");
      }
   }

   private boolean doLock(Location loc, String owner) {
      ((Map)this.locks.computeIfAbsent(this.worldKey(loc), (w) -> new HashMap())).put(this.key(loc), new LockData(owner, new HashSet()));
      return true;
   }

   private boolean doUnlock(Location loc, String actor) {
      Map<String, LockData> map = (Map)this.locks.get(this.worldKey(loc));
      if (map != null && map.containsKey(this.key(loc))) {
         if (!this.isOwner(loc, actor)) {
            return false;
         } else {
            map.remove(this.key(loc));
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean removeLockCompletely(Location loc) {
      String world = loc.getWorld().getName().toLowerCase(Locale.ROOT);
      Map<String, LockData> map = (Map)this.locks.get(world);
      if (map == null) {
         return false;
      } else {
         String key = this.key(loc);
         if (map.containsKey(key)) {
            map.remove(key);
            this.saveLocks();
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean doTrust(Location loc, String owner, String target) {
      LockData data = this.getLockData(loc);
      if (data != null && this.isOwner(loc, owner)) {
         data.trusted.add(target.toLowerCase(Locale.ROOT));
         return true;
      } else {
         return false;
      }
   }

   private boolean doUntrust(Location loc, String owner, String target) {
      LockData data = this.getLockData(loc);
      if (data != null && this.isOwner(loc, owner)) {
         data.trusted.remove(target.toLowerCase(Locale.ROOT));
         return true;
      } else {
         return false;
      }
   }

   private boolean doTransfer(Location loc, String owner, String target) {
      LockData data = this.getLockData(loc);
      if (data != null && this.isOwner(loc, owner)) {
         data.owner = target;
         return true;
      } else {
         return false;
      }
   }

   public void lockMany(List locs, Player player) {
      boolean anyLockedByOthers = false;
      boolean anyLockedByYou = false;

      for(Location loc : locs) {
         if (this.isLocked(loc)) {
            if (this.isOwner(loc, player.getName())) {
               anyLockedByYou = true;
            } else {
               anyLockedByOthers = true;
            }
         }
      }

      if (anyLockedByOthers) {
         player.sendActionBar(Colors.RED + "This container is already locked by another player.");
      } else if (anyLockedByYou && locs.stream().allMatch((locx) -> !this.isLocked(locx) || this.isOwner(locx, player.getName()))) {
         player.sendMessage(Colors.PREFIX + Colors.YELLOW + "This container is already locked by you.");
      } else {
         for(Location loc : locs) {
            if (!this.isLocked(loc)) {
               this.doLock(loc, player.getName());
            }
         }

         this.saveLocks();
         player.sendMessage(Colors.PREFIX + Colors.GRAY + "Container locked " + Colors.GREEN + "successfully" + Colors.GRAY + ".");
      }
   }

   public void unlockMany(List locs, Player player) {
      List<Location> locked = locs.stream().filter(this::isLocked).toList();
      if (locked.isEmpty()) {
         player.sendMessage(Colors.PREFIX + Colors.RED + "This container is not locked.");
      } else {
         boolean allOwned = locked.stream().allMatch((loc) -> this.isOwner(loc, player.getName()));
         if (!allOwned) {
            player.sendMessage(Colors.PREFIX + Colors.RED + "You are not the owner of this container.");
         } else {
            locked.forEach((loc) -> this.doUnlock(loc, player.getName()));
            this.saveLocks();
            player.sendMessage(Colors.PREFIX + Colors.GRAY + "Container unlocked " + Colors.GREEN + "successfully" + Colors.GRAY + ".");
         }
      }
   }

   public void trustMany(List locs, Player owner, String target) {
      if (owner.getName().equalsIgnoreCase(target)) {
         owner.sendActionBar(Colors.RED + "You cannot trust yourself.");
      } else {
         List<Location> locked = locs.stream().filter(this::isLocked).toList();
         if (locked.isEmpty()) {
            owner.sendMessage(Colors.PREFIX + Colors.RED + "This container is not locked.");
         } else {
            boolean allOwned = locked.stream().allMatch((loc) -> this.isOwner(loc, owner.getName()));
            if (!allOwned) {
               owner.sendMessage(Colors.PREFIX + Colors.RED + "You are not the owner of this container.");
            } else {
               locked.forEach((loc) -> this.doTrust(loc, owner.getName(), target));
               this.saveLocks();
               owner.sendMessage(Colors.PREFIX + Colors.GRAY + "Trusted " + Colors.YELLOW + target + Colors.GRAY + ".");
            }
         }
      }
   }

   public void untrustMany(List locs, Player owner, String target) {
      List<Location> locked = locs.stream().filter(this::isLocked).toList();
      if (locked.isEmpty()) {
         owner.sendMessage(Colors.PREFIX + Colors.RED + "This container is not locked.");
      } else {
         boolean allOwned = locked.stream().allMatch((loc) -> this.isOwner(loc, owner.getName()));
         if (!allOwned) {
            owner.sendMessage(Colors.PREFIX + Colors.RED + "You are not the owner of this container.");
         } else {
            locked.forEach((loc) -> this.doUntrust(loc, owner.getName(), target));
            this.saveLocks();
            owner.sendMessage(Colors.PREFIX + Colors.GRAY + "Removed trust for " + Colors.YELLOW + target + Colors.GRAY + ".");
         }
      }
   }

   public void transferMany(List locs, Player owner, String target) {
      List<Location> locked = locs.stream().filter(this::isLocked).toList();
      if (locked.isEmpty()) {
         owner.sendMessage(Colors.PREFIX + Colors.RED + "This container is not locked.");
      } else {
         boolean allOwned = locked.stream().allMatch((loc) -> this.isOwner(loc, owner.getName()));
         if (!allOwned) {
            owner.sendMessage(Colors.PREFIX + Colors.RED + "You are not the owner of this container.");
         } else {
            locked.forEach((loc) -> this.doTransfer(loc, owner.getName(), target));
            this.saveLocks();
            owner.sendMessage(Colors.PREFIX + Colors.GRAY + "Transferred " + Colors.GREEN + "ownership" + Colors.GRAY + " to " + Colors.YELLOW + target + Colors.GRAY + ".");
         }
      }
   }

   private static class LockData {
      String owner;
      Set trusted;

      LockData(String owner, Set trusted) {
         this.owner = owner;
         this.trusted = trusted;
      }
   }
}
