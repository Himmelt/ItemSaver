package org.soraworld.itemsaver;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.soraworld.itemsaver.api.ItemSaverAPI;
import org.soraworld.itemsaver.command.CommandSaver;
import org.soraworld.itemsaver.constant.IMod;
import org.soraworld.itemsaver.manager.ItemManager;
import org.soraworld.violet.manager.SpigotManager;
import org.soraworld.violet.plugin.SpigotPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ItemSaver extends SpigotPlugin {

    public static final String PLUGIN_ID = "itemsaver";
    public static final String PLUGIN_NAME = "ItemSaver";
    public static final String PLUGIN_VERSION = "1.1.4";

    public void serverStarting(FMLServerStartingEvent event) {
        World world = event.getServer().getEntityWorld();
        if (world instanceof WorldServer) {
            try {
                event.registerServerCommand(new CommandSaver());
                File dataDir = new File(((WorldServer) world).getChunkSaveLocation(), "data");
                File dataFile = new File(dataDir, IMod.MODID + ".dat");
                api = new ItemSaverAPI(dataFile);
                api.reload();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected SpigotManager registerManager(Path path) {
        return new ItemManager(this, path);
    }

    public void afterEnable() {

    }

    protected List<Listener> registerListeners() {
        return null;
    }
}
