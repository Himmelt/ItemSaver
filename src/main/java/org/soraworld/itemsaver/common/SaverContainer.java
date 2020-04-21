package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Himmelt
 */
public class SaverContainer extends ContainerChest {

    private final int amount;
    private final SaverInventory inventory;

    public SaverContainer(IInventory playerInventory, SaverInventory chestInventory, EntityPlayer player) {
        super(playerInventory, chestInventory, player);
        this.inventory = chestInventory;
        this.amount = chestInventory.getSizeInventory();
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < amount) {
            System.out.println("slot:" + slotId);
            CommonProxy.runTask(() -> inventory.slotClick(slotId, clickType, player));
            return ItemStack.EMPTY;
        } else {
            return super.slotClick(slotId, dragType, clickType, player);
        }
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return false;
    }
}
