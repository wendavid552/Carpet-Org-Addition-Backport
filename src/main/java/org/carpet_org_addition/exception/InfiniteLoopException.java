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

package org.carpet_org_addition.exception;

/**
 * 无限循环异常
 */
public class InfiniteLoopException extends RuntimeException {
    /**
     * 当前循环的次数
     */
    private int loopCount = 0;
    /**
     * 最大的循环次数，当前循环次数超过这个值时抛出自身的异常
     */
    private final int maxLoopCount;

    public InfiniteLoopException() {
        this.maxLoopCount = 1000;
    }

    public InfiniteLoopException(int maxLoopCount) {
        this.maxLoopCount = maxLoopCount;
    }

    /**
     * 将当前循环次数+1，然后检查循环次数，如果循环次数过多抛出异常
     */
    public void checkLoopCount() {
        loopCount++;
        if (loopCount >= maxLoopCount) {
            throw this;
        }
    }
}
