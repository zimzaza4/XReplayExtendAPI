package re.imc.xreplayextendapi.custom.record;

import org.bukkit.entity.Player;

public interface CustomRecord {

    void execute(Player player);
    String serialize();
}
