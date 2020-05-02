package org.soraworld.itemsaver.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
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
    public void read(CompoundNBT nbt) {
        ListNBT list = nbt.getList("types", 8);
        for (int i = 0; i < list.size(); i++) {
            types.add(list.getString(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT list = new ListNBT();
        types.forEach(type -> list.add(new StringNBT(type)));
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
                stack.setDisplayName(new StringTextComponent("\u00A7r" + list.get(i)));
                menu.putKey(i, list.get(i));
            } else {
                stack.setDisplayName(new StringTextComponent("\u00A7r[可添加]"));
            }
            menu.setInventorySlotContents(i, stack);
        }
    }

    public Set<String> getTypes() {
        return Collections.unmodifiableSet(types);
    }
}
