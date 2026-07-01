package fun.derjxnnik.utility;

import fun.derjxnnik.backpack.BackpackItemListener;
import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.backpack.BackpackListener;
import fun.derjxnnik.backpack.BackpackManager;
import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.commands.BackpackCommand;
import fun.derjxnnik.commands.BanCommand;
import fun.derjxnnik.commands.DiscordCommand;
import fun.derjxnnik.commands.MuteCommand;
import fun.derjxnnik.commands.TwitchCommand;
import fun.derjxnnik.commands.UnmuteCommand;
import fun.derjxnnik.commands.WebsiteCommand;
import fun.derjxnnik.mute.MuteManager;
import fun.derjxnnik.commands.BugreportCommand;
import fun.derjxnnik.commands.CoinsCommand;
import fun.derjxnnik.commands.MsgCommand;
import fun.derjxnnik.commands.PayCommand;
import fun.derjxnnik.commands.PaymentsCommand;
import fun.derjxnnik.commands.ReplyCommand;
import fun.derjxnnik.commands.UnbanCommand;
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
import fun.derjxnnik.commands.HelpCommand;
import fun.derjxnnik.commands.InfoCommand;
import fun.derjxnnik.commands.UtilityCommand;
import fun.derjxnnik.container.ContainerCommand;
import fun.derjxnnik.container.ContainerListener;
import fun.derjxnnik.container.LockManager;
import fun.derjxnnik.heads.HeadsClickListener;
import fun.derjxnnik.heads.HeadsManager;
import fun.derjxnnik.homes.ChatInputListener;
import fun.derjxnnik.homes.HomeClickListener;
import fun.derjxnnik.homes.HomeManager;
import fun.derjxnnik.listeners.BanLoginListener;
import fun.derjxnnik.listeners.ChatListener;
import fun.derjxnnik.listeners.DeathListener;
import fun.derjxnnik.listeners.EnderChestListener;
import fun.derjxnnik.listeners.InventoryProtectionListener;
import fun.derjxnnik.listeners.JoinListener;
import fun.derjxnnik.listeners.QuitListener;
import fun.derjxnnik.listeners.ServerListPingListener;
import fun.derjxnnik.listeners.SwitchWorldListener;
import fun.derjxnnik.messages.MessageManager;
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
   private SettingsManager settingsManager;
   private BanManager banManager;
   private MuteManager muteManager;
   private MessageManager messageManager;

   public void onEnable() {
      instance = this;
      LogUtil.init(this);
      this.saveDefaultConfig();
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
      this.reloadConfig();
      saveResourcePackFiles();

      boolean allowShulkers = this.getConfig().getBoolean("backpack.allow-shulkers", false);
      boolean allowAdminEdit = this.getConfig().getBoolean("backpack.allow-admin-edit", true);
      int maxHomes = this.getConfig().getInt("homes.max-homes", 3);

      BackpackManager backpackManager = new BackpackManager(this.getDataFolder(), allowShulkers);
      HomeManager homeManager = new HomeManager(this.getDataFolder(), maxHomes);

      this.settingsManager = new SettingsManager(this.getDataFolder());
      this.banManager = new BanManager(this.getDataFolder());
      this.muteManager = new MuteManager(this.getDataFolder());
      this.messageManager = new MessageManager();

      ChatInputListener chatListener = new ChatInputListener(homeManager);
      HomeClickListener homeClickListener = new HomeClickListener(homeManager, chatListener);

      File headsFile = new File(this.getDataFolder(), "heads.yml");
      if (!headsFile.exists()) {
         this.saveResource("heads.yml", false);
      }

      this.headsManager = new HeadsManager(this.getDataFolder());
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

      // Commands — core utility
      this.getCommand("backpack").setExecutor(new BackpackCommand(backpackManager));
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

      // Info / help
      InfoCommand infoCommand = new InfoCommand();
      this.getCommand("info").setExecutor(infoCommand);
      this.getCommand("info").setTabCompleter(infoCommand);
      this.getCommand("help").setExecutor(new HelpCommand());
      this.getCommand("color").setExecutor(new ColorCommand(this.colorManager));
      this.getCommand("color").setTabCompleter(new ColorTabCompleter());

      // Economy
      PayCommand payCommand = new PayCommand(this.currencyManager);
      this.getCommand("pay").setExecutor(payCommand);
      this.getCommand("pay").setTabCompleter(payCommand);

      CoinsCommand coinsCommand = new CoinsCommand(this.currencyManager, this.settingsManager);
      this.getCommand("coins").setExecutor(coinsCommand);
      this.getCommand("coins").setTabCompleter(coinsCommand);

      this.getCommand("payments").setExecutor(new PaymentsCommand(this.currencyManager));

      // Messaging
      MsgCommand msgCommand = new MsgCommand(this.messageManager, this.settingsManager);
      this.getCommand("msg").setExecutor(msgCommand);
      this.getCommand("msg").setTabCompleter(msgCommand);
      this.getCommand("r").setExecutor(new ReplyCommand(this.messageManager, this.settingsManager));

      // Moderation
      BanCommand banCommand = new BanCommand(this.banManager);
      this.getCommand("ban").setExecutor(banCommand);
      this.getCommand("ban").setTabCompleter(banCommand);

      UnbanCommand unbanCommand = new UnbanCommand(this.banManager);
      this.getCommand("unban").setExecutor(unbanCommand);
      this.getCommand("unban").setTabCompleter(unbanCommand);

      MuteCommand muteCommand = new MuteCommand(this.muteManager);
      this.getCommand("mute").setExecutor(muteCommand);
      this.getCommand("mute").setTabCompleter(muteCommand);
      UnmuteCommand unmuteCommand = new UnmuteCommand(this.muteManager);
      this.getCommand("unmute").setExecutor(unmuteCommand);
      this.getCommand("unmute").setTabCompleter(unmuteCommand);

      // Social links
      this.getCommand("discord").setExecutor(new DiscordCommand());
      this.getCommand("website").setExecutor(new WebsiteCommand());
      this.getCommand("twitch").setExecutor(new TwitchCommand());

      // Misc
      ContainerCommand containerCommand = new ContainerCommand(this.lockManager);
      this.getCommand("container").setExecutor(containerCommand);

      // Tab completers
      this.getCommand("backpack").setTabCompleter(new BackpackTabCompleter());
      this.getCommand("setspawn").setTabCompleter(new SetSpawnTabCompleter());
      this.getCommand("invsee").setTabCompleter(new InvseeTabCompleter());
      this.getCommand("uuid").setTabCompleter(new UUIDTabCompleter());
      this.getCommand("playtime").setTabCompleter(new PlaytimeTabCompleter());
      this.getCommand("ec").setTabCompleter(new EnderChestTabCompleter());
      this.getCommand("container").setTabCompleter(new ContainerTabCompleter());
      this.getCommand("tpa").setTabCompleter(new TPATabCompleter());

      // Listeners
      this.getServer().getPluginManager().registerEvents(new BackpackListener(backpackManager), this);
      this.getServer().getPluginManager().registerEvents(new JoinListener(this, backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new QuitListener(this, backpackManager), this);
      this.getServer().getPluginManager().registerEvents(new EnderChestListener(), this);
      this.getServer().getPluginManager().registerEvents(new SwitchWorldListener(), this);
      this.getServer().getPluginManager().registerEvents(homeClickListener, this);
      this.getServer().getPluginManager().registerEvents(chatListener, this);
      this.getServer().getPluginManager().registerEvents(new HeadsClickListener(this.headsManager, this), this);
      this.getServer().getPluginManager().registerEvents(new InventoryProtectionListener(), this);
      this.getServer().getPluginManager().registerEvents(new BackpackItemListener(backpackManager, this), this);
      this.getServer().getPluginManager().registerEvents(new SettingsClickListener(settingsManager, backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new DeathListener(backpackItemManager), this);
      this.getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
      this.getServer().getPluginManager().registerEvents(new ContainerListener(this, this.lockManager, containerCommand), this);
      this.getServer().getPluginManager().registerEvents(new ChatListener(this.rankManager, this.colorManager, this), this);
      this.getServer().getPluginManager().registerEvents(new ResourcePackListener(), this);
      this.getServer().getPluginManager().registerEvents(new BanLoginListener(this.banManager), this);

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
         "resourcepack/assets/minecraft/textures/font/header_general.png",
         "resourcepack/assets/minecraft/textures/font/header_rank.png",
         "resourcepack/assets/minecraft/textures/font/header_money.png",
         "resourcepack/assets/minecraft/textures/font/header_kills.png",
         "resourcepack/assets/minecraft/textures/font/header_deaths.png",
         "resourcepack/assets/minecraft/textures/font/header_clan.png",
         "resourcepack/assets/minecraft/textures/font/label_discord.png",
         "resourcepack/assets/minecraft/textures/font/label_website.png",
         "resourcepack/assets/minecraft/textures/font/label_twitch.png"
      };
      for (String path : files) {
         File target = new File(getDataFolder(), path);
         // Always overwrite font JSON files so texture additions take effect immediately.
         // PNG files are only written once (user may have replaced them manually).
         boolean alwaysOverwrite = path.endsWith(".json") || path.endsWith(".mcmeta");
         if (alwaysOverwrite || !target.exists()) {
            saveResource(path, alwaysOverwrite);
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

   public static Utility getInstance()            { return instance; }
   public RankManager getRankManager()            { return rankManager; }
   public ColorManager getColorManager()          { return colorManager; }
   public ResourcePackManager getResourcePackManager() { return resourcePackManager; }
   public CurrencyManager getCurrencyManager()    { return currencyManager; }
   public SettingsManager getSettingsManager()    { return settingsManager; }
   public BanManager getBanManager()              { return banManager; }
   public MuteManager getMuteManager()            { return muteManager; }
   public MessageManager getMessageManager()      { return messageManager; }
}
