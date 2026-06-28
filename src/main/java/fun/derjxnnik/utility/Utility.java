package fun.derjxnnik.utility;

import fun.derjxnnik.backpack.BackpackItemListener;
import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.backpack.BackpackListener;
import fun.derjxnnik.backpack.BackpackManager;
import fun.derjxnnik.commands.BackpackCommand;
import fun.derjxnnik.commands.BugreportCommand;
import fun.derjxnnik.commands.CoinsCommand;
import fun.derjxnnik.commands.PayCommand;
import fun.derjxnnik.commands.PaymentsCommand;
import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.chat.ColorManager;
import fun.derjxnnik.commands.ColorCommand;
import fun.derjxnnik.commands.CraftCommand;
import fun.derjxnnik.commands.DayCommand;
import fun.derjxnnik.commands.EnderChestCommand;
import fun.derjxnnik.commands.HatCommand;
import fun.derjxnnik.commands.HeadCommand;
import fun.derjxnnik.commands.HomeCommand;
import fun.derjxnnik.commands.InvseeCommand;
import fun.derjxnnik.commands.LastDeathCommand;
import fun.derjxnnik.commands.PingCommand;
import fun.derjxnnik.commands.PlaytimeCommand;
import fun.derjxnnik.commands.RenameCommand;
import fun.derjxnnik.commands.SetSpawnCommand;
import fun.derjxnnik.commands.SpawnCommand;
import fun.derjxnnik.commands.TPACommand;
import fun.derjxnnik.commands.UUIDCommand;
import fun.derjxnnik.commands.UtilityCommand;
import fun.derjxnnik.container.ContainerCommand;
import fun.derjxnnik.container.ContainerListener;
import fun.derjxnnik.container.LockManager;
import fun.derjxnnik.heads.HeadsClickListener;
import fun.derjxnnik.heads.HeadsManager;
import fun.derjxnnik.homes.ChatInputListener;
import fun.derjxnnik.homes.HomeClickListener;
import fun.derjxnnik.homes.HomeManager;
import fun.derjxnnik.listeners.ChatListener;
import fun.derjxnnik.listeners.DeathListener;
import fun.derjxnnik.listeners.EnderChestListener;
import fun.derjxnnik.listeners.InventoryProtectionListener;
import fun.derjxnnik.listeners.JoinListener;
import fun.derjxnnik.listeners.QuitListener;
import fun.derjxnnik.listeners.ServerListPingListener;
import fun.derjxnnik.listeners.SitListener;
import fun.derjxnnik.listeners.SwitchWorldListener;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.resourcepack.ResourcePackListener;
import fun.derjxnnik.resourcepack.ResourcePackManager;
import fun.derjxnnik.misc.LogUtil;
import fun.derjxnnik.settings.SettingsClickListener;
import fun.derjxnnik.settings.SettingsCommand;
import fun.derjxnnik.settings.SettingsManager;
import fun.derjxnnik.tabcompleters.BackpackTabCompleter;
import fun.derjxnnik.tabcompleters.ColorTabCompleter;
import fun.derjxnnik.tabcompleters.ContainerTabCompleter;
import fun.derjxnnik.tabcompleters.EnderChestTabCompleter;
import fun.derjxnnik.tabcompleters.InvseeTabCompleter;
import fun.derjxnnik.tabcompleters.PlaytimeTabCompleter;
import fun.derjxnnik.tabcompleters.SetSpawnTabCompleter;
import fun.derjxnnik.tabcompleters.TPATabCompleter;
import fun.derjxnnik.tabcompleters.UUIDTabCompleter;
import fun.derjxnnik.tpa.TPAAcceptCommand;
import fun.derjxnnik.tpa.TPADenyCommand;
import fun.derjxnnik.tpa.TPAManager;
import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Utility extends JavaPlugin {
   private static Utility instance;
   private HeadsManager headsManager;
   private LockManager lockManager;
   private RankManager rankManager;
   private ColorManager colorManager;
   private ResourcePackManager resourcePackManager;
   private CurrencyManager currencyManager;

   public void onEnable() {
      instance = this;
      LogUtil.init(this);
      this.saveDefaultConfig();
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
      this.reloadConfig();
      saveResourcePackFiles();
      int slots = this.getConfig().getInt("backpack.slots", 54);
      boolean allowShulkers = this.getConfig().getBoolean("backpack.allow-shulkers", false);
      boolean allowAdminEdit = this.getConfig().getBoolean("backpack.allow-admin-edit", true);
      int maxHomes = this.getConfig().getInt("homes.max-homes", 5);
      BackpackManager backpackManager = new BackpackManager(this.getDataFolder(), allowShulkers);
      HomeManager homeManager = new HomeManager(this.getDataFolder(), maxHomes);
      ChatInputListener chatListener = new ChatInputListener(homeManager, maxHomes);
      HomeClickListener homeClickListener = new HomeClickListener(homeManager, chatListener);
      File headsFile = new File(this.getDataFolder(), "heads.yml");
      if (!headsFile.exists()) {
         this.saveResource("heads.yml", false);
      }

      this.headsManager = new HeadsManager(this.getDataFolder());
      SettingsManager settingsManager = new SettingsManager(this.getDataFolder());
      BackpackItemManager backpackItemManager = new BackpackItemManager(this, settingsManager);
      TPAManager tpaManager = new TPAManager(this);
      this.lockManager = new LockManager(this);
      this.rankManager = new RankManager();
      this.colorManager = new ColorManager(settingsManager);
      this.currencyManager = new CurrencyManager(this.getDataFolder());
      if (this.getConfig().getBoolean("resource-pack.enabled", true)) {
         this.resourcePackManager = new ResourcePackManager(this.getDataFolder());
         this.resourcePackManager.start();
      }
      if (!this.rankManager.isLuckPermsPresent()) {
         LogUtil.info("[Ranks] LuckPerms nicht gefunden. Prefixe werden nicht angezeigt. Download: https://luckperms.net/download");
      }
      this.getCommand("backpack").setExecutor(new BackpackCommand(backpackManager, slots));
      this.getCommand("ping").setExecutor(new PingCommand());
      this.getCommand("ec").setExecutor(new EnderChestCommand());
      this.getCommand("rename").setExecutor(new RenameCommand());
      this.getCommand("craft").setExecutor(new CraftCommand());
      this.getCommand("hat").setExecutor(new HatCommand());
      this.getCommand("lastdeath").setExecutor(new LastDeathCommand());
      this.getCommand("spawn").setExecutor(new SpawnCommand(this));
      this.getCommand("setspawn").setExecutor(new SetSpawnCommand());
      this.getCommand("playtime").setExecutor(new PlaytimeCommand());
      this.getCommand("uuid").setExecutor(new UUIDCommand());
      this.getCommand("day").setExecutor(new DayCommand());
      this.getCommand("invsee").setExecutor(new InvseeCommand());
      this.getCommand("home").setExecutor(new HomeCommand(homeManager, maxHomes));
      this.getCommand("head").setExecutor(new HeadCommand(this.headsManager));
      this.getCommand("tpa").setExecutor(new TPACommand(this, tpaManager));
      this.getCommand("tpaccept").setExecutor(new TPAAcceptCommand(this, tpaManager));
      this.getCommand("tpdeny").setExecutor(new TPADenyCommand(this, tpaManager));
      this.getCommand("utility").setExecutor(new UtilityCommand());
      this.getCommand("settings").setExecutor(new SettingsCommand(settingsManager));
      this.getCommand("bugreport").setExecutor(new BugreportCommand());
      this.getCommand("color").setExecutor(new ColorCommand(this.colorManager));
      this.getCommand("color").setTabCompleter(new ColorTabCompleter());
      PayCommand payCommand = new PayCommand(this.currencyManager);
      this.getCommand("pay").setExecutor(payCommand);
      this.getCommand("pay").setTabCompleter(payCommand);
      this.getCommand("coins").setExecutor(new CoinsCommand(this.currencyManager));
      this.getCommand("payments").setExecutor(new PaymentsCommand(this.currencyManager));
      ContainerCommand containerCommand = new ContainerCommand(this.lockManager);
      this.getCommand("container").setExecutor(containerCommand);
      this.getCommand("backpack").setTabCompleter(new BackpackTabCompleter());
      this.getCommand("setspawn").setTabCompleter(new SetSpawnTabCompleter());
      this.getCommand("invsee").setTabCompleter(new InvseeTabCompleter());
      this.getCommand("uuid").setTabCompleter(new UUIDTabCompleter());
      this.getCommand("playtime").setTabCompleter(new PlaytimeTabCompleter());
      this.getCommand("ec").setTabCompleter(new EnderChestTabCompleter());
      this.getCommand("container").setTabCompleter(new ContainerTabCompleter());
      this.getCommand("tpa").setTabCompleter(new TPATabCompleter());
      this.getServer().getPluginManager().registerEvents(new BackpackListener(backpackManager), this);
      this.getServer().getPluginManager().registerEvents(new JoinListener(this, backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new QuitListener(this, backpackManager), this);
      this.getServer().getPluginManager().registerEvents(new EnderChestListener(), this);
      this.getServer().getPluginManager().registerEvents(new SwitchWorldListener(), this);
      this.getServer().getPluginManager().registerEvents(homeClickListener, this);
      this.getServer().getPluginManager().registerEvents(chatListener, this);
      this.getServer().getPluginManager().registerEvents(new SitListener(settingsManager), this);
      this.getServer().getPluginManager().registerEvents(new HeadsClickListener(this.headsManager, this), this);
      this.getServer().getPluginManager().registerEvents(new InventoryProtectionListener(), this);
      this.getServer().getPluginManager().registerEvents(new BackpackItemListener(backpackManager, this), this);
      this.getServer().getPluginManager().registerEvents(new SettingsClickListener(settingsManager, backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new DeathListener(backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
      this.getServer().getPluginManager().registerEvents(new ContainerListener(this, this.lockManager, containerCommand), this);
      this.getServer().getPluginManager().registerEvents(new ChatListener(this.rankManager, this.colorManager, this), this);
      this.getServer().getPluginManager().registerEvents(new ResourcePackListener(), this);
      (new BukkitRunnable() {
         public void run() {
            ScoreboardManager.updateAll();
         }
      }).runTaskTimer(this, 20L, 20L);
      LogUtil.info("Utility enabled successfully!");
      LogUtil.info("Heads loaded: " + this.headsManager.getTotalCount());
   }

   private void saveResourcePackFiles() {
      String[] files = {
         "resourcepack/pack.mcmeta",
         "resourcepack/assets/minecraft/font/default.json",
         "resourcepack/assets/minecraft/font/small_caps.json",
         "resourcepack/assets/minecraft/textures/font/icon_time.png",
         "resourcepack/assets/minecraft/textures/font/icon_kills.png",
         "resourcepack/assets/minecraft/textures/font/icon_deaths.png",
         "resourcepack/assets/minecraft/textures/font/icon_playtime.png",
         "resourcepack/assets/minecraft/textures/font/icon_ping.png",
         "resourcepack/assets/minecraft/textures/font/icon_money.png",
         "resourcepack/assets/minecraft/textures/font/icon_clan.png",
         "resourcepack/assets/minecraft/textures/font/small_caps_ascii.png",
         "resourcepack/assets/minecraft/textures/font/logo.png",
         "resourcepack/assets/minecraft/textures/font/label_XS.png",
         "resourcepack/assets/minecraft/textures/font/label_S.png",
         "resourcepack/assets/minecraft/textures/font/label_M.png",
         "resourcepack/assets/minecraft/textures/font/label_L.png",
         "resourcepack/assets/minecraft/textures/font/label_XL.png"
      };
      for (String path : files) {
         File target = new File(getDataFolder(), path);
         if (!target.exists()) {
            saveResource(path, false);
         }
      }
   }

   public void onDisable() {
      LogUtil.info("Utility is shutting down...");
      this.saveConfig();
      if (this.resourcePackManager != null) {
         this.resourcePackManager.stop();
      }
      if (this.lockManager != null) {
         this.lockManager.saveLocks();
         LogUtil.info("Container locks saved successfully.");
      }

      LogUtil.info("Utility has been disabled!");
   }

   public static Utility getInstance() {
      return instance;
   }

   public RankManager getRankManager() {
      return rankManager;
   }

   public ColorManager getColorManager() {
      return colorManager;
   }

   public ResourcePackManager getResourcePackManager() {
      return resourcePackManager;
   }

   public CurrencyManager getCurrencyManager() {
      return currencyManager;
   }
}
