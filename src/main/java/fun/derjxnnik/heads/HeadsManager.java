package fun.derjxnnik.heads;

import fun.derjxnnik.misc.Colors;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class HeadsManager {
   private final Map<String, List<HeadEntry>> byCategory = new HashMap<>();
   private final List<String> categories = new ArrayList<>();

   public HeadsManager(File dataFolder) {
      File file = new File(dataFolder, "heads.yml");
      if (file.exists()) {
         try {
            InputStream input = new FileInputStream(file);

            label61: {
               try {
                  LoaderOptions options = new LoaderOptions();
                  options.setCodePointLimit(Integer.MAX_VALUE);
                  Yaml yaml = new Yaml(options);
                  Map<String, Object> root = (Map<String, Object>) yaml.load(input);
                  if (root != null && root.containsKey("heads")) {
                     Map<String, Object> headsMap = (Map<String, Object>) root.get("heads");

                     for (Map.Entry<String, Object> entry : headsMap.entrySet()) {
                        String key = entry.getKey();
                        Map<String, Object> values = (Map<String, Object>) entry.getValue();
                        String cat = (String) values.getOrDefault("category", "Misc");
                        String name = (String) values.getOrDefault("name", key);
                        String tex = (String) values.get("texture");
                        if (tex != null) {
                           HeadEntry head = new HeadEntry(cat, name, tex);
                           byCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(head);
                        }
                     }

                     for (List<HeadEntry> list : byCategory.values()) {
                        list.sort(Comparator.comparing(ex -> Colors.strip(ex.name).toLowerCase()));
                     }

                     categories.addAll(byCategory.keySet());
                     categories.sort(String.CASE_INSENSITIVE_ORDER);
                     Logger var10000 = Bukkit.getLogger();
                     int var10001 = getTotalCount();
                     var10000.info("[Utility] Loaded " + var10001 + " heads in " + categories.size() + " categories.");
                     break label61;
                  }

                  Bukkit.getLogger().warning("[Utility] No 'heads:' section found in heads.yml!");
               } catch (Throwable var17) {
                  try {
                     input.close();
                  } catch (Throwable var16) {
                     var17.addSuppressed(var16);
                  }
                  throw var17;
               }

               input.close();
               return;
            }

            input.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public List<String> getCategories() {
      return Collections.unmodifiableList(categories);
   }

   public List<HeadEntry> getHeadsInCategory(String cat) {
      return byCategory.getOrDefault(cat, Collections.emptyList());
   }

   public int getTotalCount() {
      return byCategory.values().stream().mapToInt(List::size).sum();
   }

   public static class HeadEntry {
      public final String category;
      public final String name;
      public final String texture;

      public HeadEntry(String category, String name, String texture) {
         this.category = category;
         this.name = name;
         this.texture = texture;
      }
   }
}
