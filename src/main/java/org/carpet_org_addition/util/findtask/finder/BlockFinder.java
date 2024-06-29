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

package org.carpet_org_addition.util.findtask.finder;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.BlockFindResult;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;

public class BlockFinder extends AbstractFinder {
    private final BlockStateArgument argument;
    private final ArrayList<BlockFindResult> list = new ArrayList<>();

    public BlockFinder(ServerWorld world, BlockPos sourcePos, int range, BlockStateArgument blockStateArgument) {
        super(world, sourcePos, range);
        this.argument = blockStateArgument;
    }

    @Override
    public ArrayList<BlockFindResult> startSearch() throws CommandSyntaxException {
        SelectionArea selectionArea = new SelectionArea(this.world, this.sourcePos, this.range);
        long startTimeMillis = System.currentTimeMillis();
        for (BlockPos blockPos : selectionArea) {
            checkTimeOut(startTimeMillis);
            // 如果找到的方块数量过多，直接抛出异常结束方法，不再进行排序
            if (list.size() > FinderCommand.MAXIMUM_STATISTICS) {
                throw CommandUtils.createException("carpet.commands.finder.block.too_much_blocks",
                        TextUtils.getBlockName(this.argument.getBlockState().getBlock()));
            }
            if (argument.test((ServerWorld) this.world, blockPos)) {
                list.add(new BlockFindResult(blockPos, this.sourcePos));
            }
        }
        return this.list;
    }
}
