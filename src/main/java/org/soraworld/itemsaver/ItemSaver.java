package org.soraworld.itemsaver;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.soraworld.itemsaver.common.CommonProxy;

/**
 * @author Himmelt
 */
@Mod("itemsaver")
public final class ItemSaver {
    public ItemSaver() {
        CommonProxy proxy = new CommonProxy();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(proxy::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(proxy::onClientSetup);
    }
}
