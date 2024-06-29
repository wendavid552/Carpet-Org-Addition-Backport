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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.findtask.feedback.AbstractFindFeedback;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;

public abstract class AbstractFinder {
    protected final World world;
    protected final BlockPos sourcePos;
    protected final int range;

    protected AbstractFinder(World world, BlockPos sourcePos, int range) {
        this.world = world;
        this.sourcePos = sourcePos;
        this.range = range;
    }

    /**
     * 开始查找
     *
     * @return 存储查找结果的集合
     * @throws CommandSyntaxException 方法执行超时后抛出
     */
    public abstract ArrayList<? extends AbstractFindResult> startSearch() throws CommandSyntaxException;

    // 检查查找是否超时
    protected final void checkTimeOut(long startTimeMillis) throws CommandSyntaxException {
        if (System.currentTimeMillis() - startTimeMillis > 3000) {
            //3秒内未完成方块查找，通过抛出异常结束方法
            throw CommandUtils.createException(AbstractFindFeedback.TIME_OUT);
        }
    }
}
