package fun.derjxnnik.misc;

public class Messages {

    // ==================== Allgemein ====================
    public static final String NUR_SPIELER        = Colors.PREFIX + Colors.RED  + "Dieser Befehl kann nur von Spielern genutzt werden.";
    public static final String KEINE_BERECHTIGUNG = Colors.PREFIX + Colors.RED  + "Du hast keine Berechtigung für diesen Befehl.";
    public static final String BEFEHL_DEAKTIVIERT = Colors.PREFIX + Colors.RED  + "Dieser Befehl ist momentan deaktiviert.";
    public static final String SPIELER_OFFLINE    = Colors.PREFIX + Colors.RED  + "Spieler wurde nicht gefunden oder ist offline.";

    // ==================== /msg ====================
    public static final String MSG_NUTZUNG            = Colors.PREFIX + Colors.RED  + "Nutzung: /msg <Spieler> <Nachricht>";
    public static final String MSG_SELBST             = Colors.PREFIX + Colors.RED  + "Du kannst dir nicht selbst schreiben.";
    public static final String MSG_EMPFAENGER_OFFLINE = Colors.PREFIX + Colors.RED  + "Dieser Spieler ist nicht online.";
    public static final String MSG_DEAKTIVIERT        = Colors.PREFIX + Colors.RED  + "Dieser Spieler hat private Nachrichten deaktiviert.";
    public static final String MSG_SELBST_DEAKTIVIERT = Colors.PREFIX + Colors.GRAY + "Du hast private Nachrichten deaktiviert. Aktiviere sie in "
                                                        + Colors.DARK_AQUA + "/settings" + Colors.GRAY + ".";

    public static String msgGesendet(String empfaenger, String nachricht) {
        return Colors.DARK_GRAY + "[" + Colors.WHITE + "Du " + Colors.GRAY + "→ "
                + Colors.WHITE + empfaenger + Colors.DARK_GRAY + "] " + Colors.WHITE + nachricht;
    }

    public static String msgErhalten(String sender, String nachricht) {
        return Colors.DARK_GRAY + "[" + Colors.WHITE + sender + Colors.GRAY + " → "
                + Colors.WHITE + "Du" + Colors.DARK_GRAY + "] " + Colors.WHITE + nachricht;
    }

    // ==================== /r ====================
    public static final String REPLY_NUTZUNG      = Colors.PREFIX + Colors.RED + "Nutzung: /r <Nachricht>";
    public static final String REPLY_KEIN_PARTNER = Colors.PREFIX + Colors.RED + "Du hast keine aktive Konversation oder dein Partner ist offline.";

    // ==================== /ban ====================
    public static final String BAN_NUTZUNG         = Colors.PREFIX + Colors.RED + "Nutzung: /ban <Spieler> <Grund> [Zeit]";
    public static final String UNBAN_NUTZUNG        = Colors.PREFIX + Colors.RED + "Nutzung: /unban <Spieler>";
    public static final String BAN_BEREITS_GEBANNT  = Colors.PREFIX + Colors.RED + "Dieser Spieler ist bereits gebannt.";
    public static final String BAN_NICHT_GEFUNDEN   = Colors.PREFIX + Colors.RED + "Kein gebannter Spieler mit diesem Namen gefunden.";
    public static final String BAN_ZEIT_UNGUELTIG   = Colors.PREFIX + Colors.RED + "Ungültiges Zeitformat. Beispiele: 1h, 12h, 7d, 30d";

    public static String banErfolgreich(String name, String grund, String dauer) {
        return Colors.PREFIX + Colors.GREEN + "Spieler " + Colors.WHITE + name + Colors.GREEN + " wurde gebannt."
                + Colors.GRAY + " Grund: " + Colors.WHITE + grund
                + Colors.GRAY + " | Dauer: " + Colors.WHITE + dauer;
    }

    public static String unbanErfolgreich(String name) {
        return Colors.PREFIX + Colors.GREEN + "Spieler " + Colors.WHITE + name + Colors.GREEN + " wurde entbannt.";
    }

    public static String banKickNachricht(String grund, String dauer, String banner) {
        return "\n" + Colors.BOLD_RED + "Du wurdest gebannt!\n\n"
                + Colors.RESET + Colors.GRAY + "Grund: " + Colors.WHITE + grund + "\n"
                + Colors.GRAY + "Dauer: " + Colors.WHITE + dauer + "\n"
                + Colors.GRAY + "Gebannt von: " + Colors.WHITE + banner;
    }

