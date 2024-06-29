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

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    // 强制开启潜影盒
    @Inject(method = "canOpen", at = @At(value = "HEAD"), cancellable = true)
    private static void canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (canUpdateSuppression(world, pos)) {
            cir.setReturnValue(false);
            return;
        }
        if (CarpetOrgAdditionSettings.openShulkerBoxForcibly) {
            cir.setReturnValue(true);
        }
    }

    // CCE更新抑制器
    @Inject(method = "getComparatorOutput", at = @At("HEAD"))
    private void getComparatorOutput(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        //!world.isClient： 更新抑制不在客户端进行，防止客户端游戏崩溃
        if (canUpdateSuppression(world, pos) && !world.isClient) {
            throw new ClassCastException("类型转换异常，在:"
                    + world.getRegistryKey().getValue() + " " + WorldUtils.toPosString(pos));
        }
    }

    // 潜影盒是否可以更新抑制
    @Unique
    private static boolean canUpdateSuppression(World world, BlockPos pos) {
        if (CarpetOrgAdditionSettings.CCEUpdateSuppression) {
            if (world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity) {
                ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
                String blockName = shulkerBoxBlockEntity.getDisplayName().getString();
                return "更新抑制器".equals(blockName) || "updateSuppression".equalsIgnoreCase(blockName);
            }
        }
        return false;
    }
}
