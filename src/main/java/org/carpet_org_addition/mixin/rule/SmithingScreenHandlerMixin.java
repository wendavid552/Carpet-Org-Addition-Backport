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

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.SmithingScreenHandler;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.mixin.util.ForgingScreenHandlerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin {
    //#if MC>=12000
    @Shadow
    protected abstract List<ItemStack> getInputStacks();
    //#endif

    // 可重复使用的锻造模板
    @Inject(method = "decrementStack", at = @At("HEAD"), cancellable = true)
    private void decrement(int slot, CallbackInfo ci) {
        //#if MC>=11904
        if (CarpetOrgAdditionSettings.reusableSmithingTemplate && slot == 0) {
            //#if MC>=12000
            ItemStack itemStack = this.getInputStacks().get(slot);
            //#else
            //$$ ItemStack itemStack = ((ForgingScreenHandlerAccessor)this).getInput().getStack(slot);
            //#endif
            if (itemStack.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) || itemStack.isIn(ItemTags.TRIM_TEMPLATES)) {
                ci.cancel();
            }
        }
        //#endif
    }
}