    public static String banKickNachrichtPermanent(String grund, String banner) {
        return "\n" + Colors.BOLD_RED + "Du wurdest permanent gebannt!\n\n"
                + Colors.RESET + Colors.GRAY + "Grund: " + Colors.WHITE + grund + "\n"
                + Colors.GRAY + "Dauer: " + Colors.WHITE + "Permanent" + "\n"
                + Colors.GRAY + "Gebannt von: " + Colors.WHITE + banner;
    }

    // ==================== /mute ====================
    public static final String MUTE_NUTZUNG          = Colors.PREFIX + Colors.RED + "Nutzung: /mute <Spieler> <Grund> <Zeit>";
    public static final String UNMUTE_NUTZUNG        = Colors.PREFIX + Colors.RED + "Nutzung: /unmute <Spieler>";
    public static final String MUTE_BEREITS_GEMUTET  = Colors.PREFIX + Colors.RED + "Dieser Spieler ist bereits stummgeschaltet.";
    public static final String MUTE_NICHT_GEFUNDEN   = Colors.PREFIX + Colors.RED + "Kein stummgeschalteter Spieler mit diesem Namen gefunden.";
    public static final String BAN_LIMIT_UEBERSCHRITTEN = Colors.PREFIX + Colors.RED + "Du kannst maximal 7 Tage bannen. Permanente Bans sind dir nicht erlaubt.";

    public static String muteErfolgreich(String name, String grund, String dauer) {
        return Colors.PREFIX + Colors.GREEN + "Spieler " + Colors.WHITE + name + Colors.GREEN + " wurde stummgeschaltet."
                + Colors.GRAY + " Grund: " + Colors.WHITE + grund
                + Colors.GRAY + " | Dauer: " + Colors.WHITE + dauer;
    }

    public static String unmuteErfolgreich(String name) {
        return Colors.PREFIX + Colors.GREEN + "Spieler " + Colors.WHITE + name + Colors.GREEN + " wurde entstummt.";
    }

    public static String muteNachricht(String grund, String dauer, String muter) {
        return Colors.PREFIX + Colors.RED + "Du wurdest stummgeschaltet!\n"
                + Colors.GRAY + "Grund: " + Colors.WHITE + grund + "\n"
                + Colors.GRAY + "Dauer: " + Colors.WHITE + dauer + "\n"
                + Colors.GRAY + "Von: " + Colors.WHITE + muter;
    }

    public static String muteChatBlockiert(String grund, String dauer) {
        return Colors.PREFIX + Colors.RED + "Du bist stummgeschaltet! "
                + Colors.GRAY + "Grund: " + Colors.WHITE + grund
                + Colors.GRAY + " | Verbleibend: " + Colors.WHITE + dauer;
    }

    // ==================== /coins ====================
    public static final String COINS_NUTZUNG = Colors.PREFIX + Colors.RED + "Nutzung: /coins [Spieler]";
    public static final String COINS_PRIVAT  = Colors.PREFIX + Colors.RED + "Dieser Spieler hat seinen Kontostand auf privat gestellt.";

    public static String coinsEigen(long betrag) {
        return Colors.PREFIX + Colors.GRAY + "Dein Kontostand: " + Colors.YELLOW
                + String.format("%,d", betrag) + Colors.GRAY + " Coins";
    }

    public static String coinsSpieler(String name, long betrag) {
        return Colors.PREFIX + Colors.GRAY + "Kontostand von " + Colors.WHITE + name + Colors.GRAY + ": "
                + Colors.YELLOW + String.format("%,d", betrag) + Colors.GRAY + " Coins";
    }

    // ==================== /pay ====================
    public static final String PAY_NUTZUNG        = Colors.PREFIX + Colors.RED + "Nutzung: /pay <Spieler> <Betrag>";
    public static final String PAY_SELBST         = Colors.PREFIX + Colors.RED + "Du kannst nicht an dich selbst überweisen.";
    public static final String PAY_INVALID_BETRAG = Colors.PREFIX + Colors.RED + "Bitte gib einen gültigen positiven Betrag ein.";
    public static final String PAY_NICHT_GENUG    = Colors.PREFIX + Colors.RED + "Du hast nicht genug Coins.";
    public static final String PAY_SPIELER_OFFLINE = Colors.PREFIX + Colors.RED + "Dieser Spieler ist nicht online.";

