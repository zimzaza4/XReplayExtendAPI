package re.imc.xreplayextendapi.spigot.listener;

import com.google.gson.JsonObject;
import de.musterbukkit.replaysystem.main.ReplaySaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.model.ReplayMetadata;
import re.imc.xreplayextendapi.spigot.SpigotPlugin;

import java.sql.SQLException;

public class ReplayListener implements Listener {

    @EventHandler
    public void onReplaySave(ReplaySaveEvent event) {
        XReplayExtendAPI api = XReplayExtendAPI.getInstance();
        if (api.isEnableMetadata()) {
            new BukkitRunnable() {
                @Override
                public void run() {

                    if (!SpigotPlugin.getSaveWithMetadataRecords().contains(event.getReplayID())) {
                        try {
                            XReplayExtendAPI.getInstance().getReplayDataManager().getReplayMetadataDao().createOrUpdate(new ReplayMetadata().replayId(event.getReplayID()).metadata(new JsonObject()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }.runTaskLaterAsynchronously(SpigotPlugin.getInstance(), 1);
        }
    }
}
