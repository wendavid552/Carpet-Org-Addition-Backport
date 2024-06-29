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

package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFindFeedback<T extends AbstractFindResult> extends Thread {
    public static final String TIME_OUT = "carpet.commands.finder.timeout";
    /**
     * 用来获取发送消息的服务器命令源
     */
    protected final CommandContext<ServerCommandSource> context;
    /**
     * 保存查找结果的集合
     */
    protected final ArrayList<T> list;
    /**
     * 最多显示多少条消息
     */
    protected final int maxCount;
    protected final long startTime = System.currentTimeMillis();

    protected AbstractFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<T> list, int maxCount) {
        this.context = context;
        this.list = list;
        this.maxCount = maxCount;
    }

    // 检查查找是否超时
    protected final void checkTimeOut() throws TimeoutException {
        if (System.currentTimeMillis() - startTime > 1000) {
            //一秒内没有输出完所有消息，直接中断当前线程执行
            throw new TimeoutException();
        }
    }

    /**
     * 发送命令反馈
     */
    protected abstract void sendFeedback() throws TimeoutException;
}
