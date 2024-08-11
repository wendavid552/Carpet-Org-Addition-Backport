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

package org.carpet_org_addition.util.task;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import carpet.utils.Messenger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
//#if MC>=12002
//$$ import net.minecraft.server.network.ConnectedClientData;
//$$ import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
//#endif

import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.mixin.compat.carpet.EntityPlayerMPFakeManiplater.EntityPlayerMPFakeInvoker;
import org.carpet_org_addition.mixin.rule.EntityAccessor;
import org.carpet_org_addition.mixin.rule.PlayerEntityAccessor;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;

import static org.carpet_org_addition.CarpetOrgAddition.LOGGER;

public class ReLoginTask extends PlayerScheduleTask {
    // 假玩家名
    private final String playerName;
    // 重新上线的时间间隔
    private int interval;
    // 距离下一次重新上线所需的时间
    private int remainingTick;
    private final MinecraftServer server;
    private final RegistryKey<World> dimensionId;
    // 当前任务是否已经结束
    private boolean stop = false;
    // 假玩家重新上线的倒计时
    private int canSpawn = 2;

    public ReLoginTask(String playerName, int interval, MinecraftServer server, RegistryKey<World> dimensionId) {
        this.playerName = playerName;
        this.interval = interval;
        this.remainingTick = this.interval;
        this.server = server;
        this.dimensionId = dimensionId;
    }

    @Override
    public void tick() {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.playerName);
        if (player == null) {
            if (this.canSpawn == 0) {
                homePositionSpawn(this.playerName, this.server, this.dimensionId);
                this.canSpawn = 2;
            } else {
                this.canSpawn--;
            }
        } else if (this.remainingTick <= 0) {
            this.remainingTick = this.interval;
            if (player instanceof EntityPlayerMPFake fakePlayer) {
                // 如果假玩家坠入虚空，设置任务为停止
                if (fakePlayer.getY() < fakePlayer.getServerWorld().getBottomY() - 64) {
                    this.stop();
                }
                // 让假玩家退出游戏
                this.logoutPlayer(fakePlayer);
            }
        } else {
            this.remainingTick--;
        }
    }


    /**
     * 让假玩家退出游戏
     *
     * @see EntityPlayerMPFake#kill(Text)
     * @see EntityPlayerMPFake#shakeOff()
     */
    @SuppressWarnings("JavadocReference")
    private void logoutPlayer(EntityPlayerMPFake fakePlayer) {
        Text reason = Messenger.s("Killed");
        // 停止骑行
        if (fakePlayer.getVehicle() instanceof PlayerEntity) {
            fakePlayer.stopRiding();
        }
        for (Entity passenger : fakePlayer.getPassengersDeep()) {
            if (passenger instanceof PlayerEntity) {
                passenger.stopRiding();
            }
        }
        // 退出游戏
        this.server.send(new ServerTask(this.server.getTicks(), () -> {
            try {
                CarpetOrgAddition.hiddenLoginMessages = true;
                fakePlayer.networkHandler.onDisconnected(reason);
            } finally {
                CarpetOrgAddition.hiddenLoginMessages = false;
            }
        }));
    }

    @Override
    public boolean stopped() {
        return this.stop;
    }

    @Override
    public String toString() {
        return this.playerName + "周期性重新上线";
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void onCancel(CommandContext<ServerCommandSource> context) {
        MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.relogin.cancel", this.playerName);
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.playerName);
        if (player == null) {
            homePositionSpawn(this.playerName, this.server, this.dimensionId);
        }
    }

    @Override
    public void sendEachMessage(ServerCommandSource source) {
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerManager.schedule.relogin", this.playerName, this.interval);
    }

    public void setInterval(int interval) {
        this.interval = interval;
        this.remainingTick = interval;
    }

    public void stop() {
        this.stop = true;
    }

    /**
     * 在假玩家上一次退出游戏的位置生成假玩家
     *
     * @param username    假玩家名
     * @param dimensionId 假玩家要生成的维度
     */
    private void homePositionSpawn(String username, MinecraftServer server, RegistryKey<World> dimensionId) {
        ServerWorld worldIn = server.getWorld(dimensionId);
        if (worldIn == null) {
            return;
        }
        UserCache.setUseRemote(false);
        GameProfile gameprofile;
        try {
            //TODO: 此处缺少兼容语句，如白名单玩家是否生成，shadow的玩家的例外情况等等
            UserCache userCache = server.getUserCache();
            if (userCache == null) {
                return;
            }
            gameprofile = userCache.findByName(username).orElse(null);
        } finally {
            UserCache.setUseRemote(server.isDedicated() && server.isOnlineMode());
        }
        if (gameprofile == null) {
            LOGGER.warn("Failed to find profile with name {}. Skip respawning.", username);
            return;
        }
        EntityPlayerMPFake fakePlayer = EntityPlayerMPFakeInvoker.createFakePlayer(server, worldIn, gameprofile,
                //#if MC>=12002
                //$$ SyncedClientOptions.createDefault(),
                //#endif
                false);
        fakePlayer.fixStartingPosition = GameUtils::pass;



        try {
            CarpetOrgAddition.hiddenLoginMessages = true;
            server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), fakePlayer
                    //#if MC>=12002
                    //$$ ,new ConnectedClientData(gameprofile, 0, fakePlayer.getClientOptions()
                    //#if MC>=12005
                    //$$ ,true
                    //#endif
                    //$$ )
                    //#endif
            );
        } finally {
            // 假玩家加入游戏后，这个变量必须重写设置为false，防止影响其它广播消息的方法
            CarpetOrgAddition.hiddenLoginMessages = false;
        }


        fakePlayer.setHealth(20.0F);
        ((EntityAccessor) fakePlayer).cancelRemoved();
        //#if MC>=12005
        //$$ fakePlayer.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(0.6F);
        //#elseif MC>=11904
        fakePlayer.setStepHeight(0.6F);
        //#else
        //$$ fakePlayer.stepHeight = 0.6F;
        //#endif
        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(fakePlayer, (byte) ((int) (fakePlayer.headYaw * 256.0F / 360.0F))), dimensionId);
        server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(fakePlayer), dimensionId);
        fakePlayer.getDataTracker().set(PlayerEntityAccessor.getPlayerModelParts(), (byte) 127);
    }
}
