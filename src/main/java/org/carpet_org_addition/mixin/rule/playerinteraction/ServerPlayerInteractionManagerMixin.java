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

package org.carpet_org_addition.mixin.rule.playerinteraction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

//服务器最大玩家交互距离
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    //TODO: 根据1.18.2-的情况未来换用MixinExtras的@ModifyExpressionValue修改if里面的语句
    //#if MC>=12005
    //$$ @WrapOperation(
    //$$         method = "processBlockBreakingAction",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/server/network/ServerPlayerEntity;canInteractWithBlockAt (Lnet/minecraft/util/math/BlockPos;D)Z"
    //$$         )
    //$$ )
    //$$ private boolean processBlockBreakingAction(ServerPlayerEntity instance, BlockPos blockPos, double v, Operation<Boolean> original) {
    //$$     if (MathUtils.isDefaultDistance()) {
    //$$         return original.call(instance, blockPos, v);
    //$$     }
    //$$     double d = MathUtils.getMaxBreakSquaredDistance();
    //$$     return (new Box(blockPos)).squaredMagnitude(instance.getEyePos()) < d * d;
    //$$ }
    //#elseif MC>=11904
    @WrapOperation(method = "processBlockBreakingAction", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double processBlockBreakingAction(Operation<Double> original) {
        if (MathUtils.isDefaultDistance()) {
            return original.call();
        }
        return MathUtils.getMaxBreakSquaredDistance();
    }
    //#else
    //$$ @ModifyConstant(method = "processBlockBreakingAction", constant = @Constant(doubleValue = 36.0D))
    //$$ private double processBlockBreakingAction(double original) {
    //$$     if (MathUtils.isDefaultDistance()) {
    //$$         return original;
    //$$     }
    //$$     return MathUtils.getMaxBreakSquaredDistance();
    //$$ }
    //#endif
}
