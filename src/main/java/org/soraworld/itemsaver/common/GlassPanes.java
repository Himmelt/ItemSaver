package org.soraworld.itemsaver.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static net.minecraft.block.Blocks.*;

/**
 * @author Himmelt
 */
public class GlassPanes {

    private static Block[] GLASS_PANES = new Block[]{
            WHITE_STAINED_GLASS_PANE,
            ORANGE_STAINED_GLASS_PANE,
            MAGENTA_STAINED_GLASS_PANE,
            LIGHT_BLUE_STAINED_GLASS_PANE,
            YELLOW_STAINED_GLASS_PANE,
            LIME_STAINED_GLASS_PANE,
            PINK_STAINED_GLASS_PANE,
            GRAY_STAINED_GLASS_PANE,
            LIGHT_GRAY_STAINED_GLASS_PANE,
            CYAN_STAINED_GLASS_PANE,
            PURPLE_STAINED_GLASS_PANE,
            BLUE_STAINED_GLASS_PANE,
            BROWN_STAINED_GLASS_PANE,
            GREEN_STAINED_GLASS_PANE,
            RED_STAINED_GLASS_PANE,
            BLACK_STAINED_GLASS_PANE
    };

    public static ItemStack getGlassPane(int meta) {
        return new ItemStack(GLASS_PANES[meta % 16], 1);
    }
}
