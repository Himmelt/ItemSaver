package org.soraworld.itemsaver.item;

import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nonnull;

public class IItemStack {
    private final ItemStack stack;
    private final IChatComponent text;

    public IItemStack(@Nonnull String type, @Nonnull String name, @Nonnull ItemStack stack) {
        this.stack = stack.copy();
        this.text = this.stack.func_151000_E();
        this.text.getChatStyle().setChatClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + type + " " + name + " 1")
        );
    }

    public ItemStack get() {
        return stack;
    }

    public IChatComponent getText() {
        return text;
    }

}
