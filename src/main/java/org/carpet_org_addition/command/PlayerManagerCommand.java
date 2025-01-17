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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.constant.CommandSyntaxExceptionConstants;
import org.carpet_org_addition.util.fakeplayer.FakePlayerSerial;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.task.ServerTaskManagerInterface;
import org.carpet_org_addition.util.task.playerscheduletask.DelayedLoginTask;
import org.carpet_org_addition.util.task.playerscheduletask.DelayedLogoutTask;
import org.carpet_org_addition.util.task.playerscheduletask.PlayerScheduleTask;
import org.carpet_org_addition.util.task.playerscheduletask.ReLoginTask;
import org.carpet_org_addition.util.wheel.WorldFormat;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class PlayerManagerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // 延迟登录节点
        RequiredArgumentBuilder<ServerCommandSource, Integer> loginNode = CommandManager.argument("delayed", IntegerArgumentType.integer(1));
        for (TimeUnit unit : TimeUnit.values()) {
            // 添加时间单位
            loginNode.then(CommandManager.literal(unit.getName())
                    .executes(context -> addDelayedLoginTask(context, unit)));
        }
        // 延迟登出节点
        RequiredArgumentBuilder<ServerCommandSource, Integer> logoutNode = CommandManager.argument("delayed", IntegerArgumentType.integer(1));
        for (TimeUnit unit : TimeUnit.values()) {
            logoutNode.then(CommandManager.literal(unit.getName())
                    .executes(context -> addDelayedLogoutTask(context, unit)));
        }
        dispatcher.register(CommandManager.literal("playerManager")
                .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerManager))
                .then(CommandManager.literal("save")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(context -> savePlayer(context, false))
                                .then(CommandManager.argument("annotation", StringArgumentType.string())
                                        .executes(context -> withAnnotationSavePlayer(context, false)))))
                .then(CommandManager.literal("spawn")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(defaultSuggests())
                                .executes(PlayerManagerCommand::spawnPlayer)))
                .then(CommandManager.literal("resave")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(context -> savePlayer(context, true))
                                .then(CommandManager.argument("annotation", StringArgumentType.string())
                                        .executes(context -> withAnnotationSavePlayer(context, true)))))
                .then(CommandManager.literal("list")
                        .executes(PlayerManagerCommand::list))
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .suggests(defaultSuggests())
                                .executes(PlayerManagerCommand::delete)))
                .then(CommandManager.literal("schedule")
                        .then(CommandManager.literal("relogin")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .suggests(reLoginTaskSuggests())
                                        .then(CommandManager.argument("interval", IntegerArgumentType.integer(1))
                                                .executes(PlayerManagerCommand::setReLogin))
                                        .then(CommandManager.literal("stop")
                                                .executes(PlayerManagerCommand::stopReLogin))))
                        .then(CommandManager.literal("login")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .suggests(defaultSuggests())
                                        .then(loginNode)))
                        .then(CommandManager.literal("logout")
                                .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                        .then(logoutNode)))
                        .then(CommandManager.literal("cancel")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .suggests(cancelSuggests())
                                        .executes(PlayerManagerCommand::cancelScheduleTask)))
                        .then(CommandManager.literal("list")
                                .executes(PlayerManagerCommand::listScheduleTask))));
    }

    // cancel子命令自动补全
    private static @NotNull SuggestionProvider<ServerCommandSource> cancelSuggests() {
        return (context, builder) -> {
            MinecraftServer server = context.getSource().getServer();
            ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
            ArrayList<String> list = new ArrayList<>();
            // 将任务的玩家名添加到集合
            instance.findTask(PlayerScheduleTask.class, task -> true).forEach(task -> list.add(task.getPlayerName()));
            return CommandSource.suggestMatching(list, builder);
        };
    }

    // 自动补全玩家名
    private static SuggestionProvider<ServerCommandSource> defaultSuggests() {
        return (context, builder) -> CommandSource.suggestMatching(new WorldFormat(context.getSource().getServer(),
                FakePlayerSerial.PLAYER_DATA).toImmutableFileList().stream()
                .filter(file -> file.getName().endsWith(WorldFormat.JSON_EXTENSION))
                .map(file -> WorldFormat.removeExtension(file.getName()))
                .map(StringArgumentType::escapeIfRequired), builder);
    }

    // relogin子命令自动补全
    public static @NotNull SuggestionProvider<ServerCommandSource> reLoginTaskSuggests() {
        return (context, builder) -> {
            MinecraftServer server = context.getSource().getServer();
            ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
            // 所有正在周期性上下线的玩家
            List<String> taskList = instance.getTaskList().stream()
                    .filter(task -> task instanceof ReLoginTask)
                    .map(task -> ((ReLoginTask) task).getPlayerName()).toList();
            // 所有在线玩家
            List<String> onlineList = server.getPlayerManager().getPlayerList().stream()
                    .map(player -> player.getName().getString()).toList();
            HashSet<String> players = new HashSet<>();
            players.addAll(taskList);
            players.addAll(onlineList);
            return CommandSource.suggestMatching(players.stream(), builder);
        };
    }

    // 列出每一个玩家
    private static int list(CommandContext<ServerCommandSource> context) {
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        int count = FakePlayerSerial.list(context, worldFormat);
        if (count == 0) {
            // 没有玩家被保存
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.list.no_player");
            return 0;
        }
        return count;
    }

    // 保存假玩家数据
    private static int savePlayer(CommandContext<ServerCommandSource> context, boolean resave) throws CommandSyntaxException {
        EntityPlayerMPFake fakePlayer = CommandUtils.getArgumentFakePlayer(context);
        FakePlayerSerial fakePlayerSerial = new FakePlayerSerial(fakePlayer);
        savePlayer(context, fakePlayerSerial, fakePlayer, resave);
        return 1;
    }

    // 保存玩家带注释
    private static int withAnnotationSavePlayer(CommandContext<ServerCommandSource> context, boolean resave) throws CommandSyntaxException {
        EntityPlayerMPFake fakePlayer = CommandUtils.getArgumentFakePlayer(context);
        String annotation = StringArgumentType.getString(context, "annotation");
        FakePlayerSerial fakePlayerSerial = new FakePlayerSerial(fakePlayer, annotation);
        savePlayer(context, fakePlayerSerial, fakePlayer, resave);
        return 1;
    }

    // 保存玩家
    private static void savePlayer(CommandContext<ServerCommandSource> context, FakePlayerSerial fakePlayerSerial, EntityPlayerMPFake fakePlayer, boolean resave) throws CommandSyntaxException {
        try {
            if (fakePlayerSerial.save(context.getSource().getServer(), resave)) {
                // 重新保存
                MessageUtils.sendCommandFeedback(context.getSource(),
                        "carpet.commands.playerManager.save.resave",
                        fakePlayer.getDisplayName());
            } else {
                // 首次保存
                MessageUtils.sendCommandFeedback(context.getSource(),
                        "carpet.commands.playerManager.save.success",
                        fakePlayer.getDisplayName());
            }
        } catch (IOException e) {
            throw CommandUtils.createException("carpet.commands.playerManager.save.fail", fakePlayer.getDisplayName());
        }
    }

    // 生成假玩家
    private static int spawnPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        try {
            JsonObject json = WorldFormat.loadJson(worldFormat.getFile(name));
            // 生成假玩家
            FakePlayerSerial.spawn(name, context.getSource().getServer(), json);
        } catch (JsonParseException e) {
            // 无法解析json文件
            throw CommandSyntaxExceptionConstants.JSON_PARSE_EXCEPTION;
        } catch (RuntimeException e) {
            // 尝试生成假玩家时出现意外问题
            throw CommandUtils.createException("carpet.commands.playerManager.spawn.fail");
        } catch (IOException e) {
            // 从文件读取“%s”玩家数据失败
            throw CommandUtils.createException("carpet.commands.playerManager.spawn.io", name);
        }
        return 1;
    }

    // 删除玩家信息
    private static int delete(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldFormat worldFormat = new WorldFormat(context.getSource().getServer(), FakePlayerSerial.PLAYER_DATA);
        String name = StringArgumentType.getString(context, "name");
        File file = worldFormat.getFile(name);
        // 文件存在且文件删除成功
        if (file.isFile() && file.delete()) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.playerManager.delete.success");
        } else {
            throw CommandUtils.createException("carpet.commands.playerManager.delete.fail");
        }
        return 1;
    }

    // 设置不断重新上线下线
    private static int setReLogin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取目标假玩家名
        String name = StringArgumentType.getString(context, "name");
        int interval = IntegerArgumentType.getInteger(context, "interval");
        MinecraftServer server = context.getSource().getServer();
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        // 如果任务存在，修改任务，否则添加任务
        ReLoginTask task = getReLoginTask(instance, name);
        if (task == null) {
            // 添加任务
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
            if (player == null) {
                // 玩家不存在
                throw CommandUtils.createException("argument.entity.notfound.player");
            } else {
                // 目标玩家不是假玩家
                CommandUtils.checkFakePlayer(player);
            }
            instance.addTask(new ReLoginTask(name, interval, server, player.getServerWorld().getRegistryKey()));
        } else {
            // 修改周期时间
            task.setInterval(interval);
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.relogin.set_interval", name, interval);
        }
        return interval;
    }

    // 获取假玩家周期上下线任务
    private static ReLoginTask getReLoginTask(ServerTaskManagerInterface instance, String name) {
        List<ReLoginTask> list = instance.getTaskList().stream()
                .filter(task -> task instanceof ReLoginTask)
                .map(task -> (ReLoginTask) task).toList();
        for (ReLoginTask task : list) {
            if (Objects.equals(task.getPlayerName(), name)) {
                return task;
            }
        }
        return null;
    }

    // 停止重新上线下线
    private static int stopReLogin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取目标假玩家名
        String name = StringArgumentType.getString(context, "name");
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        List<ReLoginTask> list = instance.findTask(ReLoginTask.class, task -> Objects.equals(task.getPlayerName(), name));
        if (list.isEmpty()) {
            throw CommandUtils.createException("carpet.commands.playerManager.schedule.cancel.fail");
        }
        list.forEach(task -> {
            instance.getTaskList().remove(task);
            task.onCancel(context);
        });
        return 1;
    }

    // 延时上线
    private static int addDelayedLoginTask(CommandContext<ServerCommandSource> context, TimeUnit unit) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        String name = StringArgumentType.getString(context, "name");
        // 等待时间
        long tick = unit.getDelayed(context);
        List<DelayedLoginTask> list = instance.findTask(DelayedLoginTask.class, loginTask -> Objects.equals(name, loginTask.getPlayerName()));
        MutableText time = TextUtils.hoverText(GameUtils.tickToTime(tick), GameUtils.tickToRealTime(tick));
        if (list.isEmpty()) {
            // 添加上线任务
            WorldFormat worldFormat = new WorldFormat(server, FakePlayerSerial.PLAYER_DATA);
            JsonObject jsonObject;
            try {
                jsonObject = WorldFormat.loadJson(worldFormat.getFile(name));
            } catch (IOException e) {
                throw CommandUtils.createException("carpet.commands.playerManager.schedule.read_file");
            }
            instance.addTask(new DelayedLoginTask(server, name, jsonObject, tick));
            String key = server.getPlayerManager().getPlayer(name) == null
                    // <玩家>将于<时间>后上线
                    ? "carpet.commands.playerManager.schedule.login"
                    // <玩家>将于<时间>后再次尝试上线
                    : "carpet.commands.playerManager.schedule.login.try";
            // 玩家名上的悬停提示
            MutableText info = FakePlayerSerial.info(jsonObject);
            // 发送命令反馈
            MessageUtils.sendCommandFeedback(context, key, TextUtils.hoverText(name, info), time);
        } else {
            // 修改上线时间
            DelayedLoginTask task = list.get(0);
            // 为名称添加悬停文本
            MutableText info = TextUtils.hoverText(name, FakePlayerSerial.info(task.getJsonObject()));
            task.setDelayed(tick);
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.login.modify", info, time);
        }
        return (int) tick;
    }

    // 延迟下线
    private static int addDelayedLogoutTask(CommandContext<ServerCommandSource> context, TimeUnit unit) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        EntityPlayerMPFake fakePlayer = CommandUtils.getArgumentFakePlayer(context);
        // 获取假玩家延时下线游戏刻数
        long tick = unit.getDelayed(context);
        MutableText time = TextUtils.hoverText(GameUtils.tickToTime(tick), GameUtils.tickToRealTime(tick));
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        List<DelayedLogoutTask> list = instance.findTask(DelayedLogoutTask.class, logoutTask -> fakePlayer.equals(logoutTask.getFakePlayer()));
        // 添加新任务
        if (list.isEmpty()) {
            // 添加延时下线任务
            instance.addTask(new DelayedLogoutTask(server, fakePlayer, tick));
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.logout", fakePlayer.getDisplayName(), time);
        } else {
            // 修改退出时间
            DelayedLogoutTask task = list.get(0);
            task.setDelayed(tick);
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.logout.modify", fakePlayer.getDisplayName(), time);
        }
        return (int) tick;
    }

    // 取消任务
    private static int cancelScheduleTask(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        String name = StringArgumentType.getString(context, "name");
        // 获取符合条件的任务列表
        List<PlayerScheduleTask> list = instance.findTask(PlayerScheduleTask.class, task -> Objects.equals(task.getPlayerName(), name));
        if (list.isEmpty()) {
            throw CommandUtils.createException("carpet.commands.playerManager.schedule.cancel.fail");
        }
        ArrayList<ServerTask> tasks = instance.getTaskList();
        list.forEach(task -> {
            // 删除任务，发送命令反馈
            tasks.remove(task);
            task.onCancel(context);
        });
        return list.size();
    }

    // 列出所有任务
    private static int listScheduleTask(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
        List<PlayerScheduleTask> list = instance.findTask(PlayerScheduleTask.class, take -> true);
        if (list.isEmpty()) {
            MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.list.empty");
        } else {
            list.forEach(task -> task.sendEachMessage(context.getSource()));
        }
        return list.size();
    }

    /**
     * 时间单位
     */
    private enum TimeUnit {
        /**
         * tick
         */
        TICK,
        /**
         * 秒
         */
        SECOND,
        /**
         * 分钟
         */
        MINUTE,
        /**
         * 小时
         */
        HOUR;

        // 获取单位名称
        private String getName() {
            return switch (this) {
                case TICK -> "t";
                case SECOND -> "s";
                case MINUTE -> "min";
                case HOUR -> "h";
            };
        }

        // 将游戏刻转化为对应单位
        private long getDelayed(CommandContext<ServerCommandSource> context) {
            int delayed = IntegerArgumentType.getInteger(context, "delayed");
            return switch (this) {
                case TICK -> delayed;
                case SECOND -> delayed * 20L;
                case MINUTE -> delayed * 1200L;
                case HOUR -> delayed * 72000L;
            };
        }
    }
}