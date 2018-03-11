package org.soraworld.itemsaver.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.soraworld.itemsaver.constant.IMod;
import org.soraworld.itemsaver.item.IItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;
import static net.minecraft.command.CommandBase.getPlayer;
import static org.soraworld.itemsaver.ItemSaver.api;

public class CommandSaver implements ICommand {

    @Override
    public String getCommandName() {
        return IMod.MODID;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "isv.help.usage";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> alias = new ArrayList<>();
        alias.add("isv");
        return alias;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            switch (args[0]) {
                case "add":
                    throw new WrongUsageException("isv.help.add");
                case "give":
                    throw new WrongUsageException("isv.help.give");
                case "list":
                    for (String type : api.get().keySet()) {
                        IChatComponent _type = new ChatComponentText(type).setChatStyle(IMod.YELLOW);
                        sender.addChatMessage(new ChatComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                        HashMap<String, IItemStack> map = api.get(type);
                        for (String name : map.keySet()) {
                            IChatComponent _name = new ChatComponentText(name).setChatStyle(IMod.RED);
                            sender.addChatMessage(new ChatComponentTranslation("isv.list.item", IMod.PREFIX, _name, map.get(name).getText()));
                        }
                    }
                    break;
                case "save":
                    api.save();
                    sender.addChatMessage(new ChatComponentTranslation("isv.save", IMod.PREFIX));
                    break;
                case "clear":
                    api.clear();
                    sender.addChatMessage(new ChatComponentTranslation("isv.clear", IMod.PREFIX));
                    break;
                case "reload":
                    api.reload();
                    sender.addChatMessage(new ChatComponentTranslation("isv.reload", IMod.PREFIX));
                    break;
                case "remove":
                    throw new WrongUsageException("isv.help.remove");
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "add":
                    throw new WrongUsageException("isv.help.add");
                case "give":
                    throw new WrongUsageException("isv.help.give");
                case "list":
                    HashMap<String, IItemStack> map = api.get(args[1]);
                    IChatComponent _type = new ChatComponentText(args[1]).setChatStyle(IMod.YELLOW);
                    sender.addChatMessage(new ChatComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                    for (String name : map.keySet()) {
                        IChatComponent _name = new ChatComponentText(name).setChatStyle(IMod.RED);
                        //IChatComponent hint = map.get(name).getTextComponent();
                        //hint.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + args[1] + " " + name));
                        sender.addChatMessage(new ChatComponentTranslation("isv.list.item", IMod.PREFIX, _name, map.get(name).getText()));
                    }
                    break;
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1]);
                    IChatComponent type = new ChatComponentText(args[1]).setChatStyle(IMod.YELLOW);
                    sender.addChatMessage(new ChatComponentTranslation("isv.type.remove", IMod.PREFIX, type));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length == 3) {
            switch (args[0]) {
                case "add":
                    if (sender instanceof EntityPlayerMP) {
                        ItemStack it = ((EntityPlayerMP) sender).getHeldItem();
                        if (it.getItem() != null) {
                            IItemStack stack = api.add(args[1], args[2], it);
                            IChatComponent type = new ChatComponentText(args[1]).setChatStyle(IMod.YELLOW);
                            IChatComponent name = new ChatComponentText(args[2]).setChatStyle(IMod.RED);
                            sender.addChatMessage(new ChatComponentTranslation("isv.name.add", IMod.PREFIX, type, name, stack.getText()));
                        } else {
                            sender.addChatMessage(new ChatComponentTranslation("isv.help.null", IMod.PREFIX).setChatStyle(IMod.RED));
                        }
                    } else {
                        throw new WrongUsageException("isv.help.cmd");
                    }
                    break;
                case "give":
                    EntityPlayerMP target = getPlayer(sender, args[1]);
                    HashMap<String, IItemStack> map = api.get(args[2]);
                    for (String name : map.keySet()) {
                        IItemStack stack = map.get(name);
                        if (stack != null && stack.get().getItem() != null) {
                            api.give(sender, target, stack, stack.get().stackSize);
                        }
                    }
                    CommandBase.func_152373_a(sender, this, "commands.give.success", new ChatComponentText(" [" + args[2] + "] "), 1, target.getCommandSenderName());
                    break;
                case "list":
                    throw new WrongUsageException("isv.help.list");
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1], args[2]);
                    IChatComponent type = new ChatComponentText(args[1]).setChatStyle(IMod.YELLOW);
                    IChatComponent name = new ChatComponentText(args[2]).setChatStyle(IMod.RED);
                    sender.addChatMessage(new ChatComponentTranslation("isv.name.remove", IMod.PREFIX, type, name));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length >= 4 && args[0].equals("give")) {
            EntityPlayerMP target = getPlayer(sender, args[1]);
            IItemStack stack = api.get(args[2], args[3]);
            if (stack != null && stack.get().getItem() != null) {
                int count = stack.get().stackSize;
                if (args.length == 5 && args[4].matches("[0-9]{1,8}")) {
                    count = Integer.valueOf(args[4]);
                }
                api.give(sender, target, stack, count);
                CommandBase.func_152373_a(sender, this, "commands.give.success", stack.getText(), count, target.getCommandSenderName());
            } else {
                throw new WrongUsageException("isv.help.null");
            }
        } else {
            throw new WrongUsageException("isv.help.usage");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equals("give")) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        if (args.length == 3 && args[0].equals("give")) {
            return getListOfTypes(args[args.length - 1]);
        }
        if (args.length == 4 && args[0].equals("give")) {
            return getListOfNames(args[2], args[args.length - 1]);
        }
        return new ArrayList<>();
    }

    private static List<String> getListOfTypes(String head) {
        List<String> types = new ArrayList<>();
        for (String type : api.get().keySet()) {
            if (type.startsWith(head)) {
                types.add(type);
            }
        }
        return types;
    }

    private static List<String> getListOfNames(String type, String head) {
        List<String> names = new ArrayList<>();
        for (String name : api.get(type).keySet()) {
            if (name.startsWith(head)) {
                names.add(name);
            }
        }
        return names;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length >= 1 && args[0].equals("give") && index == 1;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CommandSaver) {
            return getCommandName().compareTo(((CommandSaver) o).getCommandName());
        }
        return -1;
    }
}
