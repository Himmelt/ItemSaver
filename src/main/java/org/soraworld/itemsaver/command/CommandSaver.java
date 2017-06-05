package org.soraworld.itemsaver.command;

import net.minecraft.command.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.soraworld.itemsaver.constant.IMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.command.CommandBase.getPlayer;
import static net.minecraft.command.CommandBase.notifyCommandListener;
import static org.soraworld.itemsaver.ItemSaver.proxy;

public class CommandSaver implements ICommand {
    @Override
    public String getName() {
        return IMod.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "isv.help.usage";
    }

    @Override
    public List<String> getAliases() {
        List<String> alias = new ArrayList<>();
        alias.add("isv");
        return alias;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            if (args[0].equals("list")) {
                sender.sendMessage(new TextComponentString("list:all"));
                for (String type : proxy.worldData.get().keySet()) {
                    sender.sendMessage(new TextComponentTranslation("isv.list.type", type));
                    HashMap<String, ItemStack> map = proxy.worldData.get(type);
                    for (String name : map.keySet()) {
                        sender.sendMessage(new TextComponentTranslation("isv.list.item", name, map.get(name).getDisplayName()));
                    }
                }
                return;
            }
            if (args[0].equals("save")) {
                proxy.save();
                return;
            }
            if (args[0].equals("reload")) {
                proxy.reload();
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equals("list")) {
                sender.sendMessage(new TextComponentString("list:" + args[1]));
                HashMap<String, ItemStack> map = proxy.worldData.get(args[1]);
                for (String name : map.keySet()) {
                    sender.sendMessage(new TextComponentTranslation("isv.list.item", name, map.get(name).getDisplayName()));
                }
                return;
            }
            if (args[0].equals("remove")) {
                proxy.worldData.remove(args[1]);
                sender.sendMessage(new TextComponentTranslation("isv.type.remove" + args[1]));
                return;
            }
        }
        if (args.length == 3) {
            if (args[0].equals("add") && sender instanceof EntityPlayerMP) {
                sender.sendMessage(new TextComponentString("add:" + args[1] + ":" + args[2]));
                ItemStack it = ((EntityPlayerMP) sender).getHeldItemMainhand();
                if (it.getItem() != Items.AIR) {
                    proxy.worldData.add(args[1], args[2], it);
                    sender.sendMessage(new TextComponentTranslation("isv.name.add", args[1], args[2], it.getDisplayName()));
                    return;
                }
            }
            if (args[0].equals("remove")) {
                sender.sendMessage(new TextComponentString("remove:" + args[1] + ":" + args[2]));
                proxy.worldData.remove(args[1], args[2]);
                sender.sendMessage(new TextComponentTranslation("isv.name.remove", args[1], args[2]));
                return;
            }
        }
        if (args.length >= 4 && args[0].equals("give") && sender instanceof EntityPlayerMP) {
            EntityPlayerMP target = getPlayer(server, sender, args[1]);
            ItemStack stack = proxy.worldData.get(args[2], args[3]);
            if (stack == null || stack.getItem() == Items.AIR) {
                throw new WrongUsageException("isv.give.null");
            }
            int count = stack.getCount();
            ItemStack itemStack = stack.copy();
            if (args.length == 5 && args[4].matches("[0-9]{1,8}")) {
                try {
                    count = Integer.valueOf(args[4]);
                } finally {
                    itemStack.setCount(count);
                }
            }
            boolean flag = target.inventory.addItemStackToInventory(itemStack);
            if (flag) {
                target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                target.inventoryContainer.detectAndSendChanges();
            }
            if (flag && itemStack.isEmpty()) {
                itemStack.setCount(1);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);
                EntityItem dropItem = target.dropItem(itemStack, false);
                if (dropItem != null) {
                    dropItem.makeFakeItem();
                }
            } else {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);/////////////////////////
                EntityItem dropItem = target.dropItem(itemStack, false);
                if (dropItem != null) {
                    dropItem.setNoPickupDelay();
                    dropItem.setOwner(target.getName());
                }
            }
            notifyCommandListener(sender, this, "commands.give.success", itemStack.getTextComponent(), count, target.getName());
            return;
        }
        sender.sendMessage(new TextComponentString("show help:"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[]
            args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
