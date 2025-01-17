/*
 * This file is part of the Carpet Org Addition project, licensed under the
 * MIT License
 *
 * Copyright (c) 2024 cdqtzrc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.carpet_org_addition.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
//#if MC>11900
import net.minecraft.command.CommandRegistryAccess;
//#endif
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("SpellCheckingInspection")
public class SendMessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandNodeFactory commandNodeFactory) {
        dispatcher.register(CommandManager.literal("sendMessage")
                .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandSendMessage))
                .then(CommandManager.literal("copy")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendReplicableText)))
                .then(CommandManager.literal("url")
                        .then(CommandManager.argument("url", StringArgumentType.string())
                                .executes(SendMessageCommand::sendClickableLink)))
                .then(CommandManager.literal("location")
                        .executes(SendMessageCommand::sendSelfLocation))
                .then(CommandManager.literal("color")
                        .then(CommandManager.argument("color", ColorArgumentType.color())
                                .then(CommandManager.argument("text", StringArgumentType.string())
                                        .executes(SendMessageCommand::sendColorText))))
                .then(CommandManager.literal("strikethrough")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendStrikethroughText)))
                .then(CommandManager.literal("formatting")
                        .then(CommandManager.argument("text", StringArgumentType.string())
                                .executes(SendMessageCommand::sendFormattingText)))
                .then(CommandManager.literal("item")
                        .executes(context -> SendMessageCommand.sendItemHoverableText(context, true))
                        .then(CommandManager.argument("itemStack", commandNodeFactory.itemStack())
                                .executes(context -> sendItemHoverableText(context, false)))));
    }

    //发送可复制文本
    private static int sendReplicableText(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并进行非空判断
        ServerCommandSource source = context.getSource();
        //在输入命令时输入的消息
        String text = StringArgumentType.getString(context, "text");
        //给文本添加颜色，单击事件，鼠标悬停事件
        MutableText copy = TextUtils.copy(text, text, TextUtils.getTranslate("chat.copy.click"), Formatting.GREEN);
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, copy));
        return 1;
    }

    //发送可点击链接
    private static int sendClickableLink(CommandContext<ServerCommandSource> context) {
        //获取命令来源，并非空判断
        ServerCommandSource source = context.getSource();
        //创建可变文本对象
        String text = StringArgumentType.getString(context, "url");
        MutableText url = TextUtils.url(text, text, TextUtils.getTranslate("carpet.commands.sendMessage.url.click_open_url").getString(), null);
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, url));
        return 1;
    }

    //发送自己的位置
    private static int sendSelfLocation(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        BlockPos blockPos = player.getBlockPos();
//        MutableText mutableText = switch (WorldUtils.getDimensionId(player.getWorld())) {
//            case "minecraft:overworld" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.overworld",
//                    player.getDisplayName(),
//                    TextUtils.blockPos(blockPos, Formatting.GREEN),
//                    TextUtils.blockPos(MathUtils.getTheNetherPos(player), Formatting.RED));
//            case "minecraft:the_nether" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.the_nether",
//                    player.getDisplayName(),
//                    TextUtils.blockPos(blockPos, Formatting.RED),
//                    TextUtils.blockPos(MathUtils.getOverworldPos(player), Formatting.GREEN));
//            case "minecraft:the_end" -> TextUtils.getTranslate("carpet.commands.sendMessage.location.the_end",
//                    player.getDisplayName(),
//                    TextUtils.blockPos(blockPos, Formatting.DARK_PURPLE));
//            default -> TextUtils.getTranslate("carpet.commands.sendMessage.location.default",
//                    player.getDisplayName(),
//                    WorldUtils.getDimensionId(player.getWorld()),
//                    TextUtils.blockPos(blockPos, null));
//        };
        MutableText mutableText;
        switch (WorldUtils.getDimensionId(player.getWorld())) {
            case "minecraft:overworld":
                mutableText = TextUtils.getTranslate("carpet.commands.sendMessage.location.overworld",
                        player.getDisplayName(),
                        TextUtils.blockPos(blockPos, Formatting.GREEN),
                        TextUtils.blockPos(MathUtils.getTheNetherPos(player), Formatting.RED));
                break;
            case "minecraft:the_nether":
                mutableText = TextUtils.getTranslate("carpet.commands.sendMessage.location.the_nether",
                        player.getDisplayName(),
                        TextUtils.blockPos(blockPos, Formatting.RED),
                        TextUtils.blockPos(MathUtils.getOverworldPos(player), Formatting.GREEN));
                break;
            case "minecraft:the_end":
                mutableText = TextUtils.getTranslate("carpet.commands.sendMessage.location.the_end",
                        player.getDisplayName(),
                        TextUtils.blockPos(blockPos, Formatting.DARK_PURPLE));
                break;
            default:
                mutableText = TextUtils.getTranslate("carpet.commands.sendMessage.location.default",
                        player.getDisplayName(),
                        WorldUtils.getDimensionId(player.getWorld()),
                        TextUtils.blockPos(blockPos, null));
        }
        MessageUtils.broadcastTextMessage(context.getSource(), mutableText);
        return 1;
    }

    // 发送带颜色的文本
    private static int sendColorText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取文本的颜色
        Formatting color = ColorArgumentType.getColor(context, "color");
        // 获取要发送的文本内容
        String text = StringArgumentType.getString(context, "text");
        // 发送消息
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, TextUtils.regularStyle(text, color)));
        return 1;
    }

    // 发送带删除线的消息
    private static int sendStrikethroughText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取要发送的文本内容
        String text = StringArgumentType.getString(context, "text");
        // 发送消息
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source,
                TextUtils.regularStyle(text, Formatting.WHITE, false, false, false, true)));
        return 1;
    }

    // 发送可格式化文本
    private static int sendFormattingText(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        // 获取要发送的文本内容，并将“$”符号替换为“§”
        String text = StringArgumentType.getString(context, "text").replaceAll("\\$", "§");
        MessageUtils.broadcastTextMessage(source, appendPlayerName(source, TextUtils.createText(text)));
        return 1;
    }

    // 在文本前添加玩家名（如果玩家不为null）
    private static MutableText appendPlayerName(ServerCommandSource source, MutableText text) {
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) sourceEntity;
            // 如果玩家不为null，则发送消息时在文本前添加玩家名
            return TextUtils.appendAll(player.getDisplayName(), ": ", text);
        }
        return text;
    }

    // 发送手上的物品的悬停文本
    private static int sendItemHoverableText(CommandContext<ServerCommandSource> context, boolean requiredPlayer) throws CommandSyntaxException {
        ItemStack itemStack;
        if (requiredPlayer) {
            ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
            // 获取玩家主手上的物品
            ItemStack mainHandStack = player.getMainHandStack();
            // 如果玩家主手上的物品为空，就获取玩家副手的物品
            itemStack = mainHandStack.isEmpty() ? player.getOffHandStack() : mainHandStack;
            // 如果副手的物品还是为空，直接抛出异常结束方法
            if (itemStack.isEmpty()) {
                throw CommandUtils.createException("carpet.commands.sendMessage.item.empty");
            }
        } else {
            itemStack = ItemStackArgumentType.getItemStackArgument(context, "itemStack").createStack(1, false);
        }
        MessageUtils.broadcastTextMessage(context.getSource(), itemStack.toHoverableText());
        return 1;
    }
}
