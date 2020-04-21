package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.IInteractionObject;
import org.soraworld.itemsaver.ItemSaver;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Himmelt
 */
public class SaverInventory extends InventoryBasic implements IInteractionObject {

    private final boolean isMenu;
    private final Map<Integer, String> types = new HashMap<>();

    public SaverInventory(String title, int slotCount, boolean isMenu) {
        super(title, true, slotCount);
        this.isMenu = isMenu;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
        return new SaverContainer(playerInventory, this, player);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }

    public void putType(int slot, String type) {
        types.put(slot, type);
    }

    public void slotClick(int slotId, ClickType clickType, EntityPlayer player) {
        if (types.containsKey(slotId)) {
            player.closeScreen();
            ItemSaver.proxy.openType(player, types.get(slotId));
        }
    }
}
