package org.soraworld.itemsaver.constant;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public final class IMod {
    public static final String NAME = "ItemSaver";
    public static final String MODID = "itemsaver";
    public static final String VERSION = "1.12.2-1.1.3";
    public static final String ACMCVERSION = "[1.12.2]";
    public static final ChatStyle YELLOW = new ChatStyle().setColor(EnumChatFormatting.YELLOW);
    public static final ChatStyle AQUA = new ChatStyle().setColor(EnumChatFormatting.AQUA);
    public static final ChatStyle RED = new ChatStyle().setColor(EnumChatFormatting.RED);
    public static final ChatStyle GREEN = new ChatStyle().setColor(EnumChatFormatting.GREEN);
    public static final IChatComponent PREFIX = new ChatComponentTranslation("isv.prefix").setChatStyle(AQUA);
}
