package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Himmelt
 */
public class SaverContainer extends ChestContainer {

    private final int amount;
    private final SaverInventory inventory;

    public SaverContainer(PlayerInventory playerInventory, SaverInventory chestInventory, PlayerEntity player, int rows, int windowId) {
        super(getType(rows), windowId, playerInventory, chestInventory, rows);
        this.inventory = chestInventory;
        this.amount = chestInventory.getSizeInventory();
    }

    @Override
    public ItemStack slotClick(int slotId, int mouse, ClickType clickType, PlayerEntity player) {
        if (slotId >= 0 && slotId < amount && player instanceof ServerPlayerEntity) {
            CommonProxy.runTask(() -> inventory.slotClick(slotId, mouse, clickType, (ServerPlayerEntity) player));
            return ItemStack.EMPTY;
        } else {
            return super.slotClick(slotId, mouse, clickType, player);
        }
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        return false;
    }

    private static ContainerType<ChestContainer> getType(int rows) {
        switch (rows) {
            case 1:
                return ContainerType.GENERIC_9X1;
            case 2:
                return ContainerType.GENERIC_9X2;
            case 3:
                return ContainerType.GENERIC_9X3;
            case 4:
                return ContainerType.GENERIC_9X4;
            case 5:
                return ContainerType.GENERIC_9X5;
            default:
                return ContainerType.GENERIC_9X6;
        }
    }
}
