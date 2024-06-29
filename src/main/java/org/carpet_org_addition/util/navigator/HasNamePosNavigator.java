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
import net.minecraft.world.World;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.WorldUtils;
import org.jetbrains.annotations.NotNull;

public class HasNamePosNavigator extends BlockPosNavigator {
    private final Text name;

    public HasNamePosNavigator(@NotNull ServerPlayerEntity player, BlockPos blockPos, World world, Text name) {
        super(player, blockPos, world);
        this.name = name;
    }

    @Override
    public void tick() {
        if (this.terminate()) {
            return;
        }
        MutableText text;
        MutableText posText = TextUtils.simpleBlockPos(this.blockPos);
        // 玩家与目的地是否在同一维度
        if (this.player.getWorld().equals(this.world)) {
            MutableText distance = TextUtils.getTranslate(DISTANCE, MathUtils.getBlockIntegerDistance(this.player.getBlockPos(), this.blockPos));
            text = getHUDText(this.blockPos.toCenterPos(), TextUtils.getTranslate(IN, this.name, posText), distance);
        } else {
            text = TextUtils.getTranslate(IN, this.name, TextUtils.appendAll(WorldUtils.getDimensionName(this.world), posText));
        }
        MessageUtils.sendTextMessageToHud(this.player, text);
    }

    @Override
    public HasNamePosNavigator copy(ServerPlayerEntity player) {
        return new HasNamePosNavigator(player, this.blockPos, this.world, this.name);
    }
}
