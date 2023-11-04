package re.imc.xreplayextendapi.spigot.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.plugin.Plugin;
import re.imc.xreplayextendapi.XReplayExtendAPI;

import java.util.Arrays;

public class ChatListener extends PacketAdapter {
    public ChatListener(Plugin plugin, PacketType type) {
        super(plugin, ListenerPriority.HIGHEST, type);
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        StructureModifier<BaseComponent> modifier = event.getPacket().getSpecificModifier(BaseComponent.class);

        String fullMsg = "";
        boolean isReplayRecord = false;
        BaseComponent baseComponent = modifier.readSafely(0);

        if (baseComponent == null) {
            StructureModifier<BaseComponent[]> spigotModifier = event.getPacket().getSpecificModifier(BaseComponent[].class);
            StringBuilder builder = new StringBuilder();
            BaseComponent[] components = spigotModifier.readSafely(0);
            if (components == null) {
                return;
            }
            boolean first = true;
            for (BaseComponent component : components) {
                if (first) {
                    if (component.toPlainText().startsWith("REPLAY_RECORD::")) {
                        isReplayRecord = true;
                    }
                    first = false;
                }
                builder.append(component.toLegacyText());
            }
            fullMsg = builder.toString();

        } else {
            if (baseComponent.toPlainText().startsWith("REPLAY_RECORD::")) {
                isReplayRecord = true;
            }
            fullMsg = baseComponent.toLegacyText();
        }


        if (!isReplayRecord) {
            return;
        } else {
            event.setCancelled(true);
        }

        String[] values = fullMsg.split("::", 4);

        if (values.length < 4) {
            return;
        }
        String id = values[1];
        String permission = values[2];
        String data = values[3];

        try {
            if (!permission.equals("")) {
                if (!event.getPlayer().hasPermission(permission)) {
                    return;
                }
            }
            XReplayExtendAPI.getInstance().getCustomRecordManager().getType(id).getDeclaredConstructor(String.class).newInstance(data)
                    .execute(event.getPlayer());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
