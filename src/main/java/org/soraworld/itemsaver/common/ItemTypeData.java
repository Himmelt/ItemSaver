package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Himmelt
 */
public class ItemTypeData extends WorldSavedData {

    private final Map<String, ItemStack> stacks = new HashMap<>();
    private static final Logger logger = LogManager.getLogger("ItemSaver");

    public ItemTypeData(String name) {
        super(name);
    }

    public boolean has(String key) {
        return stacks.containsKey(key);
    }

    public ItemStack get(String key) {
        return stacks.get(key);
    }

    public boolean add(String key, ItemStack stack) {
        if (!stacks.containsKey(key)) {
            stacks.put(key, stack);
            markDirty();
            return true;
        }
        return false;
    }

    public void set(String key, ItemStack stack) {
        stacks.put(key, stack);
        markDirty();
    }

    public void remove(String key) {
        stacks.remove(key);
        markDirty();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        List<String> keys = (List<String>) nbt.func_150296_c();
        for (String key : keys) {
            try {
                NBTTagCompound tag = nbt.getCompoundTag(key);
                stacks.put(key, ItemStack.loadItemStackFromNBT(tag));
            } catch (Throwable ignored) {
                logger.warn("Invalid nbt tag to read as itemstack for the key : " + key);
            }
        }
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound compound) {
        stacks.forEach((name, stack) -> {
            NBTTagCompound tagCompound = new NBTTagCompound();
            stack.writeToNBT(tagCompound);
            compound.setTag(name, tagCompound);
        });
    }

    public int getAmount() {
        return stacks.size();
    }

    public void fill(SaverInventory saver) {
        saver.clear();
        List<String> list = new ArrayList<>(stacks.keySet());
        int amount = saver.getSizeInventory();
        for (int i = 0; i < amount; i++) {
            ItemStack stack = new ItemStack(Blocks.stained_glass_pane, 1, i % 16);
            if (i == amount - 1) {
                stack.setStackDisplayName("\u00A7r[返回]");
            } else if (i < list.size()) {
                stack = stacks.getOrDefault(list.get(i), stack).copy();
                saver.putKey(i, list.get(i));
            } else {
                stack.setStackDisplayName("\u00A7r[可添加]");
            }
            saver.setInventorySlotContents(i, stack);
        }
    }

    public Set<String> getNames() {
        return Collections.unmodifiableSet(stacks.keySet());
    }

    public void clear() {
        stacks.clear();
        markDirty();
    }

    public void give(EntityPlayerMP target, String name, int amount) {
        if (name != null) {
            ItemStack stack = stacks.get(name);
            if (stack != null) {
                CommonProxy.give(target, stack, amount);
            }
        } else {
            stacks.values().forEach(stack -> CommonProxy.give(target, stack, -1));
        }
    }
}
