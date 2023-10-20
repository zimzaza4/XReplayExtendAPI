package re.imc.xreplayextendapi.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@DatabaseTable(tableName = "REPLAYHISTORY")
public class ReplayHistory {

    @DatabaseField(columnName = "UUID", id = true, width = 36)
    private String uuid;

    @DatabaseField(columnName = "REPLAYS")
    private String replays;

    @DatabaseField(columnName = "REPLAYSELECTED")
    private String replaySelected;

    @DatabaseField(columnName = "FAVOURITE")
    private String favourite;

    @DatabaseField(columnName = "CSAVE")
    private String csave;

    @DatabaseField(columnName = "CREPORT")
    private String creport;

    public String[] replays() {
        return replays.substring(0, replays.length()-1).split(",");
    }

    public String[] favourite() {
        return favourite.substring(0, replays.length()-1).split(",");
    }
}
