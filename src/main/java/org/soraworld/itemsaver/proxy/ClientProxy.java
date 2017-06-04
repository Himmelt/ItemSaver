package org.soraworld.itemsaver.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.soraworld.itemsaver.config.Config;
import org.soraworld.itemsaver.handler.EventBusHandler;
import org.soraworld.itemsaver.handler.FMLEventHandler;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        client = Minecraft.getMinecraft();
    }

    @Override
    public void loadConfig(File cfgDir) {
        config = new Config(cfgDir);
    }

    @Override
    public void registKeyBinding() {
    }

    @Override
    public void registEventHandler() {
        MinecraftForge.EVENT_BUS.register(new EventBusHandler());
        MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
    }

}
