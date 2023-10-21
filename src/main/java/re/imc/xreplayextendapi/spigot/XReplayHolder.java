package re.imc.xreplayextendapi.spigot;

import de.musterbukkit.replaysystem.main.ReplayAPI;
import org.bukkit.entity.Player;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.model.ReplayMetadata;

import java.sql.SQLException;

public class XReplayHolder {

    public void playReplay(Player player, String id) {
        ReplayAPI.playReplayID(id, player);
    };

    public void saveReplayWithMetadata(ReplayMetadata metadata) {
        String id = ReplayAPI.getReplayID();
        SpigotPlugin.getSaveWithMetadataRecords().add(id);
        ReplayAPI.saveReplay();
        metadata.replayId(id);
        try {
            XReplayExtendAPI.getInstance().getReplayDataManager().getReplayMetadataDao().createOrUpdate(new ReplayMetadata().replayId(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void createSnapshotWithMetadata(int seconds, ReplayMetadata metadata) {
        String id = ReplayAPI.createSnapshot(seconds);
        SpigotPlugin.getSaveWithMetadataRecords().add(id);
        metadata.replayId(id);
        try {
            XReplayExtendAPI.getInstance().getReplayDataManager().getReplayMetadataDao().createOrUpdate(new ReplayMetadata().replayId(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void createPlayerSnapshotWithMetadata(int seconds, Player player, ReplayMetadata metadata) {
        String id = ReplayAPI.createSnapshotPlayer(seconds, player);
        SpigotPlugin.getSaveWithMetadataRecords().add(id);
        metadata.replayId(id);
        try {
            XReplayExtendAPI.getInstance().getReplayDataManager().getReplayMetadataDao().createOrUpdate(new ReplayMetadata().replayId(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String getReplayId() {

        return ReplayAPI.getReplayID();
    }
}
