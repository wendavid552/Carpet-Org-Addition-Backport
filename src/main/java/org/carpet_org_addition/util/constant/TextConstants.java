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

package org.carpet_org_addition.util.constant;

import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;

public class TextConstants {
    /**
     * 主世界
     */
    public static final Text OVERWORLD = TextUtils.getTranslate("carpet.command.dimension.overworld");
    /**
     * 下界
     */
    public static final Text THE_NETHER = TextUtils.getTranslate("carpet.command.dimension.the_nether");
    /**
     * 末地
     */
    public static final Text THE_END = TextUtils.getTranslate("carpet.command.dimension.the_end");
    public static final Text TRUE = TextUtils.getTranslate("carpet.command.boolean.true");
    public static final Text FALSE = TextUtils.getTranslate("carpet.command.boolean.false");

    public static Text getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }
}
