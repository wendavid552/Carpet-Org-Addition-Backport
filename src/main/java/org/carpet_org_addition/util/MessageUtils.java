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

package org.carpet_org_addition.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.carpet_org_addition.CarpetOrgAddition;

import java.util.ArrayList;
import java.util.Objects;

public class MessageUtils {
    private MessageUtils() {
    }

    /**
     * 让一个玩家发送带有特殊样式的文本，文本内容仅对消息发送者可见
     *
     * @param player  要发送文本消息的玩家
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessage(PlayerEntity player, Text message) {
        player.sendMessage(message, false);
    }

    /**
     * 让一个玩家发送带有特殊样式的文本，文本会显示在屏幕中下方的HUD上，文本内容仅对消息发送者可见
     *
     * @param player  要发送文本消息的玩家
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessageToHud(PlayerEntity player, Text message) {
        player.sendMessage(message, true);
    }

    /**
     * 让一个玩家发送带有特殊样式的文本，文本内容仅对消息发送者可见
     *
     * @param source  要发送文本消息的命令源
     * @param message 发送文本消息的内容
     */
    public static void sendTextMessage(ServerCommandSource source, Text message) {
        source.sendFeedback(
                //#if MC>=12000
                () -> message
                //#else
                //$$ message
                //#endif
                , false
        );
    }

    // Send system message to server
    public static void sendSystemMessage(MinecraftServer server, Text text) {
        //#if MC<11900
        //$$ server.sendSystemMessage(text, Util.NIL_UUID);
        //#else
        server.sendMessage(text);
        //#endif
    }

    // Send system message to player
    public static void sendSystemMessage(PlayerEntity player, Text text) {
        //#if MC<11900
        //$$ player.sendSystemMessage(text, Util.NIL_UUID);
        //#else
        player.sendMessage(text);
        //#endif
    }

    /**
     * 让服务器命令源发送指定内容的消息，消息内容仅对消息发送者可见
     *
     * @param source  发送消息的消息源
     * @param message 要发送消息的内容
     */
    @Deprecated
    public static void sendStringMessage(ServerCommandSource source, String message) {
        sendTextMessage(source, TextUtils.literal(message));
    }

    public static void broadcastServerTextMessage(MinecraftServer server, Text text) {
        Objects.requireNonNull(server, "无法获取服务器对象");
        sendSystemMessage(server, text);
        server.getPlayerManager().getPlayerList().forEach(player -> sendSystemMessage(player, text));
    }

    /**
     * 广播指定内容的消息，消息对所有玩家可见，不带冒号
     *
     * @param player            1.通过这个服务器命令源对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，player不是消息的发送者。<br/>
     *                          2.如果containPlayerName为true，用来在消息前追加玩家名
     * @param message           消息的内容
     * @param containPlayerName 是否在消息前追加玩家名
     */
    @Deprecated
    public static void broadcastStringMessage(PlayerEntity player, String message, boolean containPlayerName) {
        broadcastTextMessage(player, containPlayerName ? TextUtils.appendAll(player.getDisplayName(), message)
                            : TextUtils.literal(message));
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param player  通过这个玩家对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，player不是消息的发送者
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(PlayerEntity player, Text message) {
        broadcastServerTextMessage(player.getServer(), message);
    }

    /**
     * 广播一条带有特殊样式的文本消息
     *
     * @param source  通过这个服务器命令源对象获取玩家管理器对象，然后通过玩家管理器对象发送消息，source不是消息的发送者
     * @param message 要广播消息的内容
     */
    public static void broadcastTextMessage(ServerCommandSource source, Text message) {
        broadcastServerTextMessage(source.getServer(), message);
    }

    /**
     * 发送一条可以被翻译的消息做为命令的执行反馈，消息内容仅消息发送者可见
     */
    public static void sendCommandFeedback(CommandContext<ServerCommandSource> context, String key, Object... obj) {
        MessageUtils.sendCommandFeedback(context.getSource(), key, obj);
    }

    public static void sendCommandFeedback(ServerCommandSource source, String key, Object... obj) {
        MessageUtils.sendTextMessage(source, TextUtils.getTranslate(key, obj));
    }

    public static void sendCommandFeedback(ServerCommandSource source, Text text) {
        MessageUtils.sendTextMessage(source, text);
    }

    /**
     * 发送一条红色的可以被翻译的消息做为命令的执行反馈，消息内容仅消息发送者可见
     */
    public static void sendCommandErrorFeedback(CommandContext<ServerCommandSource> context, String key, Object... obj) {
        MessageUtils.sendCommandErrorFeedback(context.getSource(), key, obj);
    }

    public static void sendCommandErrorFeedback(ServerCommandSource source, String key, Object... obj) {
        MessageUtils.sendTextMessage(source, TextUtils.setColor(TextUtils.getTranslate(key, obj), Formatting.RED));
    }

    /**
     * 发送多条带有特殊样式的消息，每一条消息单独占一行，消息内容仅发送者可见
     *
     * @param source 消息的发送者，消息内容仅发送者可见
     * @param list   存储所有要发送的消息的集合
     */
    public static void sendListMessage(ServerCommandSource source, ArrayList<MutableText> list) {
        for (MutableText mutableText : list) {
            sendTextMessage(source, mutableText);
        }
    }
}
