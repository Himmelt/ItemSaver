package org.soraworld.itemsaver.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Himmelt
 */
public class CommonProxy {

    protected final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel("itemsaver");
    private static final List<Runnable> TASKS = new ArrayList<>();

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        CHANNEL.register(this);
    }

    @SubscribeEvent
    public void onMessage(FMLNetworkEvent.ServerCustomPacketEvent event) {
        INetHandlerPlayServer handler = event.handler;
        if (handler instanceof NetHandlerPlayServer) {
            EntityPlayerMP mp = ((NetHandlerPlayServer) handler).playerEntity;
            if (mp.canCommandSenderUseCommand(2, "gamemode")) {
                openMenu(mp);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Runnable task : TASKS) {
            task.run();
        }
        TASKS.clear();
    }

    public static void openMenu(EntityPlayerMP player) {
        ItemMenuData menuData = getMenuData(player.mcServer);
        int amount = menuData.getAmount() / 9 * 9 + 9;
        SaverInventory menu = new SaverInventory("物品存储管理器 - 类别", "", amount, true);
        menuData.fill(menu);
        displayGuiSaver(player, menu);
    }

    public static void openType(EntityPlayerMP player, String type) {
        ItemTypeData saveData = getTypeData(player.mcServer, type);
        int amount = (saveData.getAmount() + 1) / 9 * 9 + 9;
        if (amount > 54) {
            amount = 54;
        }
        SaverInventory saver = new SaverInventory("物品存储管理器 - " + type, type, amount, false);
        saveData.fill(saver);
        displayGuiSaver(player, saver);
    }

    public static void displayGuiSaver(EntityPlayerMP player, SaverInventory saver) {
        if (player.openContainer != player.inventoryContainer) {
            player.closeScreen();
        }
        player.getNextWindowId();
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, saver.getInventoryName(), saver.getSizeInventory(), saver.hasCustomInventoryName()));
        player.openContainer = new SaverContainer(player.inventory, saver, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    public static ItemMenuData getMenuData(MinecraftServer server) {
        ItemMenuData menuData = (ItemMenuData) server.getEntityWorld().mapStorage.loadData(ItemMenuData.class, "itemsaver_menu");
        if (menuData == null) {
            menuData = new ItemMenuData("itemsaver_menu");
            server.getEntityWorld().mapStorage.setData("itemsaver_menu", menuData);
        }
        return menuData;
    }

    public static ItemTypeData getTypeData(MinecraftServer server, String type) {
        ItemTypeData typeData = (ItemTypeData) server.getEntityWorld().mapStorage.loadData(ItemTypeData.class, "itemsaver_type_" + type);
        if (typeData == null) {
            typeData = new ItemTypeData("itemsaver_type_" + type);
            server.getEntityWorld().mapStorage.setData("itemsaver_type_" + type, typeData);
        }
        return typeData;
    }

    public static void runTask(Runnable task) {
        if (task != null) {
            TASKS.add(task);
        }
    }

    public static void give(EntityPlayer target, ItemStack itemStack, int count) {
        ItemStack stack = itemStack.copy();
        if (count > 0) {
            stack.stackSize = count;
        }
        boolean flag = target.inventory.addItemStackToInventory(stack);
        if (flag) {
            target.worldObj.playSound(target.posX, target.posY, target.posZ, "ENTITY_ITEM_PICKUP", 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F, false);
            target.inventoryContainer.detectAndSendChanges();
        }
        if (flag && stack.stackSize == 0) {
            stack.stackSize = 1;
            EntityItem dropItem = target.entityDropItem(stack, 0);
            if (dropItem != null) {
                dropItem.delayBeforeCanPickup = 32767;
                dropItem.age = dropItem.getEntityItem().getItem().getEntityLifespan(dropItem.getEntityItem(), dropItem.worldObj) - 1;
            }
        } else {
            EntityItem dropItem = target.entityDropItem(stack, 0);
            if (dropItem != null) {
                dropItem.delayBeforeCanPickup = 0;
            }
        }
    }

    public static List<String> getListOfTypes(MinecraftServer server, String head) {
        List<String> types = new ArrayList<>();
        ItemMenuData menuData = getMenuData(server);
        for (String type : menuData.getTypes()) {
            if (type.startsWith(head)) {
                types.add(type);
            }
        }
        return types;
    }

    public static List<String> getListOfNames(MinecraftServer server, String type, String head) {
        List<String> names = new ArrayList<>();
        ItemTypeData typeData = getTypeData(server, type);
        for (String name : typeData.getNames()) {
            if (name.startsWith(head)) {
                names.add(name);
            }
        }
        return names;
    }

    public static boolean addItem(MinecraftServer server, String type, String name, ItemStack stack) {
        ItemTypeData typeData = getTypeData(server, type);
        getMenuData(server).add(type);
        return typeData.add(name, stack);
    }

    public static void setItem(MinecraftServer server, String type, String name, ItemStack stack) {
        getTypeData(server, type).set(name, stack);
        getMenuData(server).add(type);
    }

    public static void removeItem(MinecraftServer server, String type, String name) {
        getTypeData(server, type).remove(name);
    }

    public static void removeType(MinecraftServer server, String type) {
        getTypeData(server, type).clear();
        getMenuData(server).remove(type);
    }
}
