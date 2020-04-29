package org.soraworld.itemsaver.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

/**
 * @author Himmelt
 */
public class ItemMenuData extends WorldSavedData {

    private final Set<String> types = new HashSet<>();

    public ItemMenuData(String name) {
        super(name);
    }

    @Override
    public void read(NBTTagCompound nbt) {
        NBTTagList list = nbt.getList("types", 8);
        for (int i = 0; i < list.size(); i++) {
            types.add(list.getString(i));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        types.forEach(type -> list.add(new NBTTagString(type)));
        compound.put("types", list);
        return compound;
    }

    public void add(String type) {
        types.add(type);
        markDirty();
    }

    public void remove(String type) {
        types.remove(type);
        markDirty();
    }

    public int getAmount() {
        return types.size();
    }

    public void fill(SaverInventory menu) {
        menu.clear();
        List<String> list = new ArrayList<>(types);
        for (int i = 0; i < menu.getSizeInventory(); i++) {
            ItemStack stack = GlassPanes.getGlassPane(i);
            if (i < list.size()) {
                stack.setDisplayName(new TextComponentString("\u00A7r" + list.get(i)));
                menu.putKey(i, list.get(i));
            } else {
                stack.setDisplayName(new TextComponentString("\u00A7r[可添加]"));
            }
            menu.setInventorySlotContents(i, stack);
        }
    }

    public Set<String> getTypes() {
        return Collections.unmodifiableSet(types);
    }
}