    public static String payGesendet(String empfaenger, long betrag) {
        return Colors.PREFIX + Colors.GREEN + "Du hast " + Colors.YELLOW + String.format("%,d", betrag)
                + Colors.GREEN + " Coins an " + Colors.WHITE + empfaenger + Colors.GREEN + " überwiesen.";
    }

    public static String payErhalten(String sender, long betrag) {
        return Colors.PREFIX + Colors.GREEN + "Du hast " + Colors.YELLOW + String.format("%,d", betrag)
                + Colors.GREEN + " Coins von " + Colors.WHITE + sender + Colors.GREEN + " erhalten.";
    }

    // ==================== /home ====================
    public static final String HOME_NICHT_GEFUNDEN      = Colors.PREFIX + Colors.RED  + "Dieses Zuhause existiert nicht.";
    public static final String HOME_WELT_NICHT_GEFUNDEN = Colors.PREFIX + Colors.RED  + "Die Welt dieses Zuhauses wurde nicht gefunden.";
    public static final String HOME_NAME_ZU_LANG        = Colors.PREFIX + Colors.RED  + "Der Name ist zu lang (max. 20 Zeichen).";
    public static final String HOME_ABGEBROCHEN         = Colors.PREFIX + Colors.GRAY + "Erstellung des Zuhauses wurde abgebrochen.";
    public static final String HOME_LOESCHEN_ABGEBROCHEN = Colors.PREFIX + Colors.GRAY + "Löschvorgang abgebrochen.";
    public static final String HOME_EINGABE_WARTEN      = Colors.PREFIX + Colors.GRAY + "Gib einen Namen für dein neues Zuhause ein "
                                                          + Colors.GRAY + "(tippe " + Colors.RED + "cancel" + Colors.GRAY + " zum Abbrechen).";

    public static String homeLimitErreicht(int max) {
        return Colors.PREFIX + Colors.RED + "Du hast dein Zuhause-Limit erreicht "
                + Colors.GRAY + "(max. " + Colors.YELLOW + max + Colors.GRAY + ").";
    }

    public static String homeGesetzt(String name) {
        return Colors.PREFIX + Colors.GREEN + "Zuhause " + Colors.YELLOW + "'" + name + "'"
                + Colors.GREEN + " wurde erfolgreich gesetzt!";
    }

    public static String homeGeloescht(String name) {
        return Colors.PREFIX + Colors.GREEN + "Zuhause " + Colors.YELLOW + "'" + name + "'"
                + Colors.GREEN + " wurde gelöscht.";
    }

    public static String homeLoeschenFehler(String name) {
        return Colors.PREFIX + Colors.RED + "Zuhause " + Colors.YELLOW + "'" + name + "'"
                + Colors.RED + " konnte nicht gelöscht werden.";
    }

    public static String homeTeleportiert(String name) {
        return Colors.PREFIX + Colors.GRAY + "Zum Zuhause " + Colors.DARK_AQUA + name + Colors.GRAY + " teleportiert.";
    }

    // ==================== /bp ====================
    public static final String BP_GEOEFFNET         = Colors.GREEN  + "Rucksack geöffnet!";
    public static final String BP_GESCHLOSSEN        = Colors.GREEN  + "Rucksack geschlossen!";
    public static final String BP_SHULKER_VERBOTEN   = Colors.RED    + "Shulkerboxen dürfen nicht in den Rucksack gelegt werden.";
    public static final String BP_SPIELER_OFFLINE    = Colors.PREFIX + Colors.RED + "Dieser Spieler ist offline oder hat noch nie gespielt.";
    public static final String BP_NUTZUNG            = Colors.PREFIX + Colors.RED + "Nutzung: /bp [open|help|backup|restore]";

    // ==================== /spawn ====================
    public static final String SPAWN_WELT_NICHT_GEFUNDEN = Colors.PREFIX + Colors.RED  + "Die Oberwelt wurde nicht gefunden!";
    public static final String SPAWN_TELEPORTIERE        = Colors.GRAY  + "Teleportiere...";

    public static String spawnTeleportireIn(int sekunden) {
        return Colors.GRAY + "Teleportiere in " + Colors.YELLOW + sekunden;
    }

