package re.imc.xreplayextendapi.spigot;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.spigot.events.ReplayDeleteEvent;
import re.imc.xreplayextendapi.spigot.listener.PlayerListener;
import re.imc.xreplayextendapi.spigot.listener.ReplayListener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class SpigotPlugin extends JavaPlugin {


    @Getter
    private static SpigotPlugin instance;

    @Getter
    private static Set<String> saveWithMetadataRecords = Sets.newConcurrentHashSet();
    @Getter
    @Setter
    private BiConsumer<Player, String> replayAction = (player, replayId) -> {
        if (getXReplayHolder() != null) {
            getXReplayHolder().playReplay(player, replayId);
        }
    };
    @Getter
    private XReplayHolder xReplayHolder;
    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try (FileReader reader = new FileReader(configFile)) {
            Map<Object, Object> config = new Yaml().load(reader);
            XReplayExtendAPI api = new XReplayExtendAPI(config, false);
            if (api.isEnableReplayDeleteEvent()) {
                api.getReplayDeleteHandlers().add((id) -> {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(new ReplayDeleteEvent(id));
                        }
                    }.runTaskAsynchronously(SpigotPlugin.getInstance());
                });

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Bukkit.getPluginManager().getPlugin("ReplaySystem") != null) {
            xReplayHolder = new XReplayHolder();
            Bukkit.getPluginManager().registerEvents(new ReplayListener(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }
    }

    @Override
    public void onDisable() {
    }

}
