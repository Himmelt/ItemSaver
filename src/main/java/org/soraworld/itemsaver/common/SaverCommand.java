package org.soraworld.itemsaver.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.players;

/**
 * @author Himmelt
 */
public class SaverCommand {

    private static final LiteralArgumentBuilder<CommandSource> add = literal("add").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        EntityPlayerMP player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            player.sendMessage(new TextComponentString("物品不能为空!"));
        } else if (CommonProxy.addItem(player.server, type, name, stack)) {
            player.sendMessage(new TextComponentString("物品已添加!"));
        } else {
            player.sendMessage(new TextComponentString("相应名称物品已存在，请更换名称或使用 set 覆盖 !"));
        }
        return 1;
    })));
    private static final LiteralArgumentBuilder<CommandSource> set = literal("set").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        EntityPlayerMP player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            player.sendMessage(new TextComponentString("物品不能为空!"));
        } else {
            CommonProxy.setItem(player.server, type, name, stack);
            player.sendMessage(new TextComponentString("物品已设置!"));
        }
        return 1;
    })));
    private static final LiteralArgumentBuilder<CommandSource> remove = literal("remove").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        EntityPlayerMP player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        CommonProxy.removeItem(player.server, type, name);
        player.sendMessage(new TextComponentString("已移除分类 - " + type + " 下的物品 - " + name));
        return 1;
    })).executes(context -> {
        EntityPlayerMP player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        CommonProxy.removeType(player.server, type);
        player.sendMessage(new TextComponentString("已移除分类 - " + type));
        return 1;
    }));
    private static final LiteralArgumentBuilder<CommandSource> open = literal("open").executes(context -> {
        EntityPlayerMP player = context.getSource().asPlayer();
        CommonProxy.openMenu(player);
        return 1;
    });
    private static final LiteralArgumentBuilder<CommandSource> give = literal("give").then(argument("player", players()).then(argument("type", word()).then(argument("name", word()).then(argument("amount", integer(1))).executes(context -> {
        EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        int amount = context.getArgument("amount", int.class);
        ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
        data.give(target, name, amount);
        context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", amount, new TextComponentString(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
        return 1;
    })).executes(context -> {
        EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
        data.give(target, name, -1);
        context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", 1, new TextComponentString(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
        return 1;
    })).executes(context -> {
        EntityPlayerMP target = context.getArgument("player", EntityPlayerMP.class);
        String type = context.getArgument("type", String.class);
        ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
        data.give(target, null, -1);
        context.getSource().sendFeedback(new TextComponentTranslation("commands.give.success.single", 1, new TextComponentString(" [" + type + "] "), target.getDisplayName()), true);
        return 1;
    }));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("itemsaver")
                .requires((source) -> (source.getEntity() instanceof EntityPlayerMP) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
        dispatcher.register(literal("isv")
                .requires((source) -> (source.getEntity() instanceof EntityPlayerMP) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
        dispatcher.register(literal("saver")
                .requires((source) -> (source.getEntity() instanceof EntityPlayerMP) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
    }
}
