package org.soraworld.itemsaver.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.arguments.EntityArgument.player;

/**
 * @author Himmelt
 */
public class SaverCommand {

    private static final LiteralArgumentBuilder<CommandSource> add = literal("add").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        ServerPlayerEntity player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            player.sendMessage(new StringTextComponent("物品不能为空!"));
        } else if (CommonProxy.addItem(player.server, type, name, stack)) {
            player.sendMessage(new StringTextComponent("物品已添加!"));
        } else {
            player.sendMessage(new StringTextComponent("相应名称物品已存在，请更换名称或使用 set 覆盖 !"));
        }
        return 1;
    })));
    private static final LiteralArgumentBuilder<CommandSource> set = literal("set").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        ServerPlayerEntity player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            player.sendMessage(new StringTextComponent("物品不能为空!"));
        } else {
            CommonProxy.setItem(player.server, type, name, stack);
            player.sendMessage(new StringTextComponent("物品已设置!"));
        }
        return 1;
    })));
    private static final LiteralArgumentBuilder<CommandSource> remove = literal("remove").then(argument("type", word()).then(argument("name", word()).executes(context -> {
        ServerPlayerEntity player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        String name = context.getArgument("name", String.class);
        CommonProxy.removeItem(player.server, type, name);
        player.sendMessage(new StringTextComponent("已移除分类 - " + type + " 下的物品 - " + name));
        return 1;
    })).executes(context -> {
        ServerPlayerEntity player = context.getSource().asPlayer();
        String type = context.getArgument("type", String.class);
        CommonProxy.removeType(player.server, type);
        player.sendMessage(new StringTextComponent("已移除分类 - " + type));
        return 1;
    }));
    private static final LiteralArgumentBuilder<CommandSource> open = literal("open").executes(context -> {
        ServerPlayerEntity player = context.getSource().asPlayer();
        CommonProxy.openMenu(player);
        return 1;
    });
    private static final LiteralArgumentBuilder<CommandSource> give = literal("give")
            .then(argument("player", player())
                    .then(argument("type", word())
                            .then(argument("name", word())
                                    .then(argument("amount", integer(1)).executes(context -> {
                                        try {
                                            ServerPlayerEntity target = EntityArgument.getPlayer(context, "player");
                                            String type = context.getArgument("type", String.class);
                                            String name = context.getArgument("name", String.class);
                                            int amount = context.getArgument("amount", int.class);
                                            ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                                            data.give(target, name, amount);
                                            context.getSource().sendFeedback(new TranslationTextComponent("commands.give.success.single", amount, new StringTextComponent(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                        return 1;
                                    }))
                                    .executes(context -> {
                                        try {
                                            ServerPlayerEntity target = EntityArgument.getPlayer(context, "player");
                                            String type = context.getArgument("type", String.class);
                                            String name = context.getArgument("name", String.class);
                                            ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                                            data.give(target, name, -1);
                                            context.getSource().sendFeedback(new TranslationTextComponent("commands.give.success.single", 1, new StringTextComponent(" [" + type + "-" + name + "] "), target.getDisplayName()), true);
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                        return 1;
                                    }))
                            .executes(context -> {
                                try {
                                    ServerPlayerEntity target = EntityArgument.getPlayer(context, "player");
                                    String type = context.getArgument("type", String.class);
                                    ItemTypeData data = CommonProxy.getTypeData(context.getSource().getServer(), type);
                                    data.give(target, null, -1);
                                    context.getSource().sendFeedback(new TranslationTextComponent("commands.give.success.single", 1, new StringTextComponent(" [" + type + "] "), target.getDisplayName()), true);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                return 1;
                            })));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("itemsaver")
                .requires((source) -> (source.getEntity() instanceof ServerPlayerEntity) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
        dispatcher.register(literal("isv")
                .requires((source) -> (source.getEntity() instanceof ServerPlayerEntity) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
        dispatcher.register(literal("saver")
                .requires((source) -> (source.getEntity() instanceof ServerPlayerEntity) && source.hasPermissionLevel(2))
                .then(add)
                .then(set)
                .then(remove)
                .then(open)
                .then(give)
        );
    }
}
