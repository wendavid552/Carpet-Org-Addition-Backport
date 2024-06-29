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

public class RuleValidatorConstants {
    /**
     * 值必须小于等于%s
     */
    public static Text lessThanOrEqual(int number) {
        return TextUtils.getTranslate("carpet.rule.validate.less_than_or_equal", number);
    }

    /**
     * 值必须大于等于%s，或者为%s
     */
    public static Text greaterThanOrEqualOrNumber(int number, int other) {
        return TextUtils.getTranslate("carpet.rule.validate.greater_than_or_equal_or_number", number, other);
    }

    /**
     * 值必须介于%s和%s之间，或者为%s
     */
    public static Text betweenTwoNumberOrNumber(int number1, int number2, int other) {
        return TextUtils.getTranslate("carpet.rule.validate.between_two_number_or_number", number1, number2, other);
    }
}
