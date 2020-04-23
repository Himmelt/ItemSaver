package org.soraworld.itemsaver.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
        player.displayGUIChest(menu);
    }

    public static void openType(EntityPlayerMP player, String type) {
        ItemTypeData saveData = getTypeData(player.mcServer, type);
        int amount = (saveData.getAmount() + 1) / 9 * 9 + 9;
        if (amount > 54) {
            amount = 54;
        }
        SaverInventory saver = new SaverInventory("物品存储管理器 - " + type, type, amount, false);
        saveData.fill(saver);
        player.displayGUIChest(saver);
    }

    public static ItemMenuData getMenuData(MinecraftServer server) {
        ItemMenuData menuData = (ItemMenuData) server.getEntityWorld().getMapStorage().loadData(ItemMenuData.class, "itemsaver_menu");
        if (menuData == null) {
            menuData = new ItemMenuData("itemsaver_menu");
            server.getEntityWorld().getMapStorage().setData("itemsaver_menu", menuData);
        }
        return menuData;
    }

    public static ItemTypeData getTypeData(MinecraftServer server, String type) {
        ItemTypeData typeData = (ItemTypeData) server.getEntityWorld().getMapStorage().loadData(ItemTypeData.class, "itemsaver_type_" + type);
        if (typeData == null) {
            typeData = new ItemTypeData("itemsaver_type_" + type);
            server.getEntityWorld().getMapStorage().setData("itemsaver_type_" + type, typeData);
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
            target.worldObj.playSoundAtEntity(target, "random.pop", 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            target.inventoryContainer.detectAndSendChanges();
        }
        EntityItem entityitem;
        if (flag && stack.stackSize <= 0) {
            stack.stackSize = 1;
            entityitem = target.dropPlayerItemWithRandomChoice(stack, false);
            if (entityitem != null) {
                entityitem.func_174870_v();
            }
        } else {
            entityitem = target.dropPlayerItemWithRandomChoice(stack, false);
            if (entityitem != null) {
                entityitem.setNoPickupDelay();
                entityitem.setOwner(target.getCommandSenderName());
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
