package org.soraworld.itemsaver.common;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void add(String key, ItemStack stack) {
        if (!stacks.containsKey(key)) {
            stacks.put(key, stack);
        }
    }

    public void set(String key, ItemStack stack) {
        stacks.put(key, stack);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        for (String key : nbt.getKeySet()) {
            try {
                NBTTagCompound tag = nbt.getCompoundTag(key);
                stacks.put(key, new ItemStack(tag));
            } catch (Throwable ignored) {
                logger.warn("Invalid nbt tag to read as itemstack for the key : " + key);
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        stacks.forEach((name, stack) -> compound.setTag(name, stack.serializeNBT()));
        return compound;
    }

    public int getAmount() {
        return stacks.size();
    }

    public void fill(SaverInventory saver) {
        saver.clear();
        List<String> list = new ArrayList<>(stacks.keySet());
        for (int i = 0; i < saver.getSizeInventory(); i++) {
            ItemStack stack = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i % 16);
            if (i < list.size()) {
                stack = stacks.getOrDefault(list.get(i), stack).copy();
            } else {
                stack.setStackDisplayName("\u00A7r[可添加]");
            }
            saver.setInventorySlotContents(i, stack);
        }
    }
}
