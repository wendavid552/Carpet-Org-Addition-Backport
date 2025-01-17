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

import carpet.patches.EntityPlayerMPFake;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.carpet_org_addition.util.navigator.NavigatorInterface;
import org.carpet_org_addition.util.compat.minecraft.PlayerEntityLastDeathPosition.PlayerEntityLastDeathPositionRecorder;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class NavigatorCommand {

    private static final String START_NAVIGATION = "carpet.commands.navigate.start_navigation";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("navigate")
                .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandNavigate))
                .then(CommandManager.literal("entity")
                        .then(CommandManager.argument("entity", EntityArgumentType.entity())
                                .executes(context -> navigateToEntity(context, false))
                                .then(CommandManager.literal("continue")
                                        .executes(context -> navigateToEntity(context, true)))))
                .then(CommandManager.literal("waypoint")
                        .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandLocations))
                        .then(CommandManager.argument("waypoint", StringArgumentType.string())
                                .suggests(LocationsCommand.suggestion())
                                .executes(NavigatorCommand::navigateToWaypoint)))
                .then(CommandManager.literal("stop")
                        .executes(NavigatorCommand::stopNavigate))
                .then(CommandManager.literal("uuid")
                        .then(CommandManager.argument("uuid", StringArgumentType.string())
                                .executes(NavigatorCommand::navigateToEntityForUUID)))
                .then(CommandManager.literal("blockPos")
                        .then(CommandManager.argument("blockPos", BlockPosArgumentType.blockPos())
                                .executes(NavigatorCommand::navigateToBlock)))
                .then(CommandManager.literal("spawnpoint")
                        .executes(NavigatorCommand::navigateToSpawnPoint))
                .then(CommandManager.literal("lastDeathLocation")
                        .executes(context -> navigateToLastDeathLocation(context, true))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> navigateToLastDeathLocation(context, false)))));
    }

    // 开始导航实体
    private static int navigateToEntity(CommandContext<ServerCommandSource> context, boolean isContinue) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        Entity entity = EntityArgumentType.getEntity(context, "entity");
        // 如果目标是玩家，广播消息
        MutableText text = TextUtils.getTranslate(START_NAVIGATION, player.getDisplayName(), entity.getDisplayName());
        ((NavigatorInterface) player).setNavigator(entity, isContinue);
        if (shouldBeBroadcasted(entity, player)) {
            // 设置为斜体淡灰色
            text = TextUtils.toItalic(TextUtils.setColor(text, Formatting.GRAY));
            MessageUtils.broadcastTextMessage(context.getSource(), text);
        } else {
            MessageUtils.sendCommandFeedback(context.getSource(), text);
        }
        return 1;
    }

    // 开始导航到路径点
    private static int navigateToWaypoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        MinecraftServer server = context.getSource().getServer();
        String waypoint = StringArgumentType.getString(context, "waypoint");
        try {
            Optional<Waypoint> optional = Waypoint.load(server, waypoint);
            if (optional.isPresent()) {
                ((NavigatorInterface) player).setNavigator(optional.get());
                MessageUtils.sendCommandFeedback(context, START_NAVIGATION, player.getDisplayName(), "[" + waypoint + "]");
                return 1;
            } else {
                throw new NullPointerException();
            }
        } catch (IOException | NullPointerException e) {
            throw CommandUtils.createException("carpet.commands.locations.list.parse", waypoint);
        }
    }

    // 根据UUID获取实体并导航
    private static int navigateToEntityForUUID(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        UUID uuid;
        try {
            // 解析UUID
            uuid = UUID.fromString(StringArgumentType.getString(context, "uuid"));
        } catch (IllegalArgumentException e) {
            throw CommandUtils.createException("carpet.commands.navigate.parse_uuid_fail");
        }
        // 从服务器寻找这个UUID的实体
        MinecraftServer server = context.getSource().getServer();
        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(uuid);
            if (entity == null) {
                continue;
            }
            ((NavigatorInterface) player).setNavigator(entity, false);
            MutableText text = TextUtils.getTranslate(START_NAVIGATION, player.getDisplayName(), entity.getDisplayName());
            if (shouldBeBroadcasted(entity, player)) {
                // 将字体设置为灰色斜体
                text = TextUtils.toItalic(TextUtils.setColor(text, Formatting.GRAY));
                MessageUtils.broadcastTextMessage(context.getSource(), text);
            } else {
                MessageUtils.sendCommandFeedback(context.getSource(), text);
            }
            return 1;
        }
        // 未找到实体
        throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
    }

    // 是否应该广播导航消息
    private static boolean shouldBeBroadcasted(Entity entity, ServerPlayerEntity player) {
        if (entity == player || entity instanceof EntityPlayerMPFake) {
            return false;
        }
        return entity instanceof ServerPlayerEntity;
    }

    // 停止导航
    private static int stopNavigate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ((NavigatorInterface) player).clearNavigator();
        MessageUtils.sendTextMessageToHud(player, TextUtils.getTranslate("carpet.commands.navigate.hud.stop"));
        return 1;
    }

    // 导航到指定坐标
    private static int navigateToBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, "blockPos");
        NavigatorInterface instance = NavigatorInterface.getInstance(player);
        World world = player.getWorld();
        // 设置导航器，维度为玩家当前所在维度
        instance.setNavigator(blockPos, world);
        // 发送命令反馈
        MessageUtils.sendCommandFeedback(context, START_NAVIGATION, player.getDisplayName(),
                TextUtils.blockPos(blockPos, WorldUtils.getColor(world)));
        return 1;
    }

    // 导航到重生点
    private static int navigateToSpawnPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        MutableText spawnPoint = TextUtils.getTranslate("carpet.commands.navigate.name.spawnpoint");
        try {
            NavigatorInterface.getInstance(player).setNavigator(Objects.requireNonNull(player.getSpawnPointPosition()),
                    player.server.getWorld(Objects.requireNonNull(player.getSpawnPointDimension())), spawnPoint);
        } catch (NullPointerException e) {
            throw CommandUtils.createException("carpet.commands.navigate.unable_to_find", player.getDisplayName(), spawnPoint);
        }
        MessageUtils.sendCommandFeedback(context, START_NAVIGATION, player.getDisplayName(), spawnPoint);
        return 1;
    }

    // 导航到上一次死亡位置
    private static int navigateToLastDeathLocation(CommandContext<ServerCommandSource> context, boolean self) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        ServerPlayerEntity target = self ? player : CommandUtils.getArgumentPlayer(context);
        Optional<GlobalPos> lastDeathPos =
                //#if MC>=11904
                target.getLastDeathPos();
                //#else
                //$$ ((PlayerEntityLastDeathPositionRecorder)target).org$getLastDeathPos();
                //#endif
        // 导航器目标的名称
        MutableText lastDeathLocation = TextUtils.getTranslate("carpet.commands.navigate.name.last_death_location");
        // 非空判断
        if (lastDeathPos.isEmpty()) {
            throw CommandUtils.createException("carpet.commands.navigate.unable_to_find", target.getDisplayName(), lastDeathLocation);
        }
        MutableText name = self ? lastDeathLocation
                : TextUtils.getTranslate("carpet.commands.navigate.hud.of", target.getDisplayName(), lastDeathLocation);
        // 获取死亡坐标和死亡维度
        GlobalPos globalPos = lastDeathPos.get();
        NavigatorInterface.getInstance(player).setNavigator(globalPos.getPos(),
                context.getSource().getServer().getWorld(globalPos.getDimension()), name);
        MutableText message = TextUtils.getTranslate(START_NAVIGATION, player.getDisplayName(), name);
        if (self || player == target) {
            MessageUtils.sendTextMessage(player, message);
        } else {
            MessageUtils.broadcastTextMessage(context.getSource(), TextUtils.setColor(TextUtils.toItalic(message), Formatting.GRAY));
        }
        return 1;
    }
}
