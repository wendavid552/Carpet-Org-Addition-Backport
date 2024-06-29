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

package org.carpet_org_addition.util.task;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ServerTaskManagerInterface {
    void addTask(ServerTask task);

    /**
     * @return 获取任务列表
     */
    ArrayList<ServerTask> getTaskList();

    /**
     * 查找符合条件的任务
     *
     * @param clazz     用来判断任务对象是否是T类的对象
     * @param predicate 条件谓词
     * @return 包含所有符合条件的任务的集合
     */
    default <T> List<T> findTask(Class<T> clazz, Predicate<T> predicate) {
        return this.getTaskList().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @NotNull
    static ServerTaskManagerInterface getInstance(MinecraftServer server) {
        return (ServerTaskManagerInterface) server;
    }
}
