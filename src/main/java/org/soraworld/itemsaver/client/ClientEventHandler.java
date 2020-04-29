package org.soraworld.itemsaver.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.soraworld.itemsaver.common.OpenPacket;

/**
 * @author Himmelt
 */
public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final SimpleChannel channel;
    private final KeyBinding KEY_I_OPEN = new KeyBinding("key.open_saver", 0x49, "key.categories.inventory");

    public ClientEventHandler(SimpleChannel channel) {
        this.channel = channel;
        ClientRegistry.registerKeyBinding(KEY_I_OPEN);
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (mc.currentScreen == null && KEY_I_OPEN.isPressed()) {
            channel.send(PacketDistributor.SERVER.noArg(), new OpenPacket(true));
        }
    }
}
