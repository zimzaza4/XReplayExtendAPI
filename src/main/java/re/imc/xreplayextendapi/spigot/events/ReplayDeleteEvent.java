package re.imc.xreplayextendapi.spigot.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class ReplayDeleteEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private String replayId;

    public ReplayDeleteEvent(String replayId) {
        super(true);
        this.replayId = replayId;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
