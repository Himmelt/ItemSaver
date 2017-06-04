package org.soraworld.itemsaver.config;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import org.soraworld.itemsaver.config.property.IProperty;
import org.soraworld.itemsaver.constant.IMod;

import java.io.File;

public class Config {

    public final IProperty.PropertyD hudX = new IProperty.PropertyD(IMod.MODID, "hudX", 0.05D);
    public final IProperty.PropertyD hudY = new IProperty.PropertyD(IMod.MODID, "hudY", 0.05D);

    private final Configuration config;
    private final File jsonFile;

    public Config(File configDir) {
        config = new Configuration(new File(configDir, IMod.MODID + ".cfg"), IMod.VERSION);
        jsonFile = new File(new File(configDir, IMod.MODID), "target.json");
        config.load();
        bind();
        comments();
        config.save();
        IMod.logger.info("config reloaded!");
    }

    public void reload() {
        config.load();
        bind();
        IMod.logger.info("config reloaded!");
    }

    public void comments() {
        hudX.setComment(I18n.format("sf.cfg.hudX"));
        hudY.setComment(I18n.format("sf.cfg.hudY"));
    }

    public void save() {
        config.save();
        IMod.logger.info("config saved!");
    }

    private void bind() {
        hudX.bind(config);
        hudY.bind(config);
    }

}
