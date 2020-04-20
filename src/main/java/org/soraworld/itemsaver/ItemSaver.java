package org.soraworld.itemsaver;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.soraworld.itemsaver.common.CommandSaver;
import org.soraworld.itemsaver.common.CommonProxy;

/**
 * @author Himmelt
 */
@Mod(
        modid = "itemsaver",
        name = "ItemSaver",
        canBeDeactivated = true,
        acceptableRemoteVersions = "*"
)
public final class ItemSaver {

    @SidedProxy(
            clientSide = "org.soraworld.itemsaver.client.ClientProxy",
            serverSide = "org.soraworld.itemsaver.common.CommonProxy"
    )
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSaver());
    }
}
