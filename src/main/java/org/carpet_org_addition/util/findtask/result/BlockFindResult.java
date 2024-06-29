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

package org.carpet_org_addition.util.findtask.result;

import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.TextUtils;

public class BlockFindResult extends AbstractFindResult {
    /**
     * 方块所在的位置
     */
    private final BlockPos blockPos;
    /**
     * 命令执行前玩家所在的位置，用来计算玩家与方块的距离
     */
    private final BlockPos sourceBlockPos;

    public BlockFindResult(BlockPos blockPos, BlockPos sourceBlockPos) {
        this.blockPos = blockPos;
        this.sourceBlockPos = sourceBlockPos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public MutableText toText() {
        return TextUtils.getTranslate(
                "carpet.commands.finder.block.feedback",
                MathUtils.getBlockIntegerDistance(sourceBlockPos, blockPos),
                TextUtils.blockPos(blockPos, Formatting.GREEN));
    }
}
