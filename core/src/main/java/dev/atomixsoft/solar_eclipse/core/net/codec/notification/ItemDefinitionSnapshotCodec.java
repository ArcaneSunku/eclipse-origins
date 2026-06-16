package dev.atomixsoft.solar_eclipse.core.net.codec.notification;

import dev.atomixsoft.solar_eclipse.core.net.codec.PacketCodec;
import dev.atomixsoft.solar_eclipse.core.net.data.ItemDefinitionData;
import dev.atomixsoft.solar_eclipse.core.net.packet.notification.ItemDefinitionSnapshotPacket;
import dev.atomixsoft.solar_eclipse.core.utils.CodecUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ItemDefinitionSnapshotCodec implements PacketCodec<ItemDefinitionSnapshotPacket> {
    @Override
    public void encode(ItemDefinitionSnapshotPacket packet, ByteBuf out) {
        out.writeInt(packet.items().size());

        for(ItemDefinitionData item : packet.items()) {
            out.writeInt(item.id());
            CodecUtils.writeString(out, item.name());
            CodecUtils.writeString(out, item.description());
            out.writeInt(item.iconId());
            out.writeInt(item.maxStack());
        }
    }

    @Override
    public ItemDefinitionSnapshotPacket decode(ByteBuf in) {
        int count = in.readInt();

        List<ItemDefinitionData> items = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            int id = in.readInt();
            String name = CodecUtils.readString(in);
            String description = CodecUtils.readString(in);
            int iconId = in.readInt();
            int maxStack = in.readInt();

            items.add(new ItemDefinitionData(id, name, description, iconId, maxStack));
        }

        return new ItemDefinitionSnapshotPacket(items);
    }
}
