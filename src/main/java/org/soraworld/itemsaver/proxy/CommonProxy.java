package org.soraworld.itemsaver.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.soraworld.itemsaver.config.Config;
import org.soraworld.itemsaver.storage.WorldData;

import java.io.File;
import java.io.IOException;

public abstract class CommonProxy {

    public Config config;
    public Minecraft client;
    public File dataFile;
    public WorldData worldData = new WorldData();

    public abstract void init();

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

    public abstract void loadConfig(File cfgDir);

    public abstract void registKeyBinding();

    public abstract void registEventHandler();

}
