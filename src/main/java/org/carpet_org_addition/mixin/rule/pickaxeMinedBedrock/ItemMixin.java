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

package org.carpet_org_addition.mixin.rule.pickaxeMinedBedrock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC>=12005
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.ToolComponent;
//#endif

@Mixin(Item.class)
public abstract class ItemMixin {
    // 将镐作为基岩的有效采集工具
    //#if MC>=12005
    //$$ @Inject(method = "getMiningSpeed", at = @At("HEAD"), cancellable = true)
    //$$ private void miningSpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
    //$$     if (CarpetOrgAdditionSettings.pickaxeMinedBedrock && state.getBlock() == Blocks.BEDROCK) {
    //$$         ToolComponent tool = stack.get(DataComponentTypes.TOOL);
    //$$         if (tool == null) {
    //$$             return;
    //$$         }
    //$$         cir.setReturnValue(tool.getSpeed(Blocks.STONE.getDefaultState()));
    //$$     }
    //$$ }
    //#endif
}
