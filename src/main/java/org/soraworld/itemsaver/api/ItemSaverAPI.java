package org.soraworld.itemsaver.api;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
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
            dataMap.put(type, new HashMap<String, IItemStack>());
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
            dataMap.put(type, new HashMap<String, IItemStack>());
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
            NBTTagCompound compound = CompressedStreamTools.read(dataFile);
            if (compound != null) readFromNBT(compound);
        } catch (IOException e) {
            // TODO LOGGER
            //e.printStackTrace();
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
        it.stackSize = count;
        boolean flag = target.inventory.addItemStackToInventory(it);
        if (flag) {
            target.worldObj.playSound(target.posX, target.posY, target.posZ, "ENTITY_ITEM_PICKUP", 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F, false);
            target.inventoryContainer.detectAndSendChanges();
        }
        if (flag && it.stackSize == 0) {
            it.stackSize = 1;
            //sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);
            EntityItem dropItem = target.entityDropItem(it, 0);
            if (dropItem != null) {
                dropItem.delayBeforeCanPickup = 32767;
                dropItem.age = dropItem.getEntityItem().getItem().getEntityLifespan(dropItem.getEntityItem(), dropItem.worldObj) - 1;
            }
        } else {
            //sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count - it.stackSize);
            EntityItem dropItem = target.entityDropItem(it, 0);
            if (dropItem != null) {
                dropItem.delayBeforeCanPickup = 0;
                //dropItem.setNoPickupDelay();
                //dropItem.(target.getName());
            }
        }
    }

    private void readFromNBT(NBTTagCompound nbt) {
        dataMap.clear();
        for (Object type : nbt.func_150296_c()) {
            if (type instanceof String) {
                NBTBase base = nbt.getTag((String) type);
                if (base instanceof NBTTagCompound) {
                    for (Object name : ((NBTTagCompound) base).func_150296_c()) {
                        if (name instanceof String) {
                            NBTBase item = ((NBTTagCompound) base).getTag((String) name);
                            if (item instanceof NBTTagCompound) {
                                add((String) type, (String) name, ItemStack.loadItemStackFromNBT((NBTTagCompound) item));
                            }
                        }
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
                ItemStack stack = typeMap.get(name).get();
                if (stack.getItem() != null) {
                    comp.setTag(name, stack.writeToNBT(new NBTTagCompound()));
                }
            }
            compound.setTag(type, comp);
        }
        return compound;
    }

}
