package re.imc.xreplayextendapi.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.ReplayDataManager;
import re.imc.xreplayextendapi.data.model.ReplayWaitForPlay;
import re.imc.xreplayextendapi.spigot.SpigotPlugin;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CompletableFuture.supplyAsync(() -> {

            ReplayDataManager dataManager = XReplayExtendAPI.getInstance().getReplayDataManager();

            try {
                ReplayWaitForPlay data = dataManager.getReplayWaitForPlayDao().queryForId(String.valueOf(event.getPlayer().getUniqueId()));

                if (data != null) {
                    dataManager.getReplayWaitForPlayDao().delete(data);
                }
                return data;
            } catch (SQLException ignored) {

            }
            return null;
        }).thenAccept(data -> {
            if (data == null) {
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    SpigotPlugin.getInstance().getReplayAction().accept(event.getPlayer(), data.getReplay());
                }
            }.runTask(SpigotPlugin.getInstance());

        });

    }
}
