package re.imc.xreplayextendapi;

import lombok.Getter;
import org.bukkit.entity.Player;
import re.imc.xreplayextendapi.data.ReplayDataManager;
import re.imc.xreplayextendapi.data.model.ReplayMetadata;
import re.imc.xreplayextendapi.data.model.ReplayWaitForPlay;
import re.imc.xreplayextendapi.spigot.SpigotPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class XReplayExtendAPI {

    @Getter
    private static XReplayExtendAPI instance;

    private final ReplayDataManager replayDataManager;

    private boolean enableMetadata;

    private boolean enableReplayDeleteEvent = false;

    private int deleteCheckInterval = 10000;

    private final List<Consumer<String>> replayDeleteHandlers = new ArrayList<>();


    public XReplayExtendAPI(Map<Object, Object> config, boolean standalone) {

        enableMetadata = (boolean) config.get("replay-metadata");
        if (!standalone) {
            enableReplayDeleteEvent = (boolean) config.get("replay-delete-event");
            deleteCheckInterval = (int) config.get("delete-check-interval");
        }
        try {
            replayDataManager = new ReplayDataManager();
            replayDataManager.load((Map<Object, Object>) config.get("database"), this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        instance = this;
    }

    public void addToWaitForPlay(UUID uuid, String replayId) {
        try {
            replayDataManager.getReplayWaitForPlayDao().createOrUpdate(new ReplayWaitForPlay(uuid.toString(), replayId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveReplayWithMetadata(ReplayMetadata metadata) {
        SpigotPlugin.getInstance().getXReplayHolder().saveReplayWithMetadata(metadata);
    }

    public void createSnapshotWithMetadata(int seconds, ReplayMetadata metadata) {
        SpigotPlugin.getInstance().getXReplayHolder().createSnapshotWithMetadata(seconds, metadata);
    }

    public void createPlayerSnapshotWithMetadata(int seconds, Player player, ReplayMetadata metadata) {
        SpigotPlugin.getInstance().getXReplayHolder().createPlayerSnapshotWithMetadata(seconds, player, metadata);
    }



}
