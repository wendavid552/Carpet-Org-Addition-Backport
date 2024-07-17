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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WaypointNavigator extends AbstractNavigator {
    private final Waypoint waypoint;

    // 路径点所在维度的ID
    private final String waypointDimension;

    public WaypointNavigator(@NotNull ServerPlayerEntity player, Waypoint waypoint) {
        super(player);
        this.waypoint = waypoint;
        if (this.waypoint.getBlockPos() == null) {
            throw new NullPointerException();
        }
        this.waypointDimension = waypoint.getDimension();
    }

    @Override
    public void tick() {
        if (terminate()) {
            return;
        }
        // 路径点的目标位置
        BlockPos blockPos = this.waypoint.getBlockPos();
        // 玩家所在的方块位置
        BlockPos playerBlockPos = this.player.getBlockPos();
        // 玩家所在维度
        String playerDimension = WorldUtils.getDimensionId(this.player.getWorld());
        if (playerDimension.equals(waypointDimension)) {
            // 玩家和路径点在相同的维度
            Text text = this.getHUDText(Vec3d.of(blockPos).add(0.5,0.5,0.5), getIn(blockPos), getDistance(playerBlockPos, blockPos));
            MessageUtils.sendTextMessageToHud(this.player, text);
        } else if (((playerDimension.equals(WorldUtils.OVERWORLD) && waypointDimension.equals(WorldUtils.THE_NETHER))
                || (playerDimension.equals(WorldUtils.THE_NETHER) && waypointDimension.equals(WorldUtils.OVERWORLD)))
                && this.waypoint.getAnotherBlockPos() != null) {
            // 玩家和路径点在不同的维度，但是维度可以互相转换
            // 将坐标设置为斜体
            Text in = TextUtils.getTranslate(IN, waypoint.getName(),
                    TextUtils.toItalic(TextUtils.simpleBlockPos(blockPos)));
            Text text = this.getHUDText(Vec3d.of(this.waypoint.getAnotherBlockPos()).add(0.5,0.5,0.5), in,
                    getDistance(playerBlockPos, this.waypoint.getAnotherBlockPos()));
            MessageUtils.sendTextMessageToHud(this.player, text);
        } else {
            // 玩家和路径点在不同维度
            Text dimensionName = WorldUtils.getDimensionName(WorldUtils.getWorld(this.player.getServer(),
                    this.waypoint.getDimension()));
            MutableText in = TextUtils.getTranslate(IN, waypoint.getName(),
                    TextUtils.appendAll(dimensionName, TextUtils.simpleBlockPos(blockPos)));
            MessageUtils.sendTextMessageToHud(this.player, in);
        }
    }

    @Override
    public boolean terminate() {
        if (Objects.equals(WorldUtils.getDimensionId(this.player.getWorld()), this.waypointDimension)
                && MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.waypoint.getBlockPos()) <= 8) {
            // 到达目的地，停止追踪
            MessageUtils.sendTextMessageToHud(this.player, TextUtils.getTranslate(REACH));
            this.clear();
            return true;
        }
        return false;
    }

    @Override
    public WaypointNavigator copy(ServerPlayerEntity player) {
        if (this.waypoint == null || this.waypoint.getBlockPos() == null) {
            return null;
        }
        return new WaypointNavigator(player, this.waypoint);
    }

    @NotNull
    private MutableText getIn(BlockPos blockPos) {
        return TextUtils.getTranslate(IN, waypoint.getName(), TextUtils.simpleBlockPos(blockPos));
    }

    @NotNull
    private static MutableText getDistance(BlockPos playerBlockPos, BlockPos blockPos) {
        return TextUtils.getTranslate(DISTANCE, MathUtils.getBlockIntegerDistance(playerBlockPos, blockPos));
    }
}
