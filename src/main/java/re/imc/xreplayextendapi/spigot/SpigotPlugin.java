package re.imc.xreplayextendapi.spigot;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.model.ReplayMetadata;
import re.imc.xreplayextendapi.spigot.events.ReplayDeleteEvent;
import re.imc.xreplayextendapi.spigot.listener.PlayerListener;
import re.imc.xreplayextendapi.spigot.listener.ReplayListener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class SpigotPlugin extends JavaPlugin implements Listener {


    @Getter
    private static SpigotPlugin instance;

    private boolean replayPluginInstalled = false;

    @Getter
    private static final Set<String> saveWithMetadataRecords = Sets.newConcurrentHashSet();
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
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
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

    }


    @EventHandler
    public void onReplayPluginLoad(PluginEnableEvent event) {

        if (!event.getPlugin().getName().equalsIgnoreCase("replaysystem")) {
            return;
        }

        xReplayHolder = new XReplayHolder();
        Bukkit.getPluginManager().registerEvents(new ReplayListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        replayPluginInstalled = true;
    }

    @EventHandler
    public void onReplayPluginDisable(PluginDisableEvent event) {

        if (!event.getPlugin().getName().equalsIgnoreCase("replaysystem")) {
            return;
        }

        if (replayPluginInstalled) {
            try {
                XReplayExtendAPI.getInstance().getReplayDataManager().getReplayMetadataDao().createIfNotExists(new ReplayMetadata().replayId(xReplayHolder.getReplayId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
