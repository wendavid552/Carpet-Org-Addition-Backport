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

// 设置基岩硬度校验
public class BedrockHardnessValidator extends Validator<Float> {
    private BedrockHardnessValidator() {
    }

    /**
     * 对基岩硬度的值进行校验，修改的硬度值必须大于等于0，或者等于-1。因为硬度为负值的方块本身就无法挖掘，设置为其他的负硬度值是没有意义的
     */
    @Override
    public Float validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Float> carpetRule, Float aFloat, String s) {
        return aFloat >= 0 || aFloat == -1 ? aFloat : null;
    }

    /**
     * 输入基岩硬度为非法参数时显示的信息
     */
    @Override
    public String description() {
        return RuleValidatorConstants.greaterThanOrEqualOrNumber(0, -1).getString();
    }
}
