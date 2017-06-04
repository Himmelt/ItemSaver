package org.soraworld.itemsaver.proxy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {
    @Override
    public void init() {

    }

    @Override
    public void loadConfig(File cfgDir) {

    }

    @Override
    public void registKeyBinding() {

    }

    @Override
    public void registEventHandler() {

    }
}
