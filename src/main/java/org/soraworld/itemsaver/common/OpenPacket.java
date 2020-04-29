package org.soraworld.itemsaver.common;

import net.minecraft.network.PacketBuffer;

/**
 * @author Himmelt
 */
public class OpenPacket {
    private boolean open;

    public OpenPacket(boolean open) {
        this.open = open;
    }

    public static void encode(OpenPacket packet, PacketBuffer buf) {
        buf.writeBoolean(packet.open);
    }

    public static OpenPacket decode(PacketBuffer buf) {
        return new OpenPacket(buf.readBoolean());
    }
}
