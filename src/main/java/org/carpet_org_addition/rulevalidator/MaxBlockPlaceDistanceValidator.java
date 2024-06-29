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

package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.constant.RuleValidatorConstants;
import org.jetbrains.annotations.Nullable;

// 最大方块交互距离校验
public class MaxBlockPlaceDistanceValidator extends Validator<Double> {
    public static final double MAX_VALUE = 256.0;

    private MaxBlockPlaceDistanceValidator() {
    }

    @Override
    public Double validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Double> carpetRule, Double aDouble, String s) {
        return (aDouble >= 0 && aDouble <= MAX_VALUE) || aDouble == -1 ? aDouble : null;
    }

    @Override
    public String description() {
        return RuleValidatorConstants.betweenTwoNumberOrNumber(0, (int) MAX_VALUE, -1).getString();
    }
}
