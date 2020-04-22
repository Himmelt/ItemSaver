package org.soraworld.itemsaver.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Himmelt
 */
public class CommandSaver extends CommandBase {
    @Override
    public String getName() {
        return "itemsaver";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "isv give|add|set|remove|open";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("saver", "isv");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            switch (args[0]) {
                case "give":
                    if (args.length >= 3) {
                        EntityPlayerMP target = getPlayer(server, sender, args[1]);
                        String type = args[2];
                        String name = null;
                        if (args.length >= 4) {
                            name = args[3];
                        }
                        ItemTypeData data = CommonProxy.getTypeData(server, type);
                        int amount = args.length >= 5 ? Integer.parseInt(args[4]) : -1;
                        data.give(target, name, amount);
                        CommandBase.notifyCommandListener(sender, this, "commands.give.success", new TextComponentString(" [" + type + (name == null ? "" : "-" + name) + "] "), 1, target.getName());
                    } else {
                        sender.sendMessage(new TextComponentString("/isv give <player> type [item] [amount]"));
                    }
                    break;
                case "add":
                    if (args.length == 3) {
                        if (sender instanceof EntityPlayerMP) {
                            ItemStack stack = ((EntityPlayerMP) sender).getHeldItem(EnumHand.MAIN_HAND);
                            if (CommonProxy.addItem(server, args[1], args[2], stack)) {
                                sender.sendMessage(new TextComponentString("物品已添加!"));
                            } else {
                                sender.sendMessage(new TextComponentString("相应名称物品已存在，请更换名称或使用 set 覆盖 !"));
                            }
                        } else {
                            sender.sendMessage(new TextComponentString("此命令只能有游戏内玩家执行 !"));
                        }
                    } else {
                        sender.sendMessage(new TextComponentString("/isv add type name"));
                    }
                    break;
                case "set":
                    if (args.length == 3) {
                        if (sender instanceof EntityPlayerMP) {
                            ItemStack stack = ((EntityPlayerMP) sender).getHeldItem(EnumHand.MAIN_HAND);
                            CommonProxy.setItem(server, args[1], args[2], stack);
                            sender.sendMessage(new TextComponentString("物品已设置!"));
                        } else {
                            sender.sendMessage(new TextComponentString("此命令只能有游戏内玩家执行 !"));
                        }
                    } else {
                        sender.sendMessage(new TextComponentString("/isv set type name"));
                    }
                    break;
                case "remove":
                    break;
                case "open":
                    if (sender instanceof EntityPlayerMP) {
                        CommonProxy.openMenu((EntityPlayerMP) sender);
                    } else {
                        sender.sendMessage(new TextComponentString("此命令只能有游戏内玩家执行 !"));
                    }
                    break;
                default:
                    sender.sendMessage(new TextComponentString(getUsage(sender)));
            }
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, "gamemode");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if ("give".equals(args[0])) {
            switch (args.length) {
                case 2:
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                case 3:
                    return CommonProxy.getListOfTypes(server, args[args.length - 1]);
                case 4:
                    return CommonProxy.getListOfNames(server, args[2], args[args.length - 1]);
                default:
            }
        }
        return new ArrayList<>();
    }
}
