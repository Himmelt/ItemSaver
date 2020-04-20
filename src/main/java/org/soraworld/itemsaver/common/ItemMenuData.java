package org.soraworld.itemsaver.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Himmelt
 */
public class ItemMenuData extends WorldSavedData {

    private final Set<String> classes = new HashSet<>();

    public ItemMenuData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("classes", 8);
        for (int i = 0; i < list.tagCount(); i++) {
            classes.add(list.getStringTagAt(i));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        classes.forEach(clazz -> list.appendTag(new NBTTagString(clazz)));
        compound.setTag("classes", list);
        return compound;
    }

    public int getAmount() {
        return classes.size();
    }
}
