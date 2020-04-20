package org.soraworld.itemsaver.common;

import net.minecraft.inventory.InventoryBasic;

/**
 * @author Himmelt
 */
public class SaverInventory extends InventoryBasic {
    public SaverInventory(String title, int slotCount) {
        super(title, true, slotCount);
    }
}
