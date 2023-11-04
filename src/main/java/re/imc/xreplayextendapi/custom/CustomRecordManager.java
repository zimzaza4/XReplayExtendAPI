package re.imc.xreplayextendapi.custom;

import re.imc.xreplayextendapi.custom.record.CustomRecord;
import re.imc.xreplayextendapi.custom.record.impl.MessageRecord;

import java.util.HashMap;
import java.util.Map;

public class CustomRecordManager {

    private Map<String, Class<? extends CustomRecord>> registeredTypes = new HashMap<>();

    public void registerCustomRecord(String id, Class<? extends CustomRecord> type) {
        registeredTypes.put(id, type);
    }

    public Class<? extends CustomRecord> getType(String id) {
        return registeredTypes.get(id);
    }

    public void init() {
        registerCustomRecord("msg", MessageRecord.class);
    }

}
