package fun.derjxnnik.rank;

import fun.derjxnnik.misc.LogUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

public class RankPermissionSetup {

    private final LuckPerms lp;

    public RankPermissionSetup(LuckPerms lp) {
        this.lp = lp;
    }

    /**
     * Applies the permission hierarchy to all rank groups.
     * Safe to run multiple times – adding an existing node is a no-op in LP.
     *
     * Hierarchy (lowest → highest):
     *   default → subscriber → vip → streamer
     *                             → famous
     *          → helper → moderator → admin
     */
    public void apply() {
        // ── default ───────────────────────────────────────────────────────────
        addPerms("default",
                "utility.spawn",
                "utility.home",
                "utility.ping",
                "utility.uuid",
                "utility.day",
                "utility.playtime",
                "utility.lastdeath",
                "utility.backpack",
                "utility.enderchest",
                "utility.craftingtable",
                "utility.chat.use");

        // ── subscriber (inherits default) ─────────────────────────────────────
        inherit("subscriber", "default");
        // no extra perms over default — distinguished by rank only

        // ── vip (inherits subscriber) ─────────────────────────────────────────
        inherit("vip", "subscriber");
        addPerms("vip",
                "utility.rename",
                "utility.hat",
                "utility.chat.color.*",
                "utility.chat.format.*");

        // ── streamer (inherits vip) ───────────────────────────────────────────
        inherit("streamer", "vip");
        addPerms("streamer",
                "utility.coins.others",
                "utility.info.bypass");

        // ── famous (inherits vip, content creator parallel to streamer) ───────
        inherit("famous", "vip");
        addPerms("famous",
                "utility.coins.others",
                "utility.info.bypass");

        // ── helper (staff entry-level, inherits default) ──────────────────────
        inherit("helper", "default");
        addPerms("helper",
                "utility.mute",
                "utility.ban.limited",
                "utility.invsee",
                "utility.info.bypass",
                "utility.help.staff",
                "utility.coins.others",
                "utility.warn",
                "utility.staffchat",
                "utility.freeze");

        // ── moderator (inherits helper) ───────────────────────────────────────
        inherit("moderator", "helper");
        addPerms("moderator",
                "utility.ban",
                "utility.setspawn",
                "utility.vanish");

        // ── admin (inherits moderator) ────────────────────────────────────────
        inherit("admin", "moderator");

        LogUtil.info("[RankPerms] Permissions erfolgreich auf alle Gruppen angewendet.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void inherit(String groupName, String parent) {
        Group group = getOrCreate(groupName);
        if (group == null) return;
        group.data().add(InheritanceNode.builder(parent).build());
        save(group);
    }

    private void addPerms(String groupName, String... permissions) {
        Group group = getOrCreate(groupName);
        if (group == null) return;
        for (String perm : permissions) {
            group.data().add(Node.builder(perm).value(true).build());
        }
        save(group);
    }

    private void save(Group group) {
        lp.getGroupManager().saveGroup(group).join();
    }

    private Group getOrCreate(String name) {
        Group group = lp.getGroupManager().getGroup(name);
        if (group == null) {
            try {
                group = lp.getGroupManager().createAndLoadGroup(name).join();
                LogUtil.info("[RankPerms] LP-Gruppe erstellt: " + name);
            } catch (Exception e) {
                LogUtil.info("[RankPerms] Fehler beim Erstellen der Gruppe '" + name + "': " + e.getMessage());
                return null;
            }
        }
        return group;
    }
}
