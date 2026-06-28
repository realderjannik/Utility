package fun.derjxnnik.currency;

import fun.derjxnnik.misc.LogUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrencyManager {

    private static final long DEFAULT_BALANCE = 1000;
    private static final int MAX_HISTORY = 15;
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final File dataFile;
    private YamlConfiguration data;

    public CurrencyManager(File dataFolder) {
        this.dataFile = new File(dataFolder, "economy.yml");
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                LogUtil.warn("Could not create economy.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            LogUtil.warn("Could not save economy.yml: " + e.getMessage());
        }
    }

    public long getBalance(UUID uuid) {
        return data.getLong("balances." + uuid, DEFAULT_BALANCE);
    }

    public void setBalance(UUID uuid, long amount) {
        data.set("balances." + uuid, amount);
        save();
    }

    public boolean transfer(UUID from, String fromName, UUID to, String toName, long amount) {
        long fromBal = getBalance(from);
        if (fromBal < amount) return false;
        data.set("balances." + from, fromBal - amount);
        data.set("balances." + to, getBalance(to) + amount);
        recordPayment(from, fromName, to, toName, amount);
        save();
        return true;
    }

    private void recordPayment(UUID from, String fromName, UUID to, String toName, long amount) {
        String ts = LocalDateTime.now().format(TIMESTAMP_FMT);
        String entry = ts + "|" + from + "|" + fromName + "|" + to + "|" + toName + "|" + amount;
        for (UUID uuid : new UUID[]{from, to}) {
            String path = "history." + uuid;
            List<String> history = new ArrayList<>(data.getStringList(path));
            history.add(0, entry);
            if (history.size() > MAX_HISTORY) history = history.subList(0, MAX_HISTORY);
            data.set(path, history);
        }
    }

    public List<PaymentRecord> getHistory(UUID uuid) {
        List<PaymentRecord> result = new ArrayList<>();
        for (String raw : data.getStringList("history." + uuid)) {
            String[] p = raw.split("\\|", 6);
            if (p.length < 6) continue;
            try {
                result.add(new PaymentRecord(
                        p[0],
                        UUID.fromString(p[1]), p[2],
                        UUID.fromString(p[3]), p[4],
                        Long.parseLong(p[5])
                ));
            } catch (Exception ignored) {}
        }
        return result;
    }
}
