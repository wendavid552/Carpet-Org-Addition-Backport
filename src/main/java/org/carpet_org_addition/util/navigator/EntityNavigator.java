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

package org.carpet_org_addition.util.navigator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityNavigator extends AbstractNavigator {
    /**
     * 此导航器追踪的实体
     */
    private final Entity entity;

    /**
     * 该导航器是否在玩家到达目的地后仍继续导航
     */
    private final boolean isContinue;

    public EntityNavigator(@NotNull ServerPlayerEntity player, Entity entity, boolean isContinue) {
        super(player);
        this.entity = entity;
        this.isContinue = isContinue;
    }

    public void tick() {
        if (this.terminate()) {
            return;
        }
        if (this.targetDeath()) {
            // 如果目标实体死亡，就清除玩家的追踪器
            MessageUtils.sendTextMessageToHud(this.player, TextUtils.getTranslate("carpet.commands.navigate.hud.target_death"));
            this.clear();
            return;
        }
        World world = entity.getWorld();
        Text text;
        if (player.getWorld().equals(world)) {
            // 获取翻译后的文本信息
            Text in = TextUtils.getTranslate(IN, entity.getName(), TextUtils.simpleBlockPos(entity.getBlockPos()));
            Text distance = TextUtils.getTranslate(DISTANCE,
                    MathUtils.getBlockIntegerDistance(player.getBlockPos(), entity.getBlockPos()));
            // 添加上下箭头
            Vec3d eyePos = this.entity.getEyePos();
            text = getHUDText(eyePos, in, distance);
        } else {
            text = TextUtils.getTranslate(IN, entity.getName(),
                    TextUtils.appendAll(WorldUtils.getDimensionName(entity.getWorld()),
                            TextUtils.simpleBlockPos(entity.getBlockPos())));
        }
        MessageUtils.sendTextMessageToHud(this.player, text);
    }

    /**
     * @return 此导航器是否需要停止
     */
    @Override
    protected boolean terminate() {
        if (this.isContinue) {
            return false;
        }
        if (this.player.getServerWorld().equals(this.entity.getWorld())
                && MathUtils.getBlockDistance(player.getBlockPos(), entity.getBlockPos()) <= 8) {
            // 停止追踪
            MessageUtils.sendTextMessageToHud(this.player, TextUtils.getTranslate(REACH));
            this.clear();
            return true;
        }
        return false;
    }

    /**
     * 目标实体是否死亡或被清除
     *
     * @return 是否需要停止追踪这个实体
     */
    private boolean targetDeath() {
        if (this.entity == null) {
            return true;
        }
        if (this.entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.isRemoved()) {
                // 如果目标实体是玩家，并且玩家已被删除
                // 就从服务器的玩家管理器中查找新的玩家实体对象，如果找到了，设置目标为新玩家，如果找不到，玩家的追踪器对象不变
                // 只要这个玩家在线，就不需要清除这个追踪器，因为玩家可以复活
                MinecraftServer server = serverPlayerEntity.getServerWorld().getServer();
                UUID uuid = serverPlayerEntity.getUuid();
                ServerPlayerEntity newPlayer = server.getPlayerManager().getPlayer(uuid);
                if (newPlayer == null) {
                    // 如果玩家已经下线，返回true
                    return true;
                }
                this.navigatorInterface.setNavigator(newPlayer, this.isContinue);
            }
            return false;
        }
        if (this.entity instanceof LivingEntity livingEntity) {
            Entity.RemovalReason removalReason = livingEntity.getRemovalReason();
            // 生物已死亡，或者生物被不可逆的清除
            if (livingEntity.isDead() || (removalReason != null && removalReason.shouldDestroy())) {
                return true;
            }
            // 目标实体被可逆的清除，就尝试在维度找到重新目标实体，如果找到，重新设置玩家的追踪器对象，然后返回false
            MinecraftServer server = livingEntity.getWorld().getServer();
            if (server == null) {
                return true;
            }
            // 从服务器查找新实体对象
            UUID uuid = entity.getUuid();
            // 从服务器查找新实体对象
            for (ServerWorld world : server.getWorlds()) {
                Entity newEntity = world.getEntity(uuid);
                if (newEntity == null || newEntity.isRemoved()) {
                    continue;
                }
                // 将玩家的追踪器目标设置为这个新实体
                this.navigatorInterface.setNavigator(newEntity, this.isContinue);
                return false;
            }
            // 目标活着，没有被清除，返回false
            return false;
        }
        return this.entity.isRemoved();
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public EntityNavigator copy(ServerPlayerEntity player) {
        return new EntityNavigator(player, this.entity, this.isContinue);
    }
}
