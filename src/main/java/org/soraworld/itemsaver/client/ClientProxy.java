package org.soraworld.itemsaver.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.PacketBuffer;
import org.lwjgl.input.Keyboard;
import org.soraworld.itemsaver.common.CommonProxy;

/**
 * @author Himmelt
 */
public class ClientProxy extends CommonProxy {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding KEY_I_OPEN = new KeyBinding("key.open_saver", Keyboard.KEY_I, "key.categories.inventory");

    @Override
    public void onInit(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(KEY_I_OPEN);
        super.onInit(event);
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (mc.currentScreen == null && KEY_I_OPEN.isPressed()) {
            CHANNEL.sendToServer(new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()), "itemsaver"));
        }
    }
}
