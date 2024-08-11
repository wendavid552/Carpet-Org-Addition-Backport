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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.task.ServerTaskManagerInterface;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;

//TODO: 直接生成一只点燃的苦力怕
public class CreeperCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("creeper")
                .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandCreeper))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(CreeperCommand::creeperExplosion)));
    }

    // 创建苦力怕并爆炸
    private static int creeperExplosion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        World world = targetPlayer.getWorld();
        // 创建苦力怕对象
        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
        // 产生爆炸
        //#if MC<11900
        //$$ Explosion.DestructionType destructionType = world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
        //$$ targetPlayer.getWorld().createExplosion(creeper,
        //$$                targetPlayer.getX() + MathUtils.randomInt(-3, 3),
        //$$                targetPlayer.getY() + MathUtils.randomInt(-1, 1),
        //$$                targetPlayer.getZ() + MathUtils.randomInt(-3, 3),
        //$$                3F, false, Explosion.DestructionType.NONE);
        //#else
        targetPlayer.getWorld().createExplosion(creeper,
                targetPlayer.getX() + MathUtils.randomInt(-3, 3),
                targetPlayer.getY() + MathUtils.randomInt(-1, 1),
                targetPlayer.getZ() + MathUtils.randomInt(-3, 3),
                3F, false, World.ExplosionSourceType.NONE);
        //#endif
        // 删除这只苦力怕
        creeper.discard();
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayer();
        if (sourcePlayer != null) {
            CarpetOrgAddition.LOGGER.info("{}在{}周围制造了一场苦力怕爆炸",
                    sourcePlayer.getName().getString(), targetPlayer.getName().getString());
        }
        return 1;
    }

    private static class CreeperExplosionTask extends ServerTask {
        // 苦力怕爆炸延迟
        private int countdown = 30;
        private final ServerPlayerEntity player;
        private final CreeperEntity creeper;

        private CreeperExplosionTask(ServerPlayerEntity player) {
            this.player = player;
            this.creeper = new CreeperEntity(EntityType.CREEPER, player.getWorld());
            // 传送到玩家周围
            this.teleport();
        }

        // 将苦力怕传送到合适位置
        private void teleport() {
            BlockPos playerPos = player.getBlockPos();
            BlockPos fromPos = new BlockPos(playerPos.getX() - 3, playerPos.getY() - 1, playerPos.getZ() - 3);
            BlockPos toPos = new BlockPos(playerPos.getX() + 3, playerPos.getY() + 1, playerPos.getZ() + 3);
            SelectionArea selectionArea = new SelectionArea(new Box(fromPos, toPos));
            ArrayList<BlockPos> list = new ArrayList<>();
            World world = this.player.getWorld();
            // 获取符合条件的坐标
            for (BlockPos blockPos : selectionArea) {
                // 当前方块是空气
                if (world.getBlockState(blockPos).isAir()
                        // 下方方块是实心方块
                        && world.getBlockState(blockPos.down()).isSolidBlock(world, blockPos.down())
                        // 上方方块是空气
                        && world.getBlockState(blockPos.up()).isAir()) {
                    list.add(blockPos);
                }
            }
            // 将苦力怕传送到随机坐标
            BlockPos randomPos = list.isEmpty() ? playerPos : list.get(MathUtils.randomInt(1, list.size()) - 1);
            this.creeper.teleport(randomPos.getX(), randomPos.getY(), randomPos.getZ());
        }

        @Override
        public void tick() {
            if (this.countdown == 30) {
                // 播放爆炸引线音效
                this.creeper.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0f, 0.5f);
                this.creeper.emitGameEvent(GameEvent.PRIME_FUSE);
            }
            this.countdown--;
            if (this.countdown == 0) {
                // 产生爆炸
                this.player.getWorld().createExplosion(creeper, this.creeper.getX(), this.player.getY(),
                        this.player.getZ(), 3F, false, World.ExplosionSourceType.NONE);
            }
        }

        @Override
        public boolean stopped() {
            // 苦力怕倒计时结束或苦力怕距离玩家超过7格
            if (this.countdown < 0 || this.player.distanceTo(this.creeper) > 7) {
                this.creeper.discard();
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "苦力怕爆炸";
        }
    }
}
