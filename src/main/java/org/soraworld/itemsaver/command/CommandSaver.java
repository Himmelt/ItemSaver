package org.soraworld.itemsaver.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.soraworld.itemsaver.constant.IMod;
import org.soraworld.itemsaver.item.IItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.command.CommandBase.*;
import static org.soraworld.itemsaver.ItemSaver.api;

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
            switch (args[0]) {
                case "add":
                    throw new WrongUsageException("isv.help.add");
                case "give":
                    throw new WrongUsageException("isv.help.give");
                case "list":
                    for (String type : api.get().keySet()) {
                        ITextComponent _type = new TextComponentString(type).setStyle(IMod.YELLOW);
                        sender.sendMessage(new TextComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                        HashMap<String, IItemStack> map = api.get(type);
                        for (String name : map.keySet()) {
                            ITextComponent _name = new TextComponentString(name).setStyle(IMod.RED);
                            sender.sendMessage(new TextComponentTranslation("isv.list.item", IMod.PREFIX, _name, map.get(name).getText()));
                        }
                    }
                    break;
                case "save":
                    api.save();
                    sender.sendMessage(new TextComponentTranslation("isv.save", IMod.PREFIX));
                    break;
                case "clear":
                    api.clear();
                    sender.sendMessage(new TextComponentTranslation("isv.clear", IMod.PREFIX));
                    break;
                case "reload":
                    api.reload();
                    sender.sendMessage(new TextComponentTranslation("isv.reload", IMod.PREFIX));
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
                    ITextComponent _type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    sender.sendMessage(new TextComponentTranslation("isv.list.type", IMod.PREFIX, _type));
                    for (String name : map.keySet()) {
                        ITextComponent _name = new TextComponentString(name).setStyle(IMod.RED);
                        //ITextComponent hint = map.get(name).getTextComponent();
                        //hint.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/isv give @p " + args[1] + " " + name));
                        sender.sendMessage(new TextComponentTranslation("isv.list.item", IMod.PREFIX, _name, map.get(name).getText()));
                    }
                    break;
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1]);
                    ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    sender.sendMessage(new TextComponentTranslation("isv.type.remove", IMod.PREFIX, type));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length == 3) {
            switch (args[0]) {
                case "add":
                    if (sender instanceof EntityPlayerMP) {
                        ItemStack it = ((EntityPlayerMP) sender).getHeldItemMainhand();
                        if (it.getItem() != Items.AIR) {
                            IItemStack stack = api.add(args[1], args[2], it);
                            ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                            ITextComponent name = new TextComponentString(args[2]).setStyle(IMod.RED);
                            sender.sendMessage(new TextComponentTranslation("isv.name.add", IMod.PREFIX, type, name, stack.getText()));
                        } else {
                            sender.sendMessage(new TextComponentTranslation("isv.help.null", IMod.PREFIX).setStyle(IMod.RED));
                        }
                    } else {
                        throw new WrongUsageException("isv.help.cmd");
                    }
                    break;
                case "give":
                    EntityPlayerMP target = getPlayer(server, sender, args[1]);
                    HashMap<String, IItemStack> map = api.get(args[2]);
                    for (String name : map.keySet()) {
                        IItemStack stack = map.get(name);
                        if (stack != null && stack.get().getItem() != Items.AIR) {
                            api.give(sender, target, stack, stack.get().getCount());
                        }
                    }
                    notifyCommandListener(sender, this, "commands.give.success", new TextComponentString(" [" + args[2] + "] "), 1, target.getName());
                    break;
                case "list":
                    throw new WrongUsageException("isv.help.list");
                case "save":
                    throw new WrongUsageException("isv.help.save");
                case "reload":
                    throw new WrongUsageException("isv.help.reload");
                case "remove":
                    api.remove(args[1], args[2]);
                    ITextComponent type = new TextComponentString(args[1]).setStyle(IMod.YELLOW);
                    ITextComponent name = new TextComponentString(args[2]).setStyle(IMod.RED);
                    sender.sendMessage(new TextComponentTranslation("isv.name.remove", IMod.PREFIX, type, name));
                    break;
                default:
                    throw new WrongUsageException("isv.help.usage");
            }
        } else if (args.length >= 4 && args[0].equals("give")) {
            EntityPlayerMP target = getPlayer(server, sender, args[1]);
            IItemStack stack = api.get(args[2], args[3]);
            if (stack != null && stack.get().getItem() != Items.AIR) {
                int count = stack.get().getCount();
                if (args.length == 5 && args[4].matches("[0-9]{1,8}")) {
                    count = Integer.valueOf(args[4]);
                }
                api.give(sender, target, stack, count);
                notifyCommandListener(sender, this, "commands.give.success", stack.getText(), count, target.getName());
            } else {
                throw new WrongUsageException("isv.help.null");
            }
        } else {
            throw new WrongUsageException("isv.help.usage");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(2, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2 && args[0].equals("give")) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
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
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
