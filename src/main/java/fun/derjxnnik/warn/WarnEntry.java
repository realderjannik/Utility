package fun.derjxnnik.warn;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WarnEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final String playerName;
    private final String playerUuid;
    private final String reason;
    private final String warnedByName;
    private final long warnedAt;

    public WarnEntry(String playerName, String playerUuid, String reason,
                     String warnedByName, long warnedAt) {
        this.playerName   = playerName;
        this.playerUuid   = playerUuid;
        this.reason       = reason;
        this.warnedByName = warnedByName;
        this.warnedAt     = warnedAt;
    }

    public String getDateString() {
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(warnedAt), ZoneId.systemDefault());
        return FMT.format(dt);
    }

    public String getPlayerName()   { return playerName; }
    public String getPlayerUuid()   { return playerUuid; }
    public String getReason()       { return reason; }
    public String getWarnedByName() { return warnedByName; }
    public long   getWarnedAt()     { return warnedAt; }
}
