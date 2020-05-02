package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Himmelt
 */
public class SaverInventory extends Inventory implements INamedContainerProvider {

    private final String type;
    private final boolean isMenu;
    private final ITextComponent title;
    private final Map<Integer, String> keys = new HashMap<>();

    public SaverInventory(String title, String type, int slotCount, boolean isMenu) {
        super(slotCount);
        this.type = type;
        this.isMenu = isMenu;
        this.title = new StringTextComponent(title);
    }

    public void putKey(int slot, String type) {
        keys.put(slot, type);
    }

    public void slotClick(int slotId, int mouse, ClickType clickType, ServerPlayerEntity player) {
        if (isMenu) {
            if (keys.containsKey(slotId)) {
                player.closeScreen();
                CommonProxy.openType(player, keys.get(slotId));
            }
        } else {
            if (mouse == 0) {
                if (slotId == getSizeInventory() - 1) {
                    player.closeScreen();
                    CommonProxy.openMenu(player);
                } else if (keys.containsKey(slotId)) {
                    CommonProxy.getTypeData(player.server, type).give(player, keys.get(slotId), -1);
                }
            }
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return title;
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        int rows = getSizeInventory() / 9;
        return new SaverContainer(playerInventory, this, player, rows, windowId);
    }
}
