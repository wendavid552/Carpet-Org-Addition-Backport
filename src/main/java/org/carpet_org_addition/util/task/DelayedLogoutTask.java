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

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.NotNull;

public class DelayedLogoutTask extends PlayerScheduleTask {
    private final MinecraftServer server;
    private final EntityPlayerMPFake fakePlayer;
    private long delayed;

    public DelayedLogoutTask(MinecraftServer server, EntityPlayerMPFake fakePlayer, long delayed) {
        this.server = server;
        this.fakePlayer = fakePlayer;
        this.delayed = delayed;
    }

    @Override
    public void tick() {
        if (this.delayed == 0L) {
            if (this.fakePlayer.isRemoved()) {
                // 假玩家可能被真玩家顶替，或者假玩家穿过了末地返回传送门，或者假玩家退出游戏后重新上线
                ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.fakePlayer.getUuid());
                if (player instanceof EntityPlayerMPFake) {
                    player.kill();
                }
            } else {
                this.fakePlayer.kill();
            }
            this.delayed = -1L;
        } else {
            this.delayed--;
        }
    }

    public void setDelayed(long delayed) {
        this.delayed = delayed;
    }

    public EntityPlayerMPFake getFakePlayer() {
        return fakePlayer;
    }

    @Override
    public String getPlayerName() {
        return this.fakePlayer.getName().getString();
    }

    @Override
    public MutableText getCancelMessage() {
        return TextUtils.getTranslate("carpet.commands.playerManager.schedule.logout.cancel",
                this.fakePlayer.getDisplayName(), this.getDisplayTime());
    }

    private @NotNull MutableText getDisplayTime() {
        return TextUtils.hoverText(GameUtils.tickToTime(this.delayed), GameUtils.tickToRealTime(this.delayed));
    }

    @Override
    public void sendEachMessage(ServerCommandSource source) {
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerManager.schedule.logout",
                this.fakePlayer.getDisplayName(), this.getDisplayTime());
    }

    @Override
    public boolean isEndOfExecution() {
        return this.delayed < 0L;
    }
}
