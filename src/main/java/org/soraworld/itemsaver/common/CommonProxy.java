package org.soraworld.itemsaver.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.soraworld.itemsaver.client.ClientEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Himmelt
 */
public class CommonProxy {

    private final ResourceLocation CHANNEL_NAME = new ResourceLocation("itemsaver", "saver");
    private final SimpleChannel channel = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> "1.0", (v) -> true, (v) -> true);

    private static final byte OPEN_MENU = 1;
    private static final List<Runnable> TASKS = new ArrayList<>();

    public void onCommonSetup(FMLCommonSetupEvent event) {
        channel.registerMessage(OPEN_MENU, OpenPacket.class, OpenPacket::encode, OpenPacket::decode, this::processOpenPacket);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void processOpenPacket(OpenPacket packet, Supplier<NetworkEvent.Context> context) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            EntityPlayerMP player = context.get().getSender();
            if (player != null) {
                context.get().enqueueWork(() -> openMenu(player));
            }
        }
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler(channel));
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        SaverCommand.register(event.getCommandDispatcher(), this);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        for (Runnable task : TASKS) {
            task.run();
        }
        TASKS.clear();
    }

    public static void openMenu(EntityPlayerMP player) {
        if (player != null && player.hasPermissionLevel(2)) {
            ItemMenuData menuData = getMenuData(player.server);
            int amount = menuData.getAmount() / 9 * 9 + 9;
            SaverInventory menu = new SaverInventory("物品存储管理器 - 类别", "", amount, true);
            menuData.fill(menu);
            player.displayGUIChest(menu);
        }
    }

    public static void openType(EntityPlayerMP player, String type) {
        if (player != null && player.hasPermissionLevel(2)) {
            ItemTypeData saveData = getTypeData(player.server, type);
            int amount = (saveData.getAmount() + 1) / 9 * 9 + 9;
            if (amount > 54) {
                amount = 54;
            }
            SaverInventory saver = new SaverInventory("物品存储管理器 - " + type, type, amount, false);
            saveData.fill(saver);
            player.displayGUIChest(saver);
        }
    }

    public static ItemMenuData getMenuData(MinecraftServer server) {
        ItemMenuData menuData = server.getWorld(DimensionType.OVERWORLD).getSavedData(DimensionType.OVERWORLD, ItemMenuData::new, "itemsaver_menu");
        if (menuData == null) {
            menuData = new ItemMenuData("itemsaver_menu");
            server.getWorld(DimensionType.OVERWORLD).setSavedData(DimensionType.OVERWORLD, "itemsaver_menu", menuData);
        }
        return menuData;
    }

    public static ItemTypeData getTypeData(MinecraftServer server, String type) {
        ItemTypeData typeData = server.getWorld(DimensionType.OVERWORLD).getSavedData(DimensionType.OVERWORLD, ItemTypeData::new, "itemsaver_type_" + type);
        if (typeData == null) {
            typeData = new ItemTypeData("itemsaver_type_" + type);
            server.getWorld(DimensionType.OVERWORLD).setSavedData(DimensionType.OVERWORLD, "itemsaver_type_" + type, typeData);
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
            stack.setCount(count);
        }
        boolean flag = target.inventory.addItemStackToInventory(stack);
        if (flag) {
            target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            target.inventoryContainer.detectAndSendChanges();
        }
        if (flag && stack.isEmpty()) {
            stack.setCount(1);
            EntityItem entityitem1 = target.dropItem(stack, false);
            if (entityitem1 != null) {
                entityitem1.makeFakeItem();
            }
        } else {
            EntityItem entityitem = target.dropItem(stack, false);
            if (entityitem != null) {
                entityitem.setNoPickupDelay();
                entityitem.setOwnerId(target.getUniqueID());
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
