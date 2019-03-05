package org.soraworld.itemsaver.manager;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.command.CommandSender;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;

public class ItemManager extends SpigotManager {

    private final File nbtFile;
    private final TreeMap<String, TreeMap<String, ItemStack>> dataMap = new TreeMap<>();

    public ItemManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
        this.nbtFile = path.resolve("items.data").toFile();
    }

    public boolean load() {
        loadItems();
        return super.load();
    }

    public boolean save() {
        saveItems();
        return super.save();
    }

    public ChatColor defChatColor() {
        return ChatColor.YELLOW;
    }

    public void add(String type, String name, ItemStack stack) {
        if (stack != null) {
            TreeMap<String, ItemStack> items = dataMap.computeIfAbsent(type, k -> new TreeMap<>());
            items.put(name, stack);
        }
    }

    public ItemStack get(String type, String name) {
        if (dataMap.containsKey(type)) {
            return dataMap.get(type).get(name);
        }
        return null;
    }

    public void remove(String type) {
        dataMap.remove(type);
    }

    public void remove(String type, String name) {
        if (dataMap.containsKey(type)) {
            dataMap.get(type).remove(name);
        }
    }

    public void clear() {
        dataMap.clear();
    }

    public void loadItems() {
        try {
            if (!nbtFile.exists() || !nbtFile.isFile()) {
                nbtFile.delete();
                nbtFile.createNewFile();
            }
            NBTTagCompound compound = NBTCompressedStreamTools.a(new FileInputStream(nbtFile));
            if (compound != null) readFromNBT(compound);
        } catch (IOException e) {
            if (debug) e.printStackTrace();
            console(ChatColor.RED + "Items load from nbt file Exception !!");
        }
    }

    public void saveItems() {
        try {
            NBTCompressedStreamTools.a(writeToNBT(), new FileOutputStream(nbtFile));
        } catch (IOException e) {
            if (debug) e.printStackTrace();
            console(ChatColor.RED + "Items save to nbt file Exception !!");
        }
    }

    public void give(CommandSender sender, EntityPlayer target, ItemStack itemStack, int count) {
        ItemStack it = itemStack.get().copy();
        it.setCount(count);
        boolean flag = target.inventory.addItemStackToInventory(it);
        if (flag) {
            target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            target.inventoryContainer.detectAndSendChanges();
        }
        if (flag && it.isEmpty()) {
            it.setCount(1);
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);
            EntityItem dropItem = target.dropItem(it, false);
            if (dropItem != null) {
                dropItem.makeFakeItem();
            }
        } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count - it.getCount());
            EntityItem dropItem = target.dropItem(it, false);
            if (dropItem != null) {
                dropItem.setNoPickupDelay();
                dropItem.setOwner(target.getName());
            }
        }
    }

    private void readFromNBT(NBTTagCompound nbt) {
        dataMap.clear();
        for (String type : nbt.c()) {
            NBTBase base = nbt.get(type);
            if (base instanceof NBTTagCompound) {
                for (String name : ((NBTTagCompound) base).c()) {
                    NBTBase item = ((NBTTagCompound) base).get(name);
                    if (item instanceof NBTTagCompound) {
                        add(type, name, new ItemStack((NBTTagCompound) item));
                    }
                }
            }
        }
    }

    private NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        dataMap.forEach((type, items) -> {
            NBTTagCompound comp = new NBTTagCompound();
            items.forEach((name, stack) -> {
                if (stack.getItem() != null && stack.getItem() != Items.a) {
                    comp.set(name, stack.save(new NBTTagCompound()));
                }
            });
            compound.set(type, comp);
        });
        return compound;
    }
}
