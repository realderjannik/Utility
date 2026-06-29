<div align="center">

# Utility Plugin

**A comprehensive all-in-one utility plugin for Paper 1.21.10 Minecraft servers.**

[![Version](https://img.shields.io/badge/version-1.6.1-blue?style=flat-square)](https://github.com/realderjannik/Utility/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21.10-orange?style=flat-square)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-red?style=flat-square)](https://adoptium.net)
[![LuckPerms](https://img.shields.io/badge/LuckPerms-optional-green?style=flat-square)](https://luckperms.net)

---

🇬🇧 [English](#english) &nbsp;|&nbsp; 🇩🇪 [Deutsch](#deutsch)

</div>

---

<a name="english"></a>
## 🇬🇧 English

### Overview

Utility is a feature-rich Paper plugin that bundles everything a modern survival or semi-vanilla server needs into one clean package — from a backpack and home system to private messaging, an economy, a ban system, and a fully customized scoreboard/tab list. All systems are deeply integrated with LuckPerms for rank-based limits and styled output.

---

### Requirements

| Requirement | Version |
|---|---|
| [Paper](https://papermc.io) | 1.21.10 |
| Java | 21+ |
| [LuckPerms](https://luckperms.net) *(optional)* | 5.x |
| [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) *(optional)* | any |

---

### Installation

1. Download the latest `Utility-x.x.x.jar` from [Releases](https://github.com/realderjannik/Utility/releases).
2. Drop the JAR into your server's `plugins/` folder.
3. Start the server once to generate `config.yml`.
4. Configure `config.yml` to your needs (rank groups, limits, colors, …).
5. Restart the server. Done.

---

### Features

#### 🎒 Backpack System
Each player has a personal backpack that persists across sessions. The number of slots is determined by the player's LuckPerms group. Shulker boxes can optionally be blocked from being placed inside.

| Rank (example) | Slots |
|---|---|
| `admin` | 54 |
| `moderator` | 36 |
| `streamer` | 27 |
| `vip` | 27 |
| `subscriber` | 18 |
| `default` | 9 |

> Slot counts and group names are fully configurable in `config.yml` under `backpack.rank-slots`.

---

#### 🏠 Home System
Players can save named home locations and teleport to them through a GUI. The maximum number of homes is controlled per LuckPerms group.

| Rank (example) | Max Homes |
|---|---|
| `admin` | Unlimited |
| `moderator` | 10 |
| `streamer` | 7 |
| `vip` | 5 |
| `subscriber` | 4 |
| `default` | 3 |

> `-1` in the config means unlimited. All values are configurable under `homes.rank-homes`.

---

#### 💰 Economy / Coins
A lightweight coin economy. Players can check their balance, send coins to others, and view their transaction history. Balance visibility can be set to private via `/settings`.

- `/coins` — view your own balance
- `/coins <player>` — view another player's balance (respects their privacy setting; bypassed with `utility.coins.others`)
- `/pay <player> <amount>` — transfer coins
- `/payments` — view transaction history

---

#### 📬 Private Messaging
Players can send each other direct messages and reply to the last conversation partner. Private messages can be disabled per player via `/settings`.

- `/msg <player> <message>` — send a private message
- `/r <message>` — reply to the last message
- If the recipient has messages disabled, the sender is notified.

---

#### 🔨 Ban System
A full ban system with timed and permanent bans, YAML-based persistence, and pre-login blocking.

- `/ban <player> <reason> [time]` — ban a player (`1h`, `12h`, `7d`, `30d`, `permanent`)
- `/unban <player>` — remove a ban
- Banned players are kicked immediately if online.
- At login, banned players see a formatted kick screen with reason, duration, and the banning staff member.
- Expired bans are automatically cleaned up on the next login attempt.

---

#### 🏅 Rank System (LuckPerms)
When LuckPerms is present, the plugin reads each player's primary group and applies:

- **Gradient-colored rank prefix** in chat, tab list, scoreboard, and nametag
- **Rank-based backpack slots** and **home limits**
- Tab list and chat format are fully configurable

Gradients per group are defined in `config.yml` under `ranks.gradients`:
```yaml
ranks:
  gradients:
    admin:
      colors: ["#FF4444", "#AA0000"]
    default:
      colors: ["#AAAAAA", "#FFFFFF"]
```

---

#### 📊 Scoreboard & Tab List
A live sidebar scoreboard shows player stats (ping, playtime, coins, kills, deaths) using a custom bitmap font from the bundled resource pack. The tab list header/footer updates with the online player count and server name.

---

#### 🎛️ Settings GUI
Players open `/settings` to toggle personal preferences:

| Setting | Default |
|---|---|
| Private Messages | On |
| Sit (sit on stairs) | On |
| Backpack item in hotbar | On |
| Backpack color | — |
| Show Balance to others | On |

---

#### 📦 Resource Pack (auto-deploy)
The plugin can host and auto-push a custom resource pack to joining players. Configure `resource-pack.server-ip`, `resource-pack.port` (default `8080`) and `resource-pack.prompt` in `config.yml`. The resource pack includes:

- Custom bitmap font with icons (time, kills, deaths, playtime, ping, coins, rank logo)
- Small-caps font for rank prefixes
- Negative-advance characters for scoreboard label backgrounds

---

#### 🔒 Container Locking
Players can lock their chests and other containers with `/container`. Locked containers can only be accessed by their owner.

---

### Commands

| Command | Aliases | Permission | Default | Description |
|---|---|---|---|---|
| `/bp` | `backpack` | `utility.backpack` | all | Open your personal backpack |
| `/bp open <player>` | — | op | op | Open another player's backpack |
| `/bp backup <player>` | — | op | op | Save a backup of a player's backpack |
| `/bp restore <uuid> <player>` | — | op | op | Restore a backpack from backup |
| `/ban <player> <reason> [time]` | — | `utility.ban` | op | Ban a player |
| `/unban <player>` | — | `utility.ban` | op | Unban a player |
| `/bugreport` | `bug`, `ifoundabug` | — | all | Get the bug report link |
| `/coins [player]` | `balance`, `bal`, `guthaben` | — | all | Show a coin balance |
| `/pay <player> <amount>` | `transfer` | — | all | Transfer coins |
| `/payments` | `transactions` | — | all | View transaction history |
| `/color` | — | `utility.chat.use` | all | Show chat color codes |
| `/container` | — | — | all | Lock/unlock a container |
| `/craft` | `workbench`, `cr` | `utility.craftingtable` | all | Open a crafting table |
| `/day` | `currentday` | `utility.day` | all | Show current in-game day & time |
| `/ec [player]` | `enderchest` | `utility.enderchest` | all | Open ender chest |
| `/hat` | — | `utility.hat` | op | Wear held item as hat |
| `/head [player]` | — | — | op | Open HeadDatabase GUI |
| `/home` | `waypoints`, `qtp` | `utility.home` | all | Open home GUI |
| `/invsee <player>` | `openinv` | `utility.invsee` | op | View a player's inventory |
| `/lastdeath` | `death`, `deathcause` | `utility.lastdeath` | all | Show last death info |
| `/msg <player> <message>` | `whisper`, `w`, `tell`, `dm` | — | all | Send a private message |
| `/r <message>` | `reply`, `antworten` | — | all | Reply to last message |
| `/ping` | `ms` | `utility.ping` | all | Show your ping |
| `/playtime [player]` | `pt`, `spielzeit` | `utility.playtime` | all | Show playtime |
| `/rename <name>` | — | `utility.rename` | op | Rename held item |
| `/settings` | `options`, `configure` | — | all | Open settings GUI |
| `/setspawn` | — | `utility.setspawn` | op | Set the world spawn |
| `/spawn` | — | `utility.spawn` | all | Teleport to spawn |
| `/tpa <player>` | — | — | all | Send teleport request |
| `/tpaccept` | — | — | all | Accept teleport request |
| `/tpdeny` | — | — | all | Deny teleport request |
| `/uuid [player]` | `uniqueid` | `utility.uuid` | all | Show a UUID (click to copy) |
| `/utility reload` | `util` | op | op | Reload the plugin |

---

### Permissions

| Permission | Default | Description |
|---|---|---|
| `utility.backpack` | all | Use `/bp` |
| `utility.ban` | op | Use `/ban` and `/unban` |
| `utility.chat.use` | all | Use `/color` |
| `utility.chat.color.*` | op | Use all chat colors |
| `utility.chat.format.*` | op | Use all chat formatting |
| `utility.coins.others` | op | View other players' balance (ignores privacy setting) |
| `utility.craftingtable` | all | Use `/craft` |
| `utility.day` | all | Use `/day` |
| `utility.enderchest` | all | Use `/ec` |
| `utility.hat` | op | Use `/hat` |
| `utility.home` | all | Use `/home` |
| `utility.invsee` | op | Use `/invsee` |
| `utility.lastdeath` | all | Use `/lastdeath` |
| `utility.ping` | all | Use `/ping` |
| `utility.playtime` | all | Use `/playtime` |
| `utility.rename` | op | Use `/rename` |
| `utility.setspawn` | op | Use `/setspawn` |
| `utility.spawn` | all | Use `/spawn` |
| `utility.uuid` | all | Use `/uuid` |

---

### Configuration (`config.yml`)

<details>
<summary>Click to expand full config reference</summary>

```yaml
resource-pack:
  enabled: true
  server-ip: ""        # Public server IP for resource pack hosting
  port: 8080           # HTTP port (must be open in firewall)
  prompt: "..."        # Message shown in the resource pack dialog

backpack:
  slots: 9             # Fallback slot count (if no rank matches)
  allow-shulkers: false
  allow-admin-edit: true
  rank-slots:          # LuckPerms group name (lowercase) → slot count
    admin: 54
    default: 9

homes:
  max-homes: 3         # Fallback limit
  rank-homes:          # -1 = unlimited
    admin: -1
    default: 3

tpa:
  delay-seconds: 3
  expire-seconds: 60
  show-particles: true

server:
  name: "YourServer"
  website: "yourserver.com"

ranks:
  enabled: true
  show-in-tab: true
  show-in-chat: true
  show-in-scoreboard: true
  show-in-nametag: true
  tab-format: "{prefix} | {name}"
  chat-format: "{prefix} | {name}"
  gradients:
    admin:
      colors: ["#FF4444", "#AA0000"]
    default:
      colors: ["#AAAAAA", "#FFFFFF"]

use-command:           # Set to false to disable individual commands
  bp: true
  day: true
  # ...
```

</details>

---

<br>

---

<a name="deutsch"></a>
## 🇩🇪 Deutsch

### Überblick

Utility ist ein umfangreiches Paper-Plugin, das alles bündelt, was ein moderner Survival- oder Semi-Vanilla-Server braucht — vom Rucksack- und Home-System über private Nachrichten und eine Wirtschaft bis hin zu einem Ban-System und einem vollständig angepassten Scoreboard. Alle Systeme sind tief mit LuckPerms für rangbasierte Limits und farblich gestaltete Ausgaben integriert.

---

### Voraussetzungen

| Anforderung | Version |
|---|---|
| [Paper](https://papermc.io) | 1.21.10 |
| Java | 21+ |
| [LuckPerms](https://luckperms.net) *(optional)* | 5.x |
| [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/) *(optional)* | beliebig |

---

### Installation

1. Lade die aktuelle `Utility-x.x.x.jar` unter [Releases](https://github.com/realderjannik/Utility/releases) herunter.
2. Lege die JAR in den `plugins/`-Ordner deines Servers.
3. Starte den Server einmal, um `config.yml` zu generieren.
4. Passe `config.yml` nach deinen Wünschen an (Rang-Gruppen, Limits, Farben, …).
5. Server neu starten. Fertig.

---

### Features

#### 🎒 Rucksack-System
Jeder Spieler hat einen persönlichen Rucksack, der sitzungsübergreifend gespeichert wird. Die Anzahl der Slots richtet sich nach der LuckPerms-Gruppe des Spielers. Shulkerboxen können optional blockiert werden.

| Rang (Beispiel) | Slots |
|---|---|
| `admin` | 54 |
| `moderator` | 36 |
| `streamer` | 27 |
| `vip` | 27 |
| `subscriber` | 18 |
| `default` | 9 |

> Slot-Zahlen und Gruppennamen sind in `config.yml` unter `backpack.rank-slots` frei konfigurierbar.

---

#### 🏠 Home-System
Spieler können benannte Heimat-Standorte speichern und per GUI zu ihnen teleportieren. Das Maximum an Homes wird pro LuckPerms-Gruppe gesteuert.

| Rang (Beispiel) | Max. Homes |
|---|---|
| `admin` | Unbegrenzt |
| `moderator` | 10 |
| `streamer` | 7 |
| `vip` | 5 |
| `subscriber` | 4 |
| `default` | 3 |

> `-1` in der Config bedeutet unbegrenzt. Alle Werte sind unter `homes.rank-homes` konfigurierbar.

---

#### 💰 Wirtschaft / Coins
Eine einfache Coin-Wirtschaft. Spieler können ihr Guthaben einsehen, Coins versenden und ihre Transaktionshistorie abrufen. Die Sichtbarkeit des Guthabens kann über `/settings` auf privat gestellt werden.

- `/coins` — eigenes Guthaben anzeigen
- `/coins <Spieler>` — Guthaben eines anderen Spielers anzeigen (respektiert Privat-Einstellung; wird mit `utility.coins.others` umgangen)
- `/pay <Spieler> <Betrag>` — Coins überweisen
- `/payments` — Transaktionshistorie anzeigen

---

#### 📬 Private Nachrichten
Spieler können sich gegenseitig Direktnachrichten senden und auf die letzte Konversation antworten. Private Nachrichten können per `/settings` individuell deaktiviert werden.

- `/msg <Spieler> <Nachricht>` — Direktnachricht senden
- `/r <Nachricht>` — Auf die letzte Nachricht antworten
- Wenn der Empfänger Nachrichten deaktiviert hat, wird der Absender benachrichtigt.

---

#### 🔨 Ban-System
Ein vollständiges Ban-System mit zeitlich begrenzten und permanenten Bans, YAML-Persistenz und Pre-Login-Blocking.

- `/ban <Spieler> <Grund> [Zeit]` — Spieler bannen (`1h`, `12h`, `7d`, `30d`, `permanent`)
- `/unban <Spieler>` — Ban aufheben
- Gebannte Spieler werden sofort gekickt, falls sie online sind.
- Beim Login sehen gebannte Spieler einen formatierten Kick-Screen mit Grund, Dauer und dem ban-ausführenden Teammitglied.
- Abgelaufene Bans werden automatisch beim nächsten Login bereinigt.

---

#### 🏅 Rang-System (LuckPerms)
Wenn LuckPerms vorhanden ist, liest das Plugin die primäre Gruppe jedes Spielers aus und wendet folgendes an:

- **Farbverlauf-Rang-Präfix** in Chat, Tab-Liste, Scoreboard und Nametag
- **Rangbasierte Rucksack-Slots** und **Home-Limits**
- Tab-Liste und Chat-Format sind vollständig konfigurierbar

Farbverläufe pro Gruppe werden in `config.yml` unter `ranks.gradients` definiert:
```yaml
ranks:
  gradients:
    admin:
      colors: ["#FF4444", "#AA0000"]
    default:
      colors: ["#AAAAAA", "#FFFFFF"]
```

---

#### 📊 Scoreboard & Tab-Liste
Ein live-aktualisiertes Sidebar-Scoreboard zeigt Spielerstatistiken (Ping, Spielzeit, Coins, Kills, Tode) mit einer benutzerdefinierten Bitmap-Schriftart aus dem enthaltenen Ressourcen-Paket. Die Tab-Listen-Kopf-/Fußzeile wird mit der aktuellen Spielerzahl und dem Servernamen aktualisiert.

---

#### 🎛️ Einstellungen-GUI
Spieler öffnen `/settings`, um persönliche Einstellungen zu ändern:

| Einstellung | Standard |
|---|---|
| Private Nachrichten | An |
| Sitzen (auf Treppen) | An |
| Rucksack-Item in Hotbar | An |
| Rucksack-Farbe | — |
| Guthaben für andere sichtbar | An |

---

#### 📦 Ressourcen-Paket (auto-deploy)
Das Plugin kann ein benutzerdefiniertes Ressourcen-Paket hosten und beim Beitritt automatisch an Spieler senden. Konfiguriere `resource-pack.server-ip`, `resource-pack.port` (Standard: `8080`) und `resource-pack.prompt` in `config.yml`. Das Ressourcen-Paket enthält:

- Benutzerdefinierte Bitmap-Schriftart mit Icons (Zeit, Kills, Tode, Spielzeit, Ping, Coins, Rang-Logo)
- Kapitälchen-Schriftart für Rang-Präfixe
- Negative-Advance-Zeichen für Scoreboard-Label-Hintergründe

---

#### 🔒 Container-Sperrsystem
Spieler können ihre Truhen und andere Container mit `/container` sperren. Gesperrte Container können nur vom Besitzer geöffnet werden.

---

### Befehle

| Befehl | Aliase | Berechtigung | Standard | Beschreibung |
|---|---|---|---|---|
| `/bp` | `backpack` | `utility.backpack` | alle | Persönlichen Rucksack öffnen |
| `/bp open <Spieler>` | — | op | op | Rucksack eines anderen Spielers öffnen |
| `/bp backup <Spieler>` | — | op | op | Rucksack-Backup speichern |
| `/bp restore <uuid> <Spieler>` | — | op | op | Rucksack aus Backup wiederherstellen |
| `/ban <Spieler> <Grund> [Zeit]` | — | `utility.ban` | op | Spieler bannen |
| `/unban <Spieler>` | — | `utility.ban` | op | Spieler entbannen |
| `/bugreport` | `bug`, `ifoundabug` | — | alle | Bugreport-Link anzeigen |
| `/coins [Spieler]` | `balance`, `bal`, `guthaben` | — | alle | Kontostand anzeigen |
| `/pay <Spieler> <Betrag>` | `transfer`, `überweisen` | — | alle | Coins überweisen |
| `/payments` | `transaktionen` | — | alle | Transaktionshistorie anzeigen |
| `/color` | — | `utility.chat.use` | alle | Chat-Farben anzeigen |
| `/container` | — | — | alle | Container sperren/entsperren |
| `/craft` | `workbench`, `cr` | `utility.craftingtable` | alle | Handwerksstation öffnen |
| `/day` | `currentday` | `utility.day` | alle | Aktuellen Spieltag & Uhrzeit anzeigen |
| `/ec [Spieler]` | `enderchest` | `utility.enderchest` | alle | Enderchest öffnen |
| `/hat` | — | `utility.hat` | op | Gehaltenes Item als Hut tragen |
| `/head [Spieler]` | — | — | op | HeadDatabase-GUI öffnen |
| `/home` | `waypoints`, `qtp` | `utility.home` | alle | Home-GUI öffnen |
| `/invsee <Spieler>` | `openinv` | `utility.invsee` | op | Inventar eines Spielers einsehen |
| `/lastdeath` | `death`, `deathcause` | `utility.lastdeath` | alle | Letzten Tod anzeigen |
| `/msg <Spieler> <Nachricht>` | `whisper`, `w`, `tell`, `dm` | — | alle | Private Nachricht senden |
| `/r <Nachricht>` | `reply`, `antworten` | — | alle | Auf letzte Nachricht antworten |
| `/ping` | `ms` | `utility.ping` | alle | Eigenen Ping anzeigen |
| `/playtime [Spieler]` | `pt`, `spielzeit` | `utility.playtime` | alle | Spielzeit anzeigen |
| `/rename <Name>` | — | `utility.rename` | op | Gehaltenes Item umbenennen |
| `/settings` | `options`, `configure` | — | alle | Einstellungen-GUI öffnen |
| `/setspawn` | — | `utility.setspawn` | op | Welt-Spawn setzen |
| `/spawn` | — | `utility.spawn` | alle | Zum Spawn teleportieren |
| `/tpa <Spieler>` | — | — | alle | Teleportanfrage senden |
| `/tpaccept` | — | — | alle | Teleportanfrage annehmen |
| `/tpdeny` | — | — | alle | Teleportanfrage ablehnen |
| `/uuid [Spieler]` | `uniqueid` | `utility.uuid` | alle | UUID anzeigen (klicken zum Kopieren) |
| `/utility reload` | `util` | op | op | Plugin neu laden |

---

### Berechtigungen

| Berechtigung | Standard | Beschreibung |
|---|---|---|
| `utility.backpack` | alle | `/bp` verwenden |
| `utility.ban` | op | `/ban` und `/unban` verwenden |
| `utility.chat.use` | alle | `/color` verwenden |
| `utility.chat.color.*` | op | Alle Chat-Farben verwenden |
| `utility.chat.format.*` | op | Alle Chat-Formatierungen verwenden |
| `utility.coins.others` | op | Fremdes Guthaben sehen (ignoriert Privat-Einstellung) |
| `utility.craftingtable` | alle | `/craft` verwenden |
| `utility.day` | alle | `/day` verwenden |
| `utility.enderchest` | alle | `/ec` verwenden |
| `utility.hat` | op | `/hat` verwenden |
| `utility.home` | alle | `/home` verwenden |
| `utility.invsee` | op | `/invsee` verwenden |
| `utility.lastdeath` | alle | `/lastdeath` verwenden |
| `utility.ping` | alle | `/ping` verwenden |
| `utility.playtime` | alle | `/playtime` verwenden |
| `utility.rename` | op | `/rename` verwenden |
| `utility.setspawn` | op | `/setspawn` verwenden |
| `utility.spawn` | alle | `/spawn` verwenden |
| `utility.uuid` | alle | `/uuid` verwenden |

---

<div align="center">

Made with ❤️ by [DerJxnnik](https://derjxnnik.fun)

</div>
