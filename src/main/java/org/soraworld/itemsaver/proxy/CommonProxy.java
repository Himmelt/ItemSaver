package org.soraworld.itemsaver.proxy;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.soraworld.itemsaver.storage.WorldData;

import java.io.File;
import java.io.IOException;

public class CommonProxy {

    public File dataFile;
    public WorldData worldData = new WorldData();

    public void reload() {
        try {
            worldData.readFromNBT(CompressedStreamTools.read(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            CompressedStreamTools.write(worldData.writeToNBT(new NBTTagCompound()), dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerEventHandler() {
        //MinecraftForge.EVENT_BUS.register(new EventBusHandler());
        //MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
    }

}
