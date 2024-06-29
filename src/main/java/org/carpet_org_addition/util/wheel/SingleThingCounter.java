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

package org.carpet_org_addition.util.wheel;

/**
 * 统计单一种类的事物数量的计数器
 */
public class SingleThingCounter {
    private int count = 0;

    public SingleThingCounter() {
    }

    /**
     * 计数器递增
     */
    public void add() {
        this.add(1);
    }

    /**
     * 计数器递减
     */
    public void decrement() {
        this.add(-1);
    }

    /**
     * 将计数器增加指定值
     *
     * @param number 要增加的数量
     */
    public void add(int number) {
        this.count += number;
    }

    /**
     * 获取计算器的值
     */
    public int get() {
        return count;
    }

    /**
     * 设置计数器当前的数量
     *
     * @param count 要设置的值
     */
    public void set(int count) {
        this.count = count;
    }

    /**
     * 判断计数器是否归零
     */
    public boolean nonZero() {
        return this.count != 0;
    }
}
