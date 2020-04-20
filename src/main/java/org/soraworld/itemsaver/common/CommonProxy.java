package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * @author Himmelt
 */
public class CommonProxy {

    private ItemMenuData menuData;
    protected final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel("itemsaver");

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        CHANNEL.register(this);
    }

    @SubscribeEvent
    public void onMessage(FMLNetworkEvent.ServerCustomPacketEvent event) {
        INetHandlerPlayServer handler = event.getHandler();
        if (handler instanceof NetHandlerPlayServer) {
            EntityPlayerMP mp = ((NetHandlerPlayServer) handler).player;
            openMenu(mp);
        }
    }

    public void openMenu(EntityPlayerMP player) {
        ItemMenuData menuData = getMenuData(player);
        int amount = (menuData.getAmount() + 8) / 9 * 9;
        SaverInventory menu = new SaverInventory("物品春初管理器 - 类别", amount);
        player.displayGUIChest(menu);
    }

    private ItemMenuData getMenuData(EntityPlayerMP player) {
        if (menuData == null) {
            menuData = (ItemMenuData) player.world.getMapStorage().getOrLoadData(ItemMenuData.class, "itemsaver:classes");
        }
        return menuData;
    }
}
