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
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    //#if MC<12005
    //修改方块最大可交互距离
    //#if MC>=11904
    @WrapOperation(method = "onPlayerInteractBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractBlock(Operation<Double> original) {
        if (MathUtils.isDefaultDistance()) {
            return original.call();
        }
        return MathUtils.getMaxBreakSquaredDistance();
    }
    //#else
    //$$ @ModifyConstant(method = "onPlayerInteractBlock", constant = @Constant(doubleValue = 64.0D))
    //$$ private double onPlayerInteractBlock(double original) {
    //$$     if (MathUtils.isDefaultDistance()) {
    //$$         return original;
    //$$     }
    //$$     return MathUtils.getMaxBreakSquaredDistance();
    //$$ }
    //#endif

    //修改方块交互距离第二次检测
    @WrapOperation(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;squaredDistanceTo(DDD)D"))
    private double squaredDistance(ServerPlayerEntity instance, double x, double y, double z, Operation<Double> original) {
        double distance = original.call(instance, x, y, z);
        if (MathUtils.isDefaultDistance()) {
            return distance;
        }
        return distance - MathUtils.getMaxBreakSquaredDistance();
    }

    //修改实体最大交互距离
    //#if MC>=11904
    @WrapOperation(method = "onPlayerInteractEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"))
    private double onPlayerInteractEntity(Operation<Double> original) {
        if (CarpetOrgAdditionSettings.maxBlockPlaceDistanceReferToEntity) {
            return MathUtils.getMaxBreakSquaredDistance();
        }
        return original.call();
    }
    //#else
    //$$ @ModifyConstant(method = "onPlayerInteractEntity", constant = @Constant(doubleValue = 36.0D))
    //$$ private double onPlayerInteractEntity(double original) {
    //$$     if (MathUtils.isDefaultDistance()) {
    //$$         return original;
    //$$     }
    //$$     return MathUtils.getMaxBreakSquaredDistance();
    //$$ }
    //#endif
    //#endif
}
