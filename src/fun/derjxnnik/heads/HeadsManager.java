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
   private final Map byCategory = new HashMap();
   private final List categories = new ArrayList();

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
                  Map<String, Object> root = (Map)yaml.load(input);
                  if (root != null && root.containsKey("heads")) {
                     Map<String, Object> headsMap = (Map)root.get("heads");

                     for(Map.Entry entry : headsMap.entrySet()) {
                        String key = (String)entry.getKey();
                        Map<String, Object> values = (Map)entry.getValue();
                        String cat = (String)values.getOrDefault("category", "Misc");
                        String name = (String)values.getOrDefault("name", key);
                        String tex = (String)values.get("texture");
                        if (tex != null) {
                           HeadEntry head = new HeadEntry(cat, name, tex);
                           ((List)this.byCategory.computeIfAbsent(cat, (k) -> new ArrayList())).add(head);
                        }
                     }

                     for(List list : this.byCategory.values()) {
                        list.sort(Comparator.comparing((ex) -> Colors.strip(ex.name).toLowerCase()));
                     }

                     this.categories.addAll(this.byCategory.keySet());
                     this.categories.sort(String.CASE_INSENSITIVE_ORDER);
                     Logger var10000 = Bukkit.getLogger();
                     int var10001 = this.getTotalCount();
                     var10000.info("[Utility] Loaded " + var10001 + " heads in " + this.categories.size() + " categories.");
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

   public List getCategories() {
      return Collections.unmodifiableList(this.categories);
   }

   public List getHeadsInCategory(String cat) {
      return (List)this.byCategory.getOrDefault(cat, Collections.emptyList());
   }

   public int getTotalCount() {
      return this.byCategory.values().stream().mapToInt(List::size).sum();
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
