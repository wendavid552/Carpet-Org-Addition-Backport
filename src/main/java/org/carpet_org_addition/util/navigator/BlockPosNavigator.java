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

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.jetbrains.annotations.NotNull;

public class BlockPosNavigator extends AbstractNavigator {
    protected final BlockPos blockPos;
    protected final World world;

    public BlockPosNavigator(@NotNull ServerPlayerEntity player, BlockPos blockPos, World world) {
        super(player);
        this.blockPos = blockPos;
        this.world = world;
    }

    @Override
    public void tick() {
        if (this.terminate()) {
            return;
        }
        MutableText text;
        if (this.player.getWorld().equals(this.world)) {
            MutableText in = TextUtils.simpleBlockPos(this.blockPos);
            MutableText distance = TextUtils.getTranslate(DISTANCE, MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.blockPos));
            text = getHUDText(this.blockPos.toCenterPos(),in,distance);
        } else {
            text = TextUtils.appendAll(WorldUtils.getDimensionName(this.world), TextUtils.simpleBlockPos(this.blockPos));
        }
        MessageUtils.sendTextMessageToHud(this.player, text);
    }

    @Override
    public BlockPosNavigator copy(ServerPlayerEntity player) {
        return new BlockPosNavigator(player, this.blockPos, this.world);
    }

    @Override
    protected boolean terminate() {
        // 玩家与目的地在同一维度
        if (this.player.getServerWorld().equals(this.world)) {
            if (MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.blockPos) <= 8) {
                // 到达目的地，停止追踪
                MessageUtils.sendTextMessageToHud(this.player, TextUtils.getTranslate(REACH));
                this.clear();
                return true;
            }
        }
        return false;
    }
}
