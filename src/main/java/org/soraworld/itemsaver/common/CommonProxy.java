package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Himmelt
 */
public class CommonProxy {

    protected final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel("itemsaver");
    private static final List<Runnable> tasks = new ArrayList<>();

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        CHANNEL.register(this);
    }

    @SubscribeEvent
    public void onMessage(FMLNetworkEvent.ServerCustomPacketEvent event) {
        INetHandlerPlayServer handler = event.getHandler();
        if (handler instanceof NetHandlerPlayServer) {
            EntityPlayerMP mp = ((NetHandlerPlayServer) handler).player;
            if (mp.canUseCommand(2, "gamemode")) {
                openMenu(mp);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Runnable task : tasks) {
            task.run();
        }
        tasks.clear();
    }

    public void openMenu(EntityPlayer player) {
        ItemMenuData menuData = getMenuData(player);
        int amount = menuData.getAmount() / 9 * 9 + 9;
        SaverInventory menu = new SaverInventory("物品存储管理器 - 类别", amount, true);
        menuData.fill(menu);
        player.displayGUIChest(menu);
    }

    public void openType(EntityPlayer player, String type) {
        ItemTypeData saveData = getTypeData(player, type);
        int amount = saveData.getAmount() / 9 * 9 + 9;
        if (amount > 54) {
            amount = 54;
        }
        SaverInventory saver = new SaverInventory("物品存储管理器 - " + type, amount, false);
        saveData.fill(saver);
        player.displayGUIChest(saver);
    }

    private ItemMenuData getMenuData(EntityPlayer player) {
        ItemMenuData menuData = (ItemMenuData) player.world.loadData(ItemMenuData.class, "itemsaver_menu");
        if (menuData == null) {
            menuData = new ItemMenuData("itemsaver_menu");
            player.world.setData("itemsaver_menu", menuData);
        }
        // TODO
        menuData.markDirty();
        return menuData;
    }

    private ItemTypeData getTypeData(EntityPlayer player, String type) {
        ItemTypeData typeData = (ItemTypeData) player.world.loadData(ItemTypeData.class, "itemsaver_type_" + type);
        if (typeData == null) {
            typeData = new ItemTypeData("itemsaver_type_" + type);
            player.world.setData("itemsaver_type_" + type, typeData);
        }
        return typeData;
    }

    public static void runTask(Runnable task) {
        if (task != null) {
            tasks.add(task);
        }
    }
}
