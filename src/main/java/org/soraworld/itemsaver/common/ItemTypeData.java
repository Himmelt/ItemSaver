package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSavedData;
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
        ItemStack stack = stacks.get(key);
        return stack == null ? null : stack.copy();
    }

    public boolean add(String key, ItemStack stack) {
        if (!stacks.containsKey(key)) {
            stacks.put(key, stack.copy());
            markDirty();
            return true;
        }
        return false;
    }

    public void set(String key, ItemStack stack) {
        stacks.put(key, stack.copy());
        markDirty();
    }

    public void remove(String key) {
        stacks.remove(key);
        markDirty();
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        for (String key : nbt.keySet()) {
            try {
                CompoundNBT tag = nbt.getCompound(key);
                stacks.put(key, ItemStack.read(tag));
            } catch (Throwable ignored) {
                logger.warn("Invalid nbt tag to read as itemstack for the key : " + key);
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        stacks.forEach((name, stack) -> compound.put(name, stack.serializeNBT()));
        return compound;
    }

    public int getAmount() {
        return stacks.size();
    }

    public void fill(SaverInventory saver) {
        saver.clear();
        List<String> list = new ArrayList<>(stacks.keySet());
        int amount = saver.getSizeInventory();
        for (int i = 0; i < amount; i++) {
            ItemStack stack = GlassPanes.getGlassPane(i);
            if (i == amount - 1) {
                stack.setDisplayName(new StringTextComponent("\u00A7r[返回]"));
            } else if (i < list.size()) {
                String name = list.get(i);
                stack = stacks.getOrDefault(name, stack).copy();
                if (stack.hasDisplayName()) {
                    stack.setDisplayName(new StringTextComponent("\u00A7r[" + name + "]").appendSibling(stack.getDisplayName()));
                } else {
                    stack.setDisplayName(new StringTextComponent("\u00A7r[" + name + "]").appendSibling(new TranslationTextComponent(stack.getTranslationKey())));
                }
                saver.putKey(i, name);
            } else {
                stack.setDisplayName(new StringTextComponent("\u00A7r[可添加]"));
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

    public void give(ServerPlayerEntity target, String name, int amount) {
        if (name != null) {
            ItemStack stack = stacks.get(name);
            if (stack != null) {
                CommonProxy.give(target, stack.copy(), amount);
            }
        } else {
            stacks.values().forEach(stack -> CommonProxy.give(target, stack.copy(), -1));
        }
    }
}
