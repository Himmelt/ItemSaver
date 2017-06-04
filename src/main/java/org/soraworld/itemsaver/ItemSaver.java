package org.soraworld.itemsaver;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.soraworld.itemsaver.command.CommandSaver;
import org.soraworld.itemsaver.constant.IMod;
import org.soraworld.itemsaver.proxy.CommonProxy;

import java.io.File;
import java.io.IOException;

@Mod(
        modid = IMod.MODID,
        name = IMod.NAME,
        version = IMod.VERSION,
        acceptedMinecraftVersions = IMod.ACMCVERSION
)
public class ItemSaver {
    @SidedProxy(clientSide = IMod.CLIENT_PROXY_CLASS, serverSide = IMod.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.registEventHandler();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSaver());
        World world = event.getServer().getEntityWorld();
        if (world instanceof WorldServer) {
            try {
                File dataDir = new File(((WorldServer) world).getChunkSaveLocation(), "data");
                proxy.dataFile = new File(dataDir, IMod.MODID + ".dat");
                proxy.worldData.readFromNBT(CompressedStreamTools.read(proxy.dataFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
