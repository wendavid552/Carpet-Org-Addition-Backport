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

package org.carpet_org_addition.mixin.command;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.navigator.*;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements NavigatorInterface {
    @Unique
    private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
    @Unique
    private AbstractNavigator navigator;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.navigator == null) {
            return;
        }
        try {
            this.navigator.tick();
        } catch (RuntimeException e) {
            MessageUtils.sendCommandErrorFeedback(thisPlayer.getCommandSource(), "carpet.commands.navigate.exception");
            CarpetOrgAddition.LOGGER.error("导航器没有按照预期工作", e);
            // 清除导航器
            this.clearNavigator();
        }
    }

    // 玩家穿越末地祭坛的传送门时复制身上的数据
    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        AbstractNavigator oldNavigator = ((NavigatorInterface) oldPlayer).getNavigator();
        // 复制追踪器对象
        if (oldNavigator != null) {
            this.navigator = oldNavigator.copy(thisPlayer);
        }
        // 复制假玩家动作管理器对象
        if (thisPlayer instanceof FakePlayerActionInterface && oldPlayer instanceof EntityPlayerMPFake) {
            ((FakePlayerActionInterface) thisPlayer).copyActionManager((EntityPlayerMPFake) oldPlayer);
        }
    }

    @Override
    public AbstractNavigator getNavigator() {
        return this.navigator;
    }

    @Override
    public void setNavigator(Entity entity, boolean isContinue) {
        this.navigator = new EntityNavigator(thisPlayer, entity, isContinue);
    }

    @Override
    public void setNavigator(Waypoint waypoint) {
        this.navigator = new WaypointNavigator(thisPlayer, waypoint);
    }

    @Override
    public void setNavigator(BlockPos blockPos, World world) {
        this.navigator = new BlockPosNavigator(thisPlayer, blockPos, world);
    }

    @Override
    public void setNavigator(BlockPos blockPos, World world, Text name) {
        this.navigator = new HasNamePosNavigator(thisPlayer, blockPos, world, name);
    }

    @Override
    public void clearNavigator() {
        this.navigator = null;
    }
}
