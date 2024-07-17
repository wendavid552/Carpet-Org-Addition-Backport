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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private final Entity thisEntity = (Entity) (Object) this;

    // 登山船
//    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
//    private void getStepHeight(CallbackInfoReturnable<Float> cir) {
//        if (CarpetOrgAdditionSettings.climbingBoat
//                && thisEntity instanceof BoatEntity
//                && thisEntity.getControllingPassenger() instanceof PlayerEntity) {
//            cir.setReturnValue(1.0F);
//        }
//    }

    @WrapOperation(
            method = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(
                    //#if MC>=11904
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getStepHeight()F"
                    //#else
                    //$$ value = "FIELD",
                    //$$ target = "stepHeight"
                    //#endif
            )
    )
    private float adjustMovementForCollisions(Entity instance, Operation<Float> original) {
        if (CarpetOrgAdditionSettings.climbingBoat
                && thisEntity instanceof BoatEntity
                && thisEntity.getControllingPassenger() instanceof PlayerEntity) {
            return 1.0F;
        }
        return original.call(instance);
    }
}
