package org.soraworld.itemsaver.api;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import org.soraworld.itemsaver.item.IItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ItemSaverAPI {

    private final File dataFile;
    private final HashMap<String, HashMap<String, IItemStack>> dataMap;

    public ItemSaverAPI(File dataFile) {
        this.dataFile = dataFile;
        this.dataMap = new HashMap<>();
    }

    public IItemStack add(@Nonnull String type, @Nonnull String name, @Nonnull ItemStack itemStack) {
        if (!dataMap.containsKey(type)) {
            dataMap.put(type, new HashMap<>());
        }
        IItemStack stack = new IItemStack(type, name, itemStack);
        dataMap.get(type).put(name, stack);
        return stack;
    }

    public HashMap<String, HashMap<String, IItemStack>> get() {
        return dataMap;
    }

    public HashMap<String, IItemStack> get(String type) {
        if (!dataMap.containsKey(type)) {
            dataMap.put(type, new HashMap<>());
        }
        return dataMap.get(type);
    }

    @Nullable
    public IItemStack get(String type, String name) {
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

    public void reload() {
        try {
            if (!dataFile.exists() || !dataFile.isFile()) {
                dataFile.delete();
                dataFile.createNewFile();
            }
            readFromNBT(CompressedStreamTools.read(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            CompressedStreamTools.write(writeToNBT(new NBTTagCompound()), dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void give(ICommandSender sender, EntityPlayer target, IItemStack itemStack, int count) {
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
        for (String type : nbt.getKeySet()) {
            NBTBase base = nbt.getTag(type);
            if (base instanceof NBTTagCompound) {
                for (String name : ((NBTTagCompound) base).getKeySet()) {
                    NBTBase item = ((NBTTagCompound) base).getTag(name);
                    if (item instanceof NBTTagCompound) {
                        add(type, name, new ItemStack((NBTTagCompound) item));
                    }
                }
            }
        }
    }

    private NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for (String type : dataMap.keySet()) {
            NBTTagCompound comp = new NBTTagCompound();
            HashMap<String, IItemStack> typeMap = dataMap.get(type);
            for (String name : typeMap.keySet()) {
                comp.setTag(name, typeMap.get(name).get().serializeNBT());
            }
            compound.setTag(type, comp);
        }
        return compound;
    }

}
