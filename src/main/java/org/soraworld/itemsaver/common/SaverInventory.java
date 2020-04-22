package org.soraworld.itemsaver.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.IInteractionObject;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Himmelt
 */
public class SaverInventory extends InventoryBasic implements IInteractionObject {

    private final String type;
    private final boolean isMenu;
    private final Map<Integer, String> keys = new HashMap<>();

    public SaverInventory(String title, String type, int slotCount, boolean isMenu) {
        super(title, true, slotCount);
        this.type = type;
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

    public void putKey(int slot, String type) {
        keys.put(slot, type);
    }

    public void slotClick(int slotId, int mouse, ClickType clickType, EntityPlayerMP player) {
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
}
