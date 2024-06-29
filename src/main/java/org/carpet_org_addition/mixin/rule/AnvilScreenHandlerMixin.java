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

package org.carpet_org_addition.mixin.rule;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 没看懂源代码是什么意思之前先不要乱改，防止产生副作用<br/>
 * Wiki上说的操作数是什么，为什么会同时有操作数和累积惩罚，操作数和累积惩罚是什么关系，任凭操作数增加会不会出现别的问题
 *
 * @see <a href="https://zh.minecraft.wiki/w/%E9%93%81%E7%A0%A7%E6%9C%BA%E5%88%B6#%E7%B4%AF%E7%A7%AF%E6%83%A9%E7%BD%9A">铁砧机制#累积惩罚</a>
 */
//防止铁砧过于昂贵
@SuppressWarnings("CommentedOutCode")
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

/*    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        //能不能取下输出槽的物品
        cir.setReturnValue(false);
    }

    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int onTakeOutput(Property property) {
        //取下输出槽物品后消耗多少级经验
        return 39;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I"))
    private int updateResult(Property instance) {
        //如果成本超过这个值，那么输出槽会被设置为空
        return 39;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getRepairCost()I"))
    private int updateResult(ItemStack itemStack) {
        return Math.min(itemStack.getRepairCost(), 39);
    }*/
}
