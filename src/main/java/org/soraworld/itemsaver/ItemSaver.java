package org.soraworld.itemsaver;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.soraworld.itemsaver.common.CommandSaver;
import org.soraworld.itemsaver.common.CommonProxy;

/**
 * @author Himmelt
 */
@Mod(
        modid = "itemsaver",
        name = "ItemSaver",
        canBeDeactivated = true,
        acceptableRemoteVersions = "*",
        useMetadata = true
)
public final class ItemSaver {

    @SidedProxy(
            clientSide = "org.soraworld.itemsaver.client.ClientProxy",
            serverSide = "org.soraworld.itemsaver.common.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSaver());
    }
}
