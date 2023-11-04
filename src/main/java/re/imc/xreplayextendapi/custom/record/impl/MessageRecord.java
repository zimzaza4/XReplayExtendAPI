package re.imc.xreplayextendapi.custom.record.impl;

import org.bukkit.entity.Player;
import re.imc.xreplayextendapi.custom.record.CustomRecord;

public class MessageRecord implements CustomRecord {
    String msg;
    public MessageRecord(String msg) {
        this.msg = msg;
    }
    @Override
    public void execute(Player player) {
        player.sendMessage(msg);
    }

    @Override
    public String serialize() {
        return msg;
    }
}
