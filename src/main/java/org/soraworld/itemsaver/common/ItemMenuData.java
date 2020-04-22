package org.soraworld.itemsaver.common;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Himmelt
 */
public class ItemMenuData extends WorldSavedData {

    private final Set<String> types = new HashSet<>();

    public ItemMenuData(String name) {
        super(name);
    }

    public void add(String type) {
        types.add(type);
        markDirty();
    }

    public void remove(String type) {
        types.remove(type);
        markDirty();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("types", 8);
        for (int i = 0; i < list.tagCount(); i++) {
            types.add(list.getStringTagAt(i));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        types.forEach(type -> list.appendTag(new NBTTagString(type)));
        compound.setTag("types", list);
        return compound;
    }

    public int getAmount() {
        return types.size();
    }

    public void fill(SaverInventory menu) {
        menu.clear();
        List<String> list = new ArrayList<>(types);
        for (int i = 0; i < menu.getSizeInventory(); i++) {
            ItemStack stack = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, i % 16);
            if (i < list.size()) {
                stack.setStackDisplayName("\u00A7r" + list.get(i));
                menu.putKey(i, list.get(i));
            } else {
                stack.setStackDisplayName("\u00A7r[可添加]");
            }
            menu.setInventorySlotContents(i, stack);
        }
    }

    public Set<String> getTypes() {
        return Collections.unmodifiableSet(types);
    }
}
