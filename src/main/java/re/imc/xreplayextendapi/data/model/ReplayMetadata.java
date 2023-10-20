package re.imc.xreplayextendapi.data.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
@DatabaseTable(tableName = "REPLAYMETADATA")
public class ReplayMetadata {

    @DatabaseField(columnName = "REPLAYID", id = true, width = 10)
    private String replayId;


    @DatabaseField(columnName = "METADATA")
    private String metadata;

    public ReplayMetadata metadata(JsonElement json) {
        metadata = json.toString();
        return this;
    }

    public JsonElement metadata() {
        return new JsonParser().parse(metadata);
    }


}
