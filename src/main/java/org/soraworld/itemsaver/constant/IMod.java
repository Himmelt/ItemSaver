package org.soraworld.itemsaver.constant;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public final class IMod {
    public static final String NAME = "ItemSaver";
    public static final String MODID = "itemsaver";
    public static final String VERSION = "1.11.2-1.1.3";
    public static final String ACMCVERSION = "[1.11.2]";
    public static final Style YELLOW = new Style().setColor(TextFormatting.YELLOW);
    public static final Style AQUA = new Style().setColor(TextFormatting.AQUA);
    public static final Style RED = new Style().setColor(TextFormatting.RED);
    public static final Style GREEN = new Style().setColor(TextFormatting.GREEN);
    public static final ITextComponent PREFIX = new TextComponentTranslation("isv.prefix").setStyle(AQUA);
}
