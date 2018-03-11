package org.soraworld.itemsaver;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.soraworld.itemsaver.api.ItemSaverAPI;
import org.soraworld.itemsaver.command.CommandSaver;
import org.soraworld.itemsaver.constant.IMod;

import java.io.File;

@Mod(
        modid = IMod.MODID,
        name = IMod.NAME,
        version = IMod.VERSION,
        acceptedMinecraftVersions = IMod.ACMCVERSION
)
public class ItemSaver {

    public static ItemSaverAPI api;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        World world = event.getServer().getEntityWorld();
        if (world instanceof WorldServer) {
            try {
                event.registerServerCommand(new CommandSaver());
                File dataDir = new File(((WorldServer) world).getChunkSaveLocation(), "data");
                File dataFile = new File(dataDir, IMod.MODID + ".dat");
                api = new ItemSaverAPI(dataFile);
                api.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        api.save();
        api = null;
    }
}
