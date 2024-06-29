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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Unique
    private final MobEntity thisMob = (MobEntity) (Object) this;

    // 生物是否可以减轻物品
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;canPickUpLoot()Z"))
    private boolean canPickUpLoot(MobEntity instance, Operation<Boolean> original) {
//        return switch (CarpetOrgAdditionSettings.mobWhetherOrNotCanPickItem) {
//            case YES -> true;
//            case VANILLA -> original.call(instance);
//            case NO -> false;
//            case YES_ONLY_HOSTILE -> thisMob instanceof HostileEntity || original.call(instance);
//            case NO_ONLY_HOSTILE -> !(thisMob instanceof HostileEntity) && original.call(instance);
//        };
        switch(CarpetOrgAdditionSettings.mobWhetherOrNotCanPickItem) {
            case YES:
                return true;
            case VANILLA:
                return original.call(instance);
            case NO:
                return false;
            case YES_ONLY_HOSTILE:
                return thisMob instanceof HostileEntity || original.call(instance);
            case NO_ONLY_HOSTILE:
                return !(thisMob instanceof HostileEntity) && original.call(instance);
        }
        throw new IllegalStateException("Unexpected value: " + CarpetOrgAdditionSettings.mobWhetherOrNotCanPickItem);
    }
}
