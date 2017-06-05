package org.soraworld.itemsaver.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

import static org.soraworld.itemsaver.ItemSaver.proxy;

public class WorldData {

    private final HashMap<String, HashMap<String, ItemStack>> dataMap = new HashMap<>();

    public void add(String type, String name, ItemStack itemStack) {
        if (!dataMap.containsKey(type)) {
            dataMap.put(type, new HashMap<>());
        }
        dataMap.get(type).put(name, itemStack);
        proxy.save();
    }

    public HashMap<String, HashMap<String, ItemStack>> get() {
        return dataMap;
    }

    public HashMap<String, ItemStack> get(String type) {
        if (!dataMap.containsKey(type)) {
            dataMap.put(type, new HashMap<>());
        }
        return dataMap.get(type);
    }

    public ItemStack get(String type, String name) {
        if (dataMap.containsKey(type)) {
            System.out.println(type);
            return dataMap.get(type).get(name);
        }
        return null;
    }

    public void remove(String type) {
        dataMap.remove(type);
        proxy.save();
    }

    public void remove(String type, String name) {
        if (dataMap.containsKey(type)) {
            dataMap.get(type).remove(name);
            proxy.save();
        }
    }

    public void clear() {
        dataMap.clear();
        proxy.save();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        dataMap.clear();
        for (String type : nbt.getKeySet()) {
            NBTBase base = nbt.getTag(type);
            if (base instanceof NBTTagCompound) {
                HashMap<String, ItemStack> typeMap = new HashMap<>();
                for (String name : ((NBTTagCompound) base).getKeySet()) {
                    NBTBase item = ((NBTTagCompound) base).getTag(name);
                    if (item instanceof NBTTagCompound) {
                        typeMap.put(name, new ItemStack((NBTTagCompound) item));
                    }
                }
                dataMap.put(type, typeMap);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for (String type : dataMap.keySet()) {
            NBTTagCompound comp = new NBTTagCompound();
            HashMap<String, ItemStack> typeMap = dataMap.get(type);
            for (String name : typeMap.keySet()) {
                comp.setTag(name, typeMap.get(name).serializeNBT());
            }
            compound.setTag(type, comp);
        }
        return compound;
    }

}
