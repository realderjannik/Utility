package fun.derjxnnik.rank;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class RankManager {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final LuckPerms luckPerms;
    private final boolean luckPermsPresent;

    public RankManager() {
        RegisteredServiceProvider<LuckPerms> provider =
                Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
            this.luckPermsPresent = true;
            registerLuckPermsListener();
        } else {
            this.luckPerms = null;
            this.luckPermsPresent = false;
        }
    }

    private void registerLuckPermsListener() {
        Utility plugin = Utility.getInstance();
        luckPerms.getEventBus().subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            UUID uuid = event.getUser().getUniqueId();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                Bukkit.getScheduler().runTask(plugin, () -> refreshPlayer(player));
            }
        });
    }

    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    public boolean isAvailable() {
        return luckPermsPresent && Utility.getInstance().getConfig().getBoolean("ranks.enabled", true);
    }

    public String getPrefix(Player player) {
        if (!luckPermsPresent) return "";
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return "";
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String buildTabName(Player player) {
        FileConfiguration config = Utility.getInstance().getConfig();
        if (!isAvailable() || !config.getBoolean("ranks.show-in-tab", true)) {
            return Colors.WHITE + player.getName();
        }
        String prefix = getPrefix(player);
        if (prefix.isEmpty()) return Colors.WHITE + player.getName();

        String format = config.getString("ranks.tab-format", "{prefix} | {name}");
        return format
                .replace("{prefix}", prefix)
                .replace(" | ", Colors.GRAY + " | " + Colors.WHITE)
                .replace("{name}", Colors.WHITE + player.getName());
    }

    /** Unique 16-char team name per player, based on UUID. */
    private String getTeamName(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");
        return "rk" + uuid.substring(0, 14);
    }

    /**
     * Applies (or removes) the nametag prefix team for `target` on the given scoreboard.
     * Must be called for every online player's scoreboard so the prefix is visible to them.
     */
    public void applyNametagTeam(Player target, Scoreboard board) {
        FileConfiguration config = Utility.getInstance().getConfig();
        String teamName = getTeamName(target);

        if (!isAvailable() || !config.getBoolean("ranks.show-in-nametag", true)) {
            Team team = board.getTeam(teamName);
            if (team != null) team.removeEntry(target.getName());
            return;
        }

        String prefix = getPrefix(target);
        if (prefix.isEmpty()) {
            Team team = board.getTeam(teamName);
            if (team != null) team.removeEntry(target.getName());
            return;
        }

        Team team = board.getTeam(teamName);
        if (team == null) team = board.registerNewTeam(teamName);

        // Prefix ends with Colors.WHITE so the player's name renders in white after it
        String nametagPrefix = prefix + Colors.GRAY + " | " + Colors.WHITE;
        team.prefix(LEGACY.deserialize(nametagPrefix));

        if (!team.hasEntry(target.getName())) {
            team.addEntry(target.getName());
        }
    }

    /** Applies nametag teams for ALL online players onto the given scoreboard. */
    public void applyAllNametagTeams(Scoreboard board) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyNametagTeam(p, board);
        }
    }

    /** Updates tab name + nametag team on every online player's scoreboard. */
    public void refreshPlayer(Player player) {
        player.playerListName(LEGACY.deserialize(buildTabName(player)));
        for (Player observer : Bukkit.getOnlinePlayers()) {
            applyNametagTeam(player, observer.getScoreboard());
        }
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }
}
