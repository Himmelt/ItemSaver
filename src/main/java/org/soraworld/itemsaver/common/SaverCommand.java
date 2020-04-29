package org.soraworld.itemsaver.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author Himmelt
 */
public class SaverCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher, CommonProxy proxy) {
        LiteralCommandNode<CommandSource> command = dispatcher.register(Commands.literal("itemsaver")
                .requires((source) -> (source.getEntity() instanceof EntityPlayerMP) && source.hasPermissionLevel(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                                    EntityPlayerMP player = context.getSource().asPlayer();
                                    String type = context.getArgument("type", String.class);
                                    String name = context.getArgument("name", String.class);
                                    if (CommonProxy.addItem(player.server, type, name, player.getHeldItemMainhand())) {
                                        player.sendMessage(new TextComponentString("物品已添加!"));
                                    } else {
                                        player.sendMessage(new TextComponentString("相应名称物品已存在，请更换名称或使用 set 覆盖 !"));
                                    }
                                    return 1;
                                }))))
                .then(Commands.literal("set")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                                    EntityPlayerMP player = context.getSource().asPlayer();
                                    String type = context.getArgument("type", String.class);
                                    String name = context.getArgument("name", String.class);
                                    CommonProxy.setItem(player.server, type, name, player.getHeldItemMainhand());
                                    player.sendMessage(new TextComponentString("物品已设置!"));
                                    return 1;
                                }))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .then(Commands.argument("name", StringArgumentType.word()).executes(context -> {
                                    EntityPlayerMP player = context.getSource().asPlayer();
                                    String type = context.getArgument("type", String.class);
                                    String name = context.getArgument("name", String.class);
                                    CommonProxy.removeItem(player.server, type, name);
                                    player.sendMessage(new TextComponentString("已移除分类 - " + type + " 下的物品 - " + name));
                                    return 1;
                                }))
                                .executes(context -> {
                                    EntityPlayerMP player = context.getSource().asPlayer();
                                    String type = context.getArgument("type", String.class);
                                    CommonProxy.removeType(player.server, type);
                                    player.sendMessage(new TextComponentString("已移除分类 - " + type));
                                    return 1;
                                })))
                .then(Commands.literal("open").executes(context -> {
                    EntityPlayerMP player = context.getSource().asPlayer();
                    CommonProxy.openMenu(player);
                    return 1;
                }))
                .then(Commands.literal("give").then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("type", StringArgumentType.word())
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))).executes(context -> {
                                            EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
                                            String type = context.getArgument("type", String.class);
                                            String name = context.getArgument("name", String.class);
                                            int amount = context.getArgument("amount", int.class);
                                            ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                                            data.give(target, name, amount);
                                            context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", amount, new TextComponentString(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
                                            return 1;
                                        }))
                                .executes(context -> {
                                    EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
                                    String type = context.getArgument("type", String.class);
                                    String name = context.getArgument("name", String.class);
                                    ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                                    data.give(target, name, -1);
                                    context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", 1, new TextComponentString(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
                                    return 1;
                                }))
                        .executes(context -> {
                            EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
                            String type = context.getArgument("type", String.class);
                            ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                            data.give(target, null, -1);
                            context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", 1, new TextComponentString(" [" + type + "] "), target.getDisplayName()), true);
                            return 1;
                        })))
        );
        dispatcher.register(Commands.literal("saver").redirect(command));
        dispatcher.register(Commands.literal("isv").redirect(command));
    }
}