    // ==================== /setspawn ====================
    // (Die Bestätigungsnachricht wird als Adventure-Component gebaut, Texte hier als Konstanten)
    public static final String SETSPAWN_BESTAETIGEN_TEXT  = "Bist du sicher, dass du den Spawn hier setzen möchtest? Tippe ";
    public static final String SETSPAWN_BESTAETIGEN_CMD   = "/setspawn confirm";
    public static final String SETSPAWN_BESTAETIGEN_HOVER = "Klicken zum Bestätigen";
    public static final String SETSPAWN_GESETZT           = Colors.PREFIX + Colors.GREEN + "Spawn wurde erfolgreich an deiner aktuellen Position gesetzt.";
    public static final String SETSPAWN_NUTZUNG           = Colors.PREFIX + Colors.RED   + "Nutzung: " + Colors.YELLOW + "/setspawn confirm";

    // ==================== /ping ====================
    public static String pingEigen(int ms) {
        return Colors.PREFIX + Colors.YELLOW + "Dein Ping: " + pingFarbe(ms) + ms + "ms";
    }

    public static String ping(String name, int ms) {
        return Colors.PREFIX + Colors.WHITE + name + Colors.GRAY + ": " + pingFarbe(ms) + ms + "ms";
    }

    private static String pingFarbe(int ms) {
        if (ms <= 60)   return Colors.GREEN;
        if (ms <= 150)  return Colors.YELLOW;
        return Colors.RED;
    }

    // ==================== /playtime ====================
    public static final String SPIELZEIT_NUTZUNG       = Colors.PREFIX + Colors.RED + "Nutzung: /playtime [Spieler]";
    public static final String SPIELZEIT_SPIELER_OFFLINE = Colors.PREFIX + Colors.RED + "Dieser Spieler ist nicht online.";

    public static String spielzeitEigen(String dauer) {
        return Colors.PREFIX + Colors.YELLOW + "Deine Spielzeit: " + Colors.GREEN + dauer;
    }

    public static String spielzeitSpieler(String name, String dauer) {
        return Colors.PREFIX + Colors.YELLOW + name + Colors.GRAY + "'s Spielzeit: " + Colors.GREEN + dauer;
    }

    /** Formats playtime in German: "3 Tage, 5 Stunden, 12 Minuten" */
    public static String formatSpielzeit(int totalMinutes) {
        int days    = totalMinutes / 1440;
        int hours   = totalMinutes % 1440 / 60;
        int minutes = totalMinutes % 60;
        return days + " Tage, " + hours + " Stunden, " + minutes + " Minuten";
    }

    // ==================== /uuid ====================
    public static final String UUID_NUTZUNG      = Colors.PREFIX + Colors.RED  + "Nutzung: /uuid [Spieler]";
    public static final String UUID_NICHT_GEFUNDEN = Colors.PREFIX + Colors.RED + "Spieler nicht gefunden.";
    public static final String UUID_KOPIEREN_HOVER = "Zum Kopieren klicken";

    // ==================== /rename ====================
    public static final String RENAME_NUTZUNG    = Colors.PREFIX + Colors.RED   + "Nutzung: /rename <Name>";
    public static final String RENAME_KEIN_ITEM  = Colors.PREFIX + Colors.RED   + "Du musst ein Item in der Hand halten.";

    public static String renameErfolgreich(String name) {
        return Colors.PREFIX + Colors.GREEN + "Item wurde umbenannt in: " + Colors.RESET + name;
    }

    // ==================== /hat ====================
    public static final String HAT_KEIN_ITEM    = Colors.PREFIX + Colors.RED   + "Du musst ein Item in der Hand halten.";
    public static final String HAT_ANGEWENDET   = Colors.PREFIX + Colors.GREEN + "Dein Hut wurde angewendet.";

    // ==================== /day ====================
    public static String tagAnzeige(long tag, long zeit, String zeitLabel) {
        return Colors.PREFIX + Colors.GRAY + "Aktueller Tag: " + Colors.YELLOW + tag
                + Colors.GRAY + "\n" + Colors.PREFIX + Colors.GRAY + "Aktuelle Zeit: " + Colors.YELLOW + zeit + " (" + zeitLabel + ")";
    }

