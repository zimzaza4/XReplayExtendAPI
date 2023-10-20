package re.imc.xreplayextendapi.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import re.imc.xreplayextendapi.XReplayExtendAPI;
import re.imc.xreplayextendapi.data.model.ReplayHistory;
import re.imc.xreplayextendapi.data.model.ReplayIndex;
import re.imc.xreplayextendapi.data.model.ReplayMetadata;
import re.imc.xreplayextendapi.data.model.ReplayWaitForPlay;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReplayDataManager {

    @Getter
    private Dao<ReplayHistory, String> replayHistoryDao;
    @Getter
    private Dao<ReplayIndex, String> replayIndexDao;
    @Getter
    private Dao<ReplayWaitForPlay, String> replayWaitForPlayDao;
    @Getter
    private Dao<ReplayMetadata, String> replayMetadataDao;


    public void load(Map<Object, Object> config, XReplayExtendAPI api) throws SQLException {
        String hostname = (String) config.get("hostname");
        String user = (String) config.get("user");
        String password = (String) config.get("password");
        String database = (String) config.get("database");
        String connectionParameters = (String) config.get("connection-parameters");

        String url = "jdbc:mysql://" + hostname + "/" + database + connectionParameters;
        JdbcPooledConnectionSource connection = new JdbcPooledConnectionSource(url, user, password);
        TableUtils.createTableIfNotExists(connection, ReplayWaitForPlay.class);

        replayHistoryDao = DaoManager.createDao(connection, ReplayHistory.class);
        replayIndexDao = DaoManager.createDao(connection, ReplayIndex.class);
        replayWaitForPlayDao = DaoManager.createDao(connection, ReplayWaitForPlay.class);

        if (api.isEnableMetadata()) {
            TableUtils.createTableIfNotExists(connection, ReplayMetadata.class);
            replayMetadataDao = DaoManager.createDao(connection, ReplayMetadata.class);

            if (XReplayExtendAPI.getInstance().isEnableReplayDeleteEvent()) {
                startDeleteCheck(XReplayExtendAPI.getInstance().getDeleteCheckInterval());
            }
        }
    }


    public void startDeleteCheck(int minutes) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                for (ReplayMetadata metadata : replayMetadataDao.queryForAll()) {
                    if (replayIndexDao.queryForId(metadata.replayId()) == null) {
                        replayMetadataDao.deleteById(metadata.replayId());
                        XReplayExtendAPI.getInstance().getReplayDeleteHandlers().forEach(handler -> handler.accept(metadata.replayId()));
                    }

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, minutes, minutes, TimeUnit.MINUTES);
    }

}
