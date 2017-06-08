package org.soraworld.itemsaver.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;

public class IItemStack {
    private final ItemStack stack;
    private final ITextComponent text;

    public IItemStack(@Nonnull String type, @Nonnull String name, @Nonnull ItemStack stack) {
        this.stack = stack.copy();
        this.text = this.stack.getTextComponent();
        this.text.getStyle().setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + type + " " + name + " 1")
        );
    }

    public ItemStack get() {
        return stack;
    }

    public ITextComponent getText() {
        return text;
    }

}