    // ==================== /invsee ====================
    public static final String INVSEE_NUTZUNG       = Colors.PREFIX + Colors.RED + "Nutzung: /invsee <Spieler>";
    public static final String INVSEE_SPIELER_OFFLINE = Colors.PREFIX + Colors.RED + "Dieser Spieler ist nicht online.";

    // ==================== /ec ====================
    public static final String EC_NUTZUNG          = Colors.PREFIX + Colors.RED + "Nutzung: /ec [Spieler]";
    public static final String EC_SPIELER_OFFLINE  = Colors.PREFIX + Colors.RED + "Dieser Spieler ist nicht online.";

    // ==================== /lastdeath ====================
    public static final String LETZTER_TOD_NIE_GESTORBEN = Colors.PREFIX + Colors.RED + "Du bist noch nie gestorben.";

    public static String letzterTodInfo(String todesursache, int minutenSeit, int x, int y, int z) {
        return Colors.PREFIX + Colors.YELLOW + "Letzte " + Colors.RED + "Todesinformationen" + Colors.YELLOW + ":\n"
                + Colors.GREEN + "Todesursache: " + Colors.RED + todesursache
                + Colors.GREEN + "\nZeit seit Tod: " + Colors.RED + minutenSeit + " Minuten"
                + Colors.GREEN + "\nX: " + Colors.RED + x
                + Colors.GREEN + " Y: " + Colors.RED + y
                + Colors.GREEN + " Z: " + Colors.RED + z;
    }

    // ==================== /bugreport ====================
    public static final String BUGREPORT_LINK_TEXT  = "Melde Bugs unter: ";
    public static final String BUGREPORT_LINK_URL   = "dasumc.de/bugreport";
    public static final String BUGREPORT_LINK_HOVER = "Klicken um die Bugreport-Seite zu öffnen";

    // ==================== /tpa ====================
    public static final String TPA_SELBST        = Colors.PREFIX + Colors.RED + "Du kannst keine Teleportanfrage an dich selbst senden.";
    public static final String TPA_KEINE_ANFRAGE = Colors.PREFIX + Colors.RED + "Du hast keine ausstehende Teleportanfrage.";

    public static String tpaGesendet(String empfaenger) {
        return Colors.PREFIX + Colors.GREEN + "Teleportanfrage an " + Colors.WHITE + empfaenger + Colors.GREEN + " gesendet.";
    }

    public static String tpaErhalten(String sender) {
        return Colors.PREFIX + Colors.WHITE + sender + Colors.GRAY + " möchte zu dir teleportieren. "
                + Colors.GREEN + "/tpaccept" + Colors.GRAY + " | " + Colors.RED + "/tpdeny";
    }

    // ==================== /container ====================
    public static final String CONTAINER_GESPERRT          = Colors.PREFIX + Colors.GREEN + "Container wurde gesperrt.";
    public static final String CONTAINER_ENTSPERRT         = Colors.PREFIX + Colors.GREEN + "Container wurde entsperrt.";
    public static final String CONTAINER_KEINE_BERECHTIGUNG = Colors.PREFIX + Colors.RED  + "Dieser Container ist gesperrt!";

    // ==================== /settings (ActionBars) ====================
    public static final String SETTINGS_INFO_AN       = "§7Dein Profil ist jetzt §asichtbar§7.";
    public static final String SETTINGS_INFO_AUS      = "§7Dein Profil ist jetzt §cprivat§7.";
    public static final String SETTINGS_RUCKSACK_AN  = "§7Rucksack-Item wurde §aaktiviert§7.";
    public static final String SETTINGS_RUCKSACK_AUS = "§7Rucksack-Item wurde §cdeaktiviert§7.";
    public static final String SETTINGS_MSG_AN       = "§7Private Nachrichten wurden §aaktiviert§7.";
    public static final String SETTINGS_MSG_AUS      = "§7Private Nachrichten wurden §cdeaktiviert§7.";
    public static final String SETTINGS_GUTHABEN_AN  = "§7Dein Guthaben ist jetzt für andere §asichtbar§7.";
    public static final String SETTINGS_GUTHABEN_AUS = "§7Dein Guthaben ist jetzt §cprivat§7.";
    public static final String SETTINGS_RUCKSACK_FARBE_NICHT_AKTIV = "§cAktiviere zuerst dein Rucksack-Item!";
}
