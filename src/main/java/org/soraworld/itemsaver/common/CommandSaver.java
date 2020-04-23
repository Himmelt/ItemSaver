package org.soraworld.itemsaver.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Himmelt
 */
public class CommandSaver extends CommandBase {
    @Override
    public String getCommandName() {
        return "itemsaver";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "isv give|add|set|remove|open";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("saver", "isv");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MinecraftServer server = MinecraftServer.getServer();
        if (args.length > 0) {
            switch (args[0]) {
                case "give":
                    if (args.length >= 3) {
                        EntityPlayerMP target = getPlayer(sender, args[1]);
                        String type = args[2];
                        String name = null;
                        if (args.length >= 4) {
                            name = args[3];
                        }
                        ItemTypeData data = CommonProxy.getTypeData(server, type);
                        int amount = args.length >= 5 ? Integer.parseInt(args[4]) : -1;
                        data.give(target, name, amount);
                        CommandBase.notifyOperators(sender, this, "commands.give.success", new ChatComponentText(" [" + type + (name == null ? "" : "-" + name) + "] "), 1, target.getCommandSenderName());
                    } else {
                        sender.addChatMessage(new ChatComponentText("/isv give <player> <type> [item] [amount]"));
                    }
                    break;
                case "add":
                    if (args.length == 3) {
                        if (sender instanceof EntityPlayerMP) {
                            ItemStack stack = ((EntityPlayerMP) sender).getHeldItem();
                            if (CommonProxy.addItem(server, args[1], args[2], stack)) {
                                sender.addChatMessage(new ChatComponentText("物品已添加!"));
                            } else {
                                sender.addChatMessage(new ChatComponentText("相应名称物品已存在，请更换名称或使用 set 覆盖 !"));
                            }
                        } else {
                            sender.addChatMessage(new ChatComponentText("此命令只能有游戏内玩家执行 !"));
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText("/isv add <type> <name>"));
                    }
                    break;
                case "set":
                    if (args.length == 3) {
                        if (sender instanceof EntityPlayerMP) {
                            ItemStack stack = ((EntityPlayerMP) sender).getHeldItem();
                            CommonProxy.setItem(server, args[1], args[2], stack);
                            sender.addChatMessage(new ChatComponentText("物品已设置!"));
                        } else {
                            sender.addChatMessage(new ChatComponentText("此命令只能有游戏内玩家执行 !"));
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText("/isv set <type> <name>"));
                    }
                    break;
                case "remove":
                    if (args.length >= 2) {
                        String type = args[1];
                        if (args.length >= 3) {
                            CommonProxy.removeItem(server, type, args[2]);
                            sender.addChatMessage(new ChatComponentText("已移除分类 - " + type + " 下的物品 - " + args[2]));
                        } else {
                            CommonProxy.removeType(server, type);
                            sender.addChatMessage(new ChatComponentText("已移除分类 - " + type));
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText("/isv remove <type> [name]"));
                    }
                    break;
                case "open":
                    if (sender instanceof EntityPlayerMP) {
                        CommonProxy.openMenu((EntityPlayerMP) sender);
                    } else {
                        sender.addChatMessage(new ChatComponentText("此命令只能有游戏内玩家执行 !"));
                    }
                    break;
                default:
                    sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        } else {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, "gamemode");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if ("give".equals(args[0])) {
            MinecraftServer server = MinecraftServer.getServer();
            switch (args.length) {
                case 2:
                    return CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames());
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
