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

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(net.minecraft.client.render.GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private final GameRenderer thisGameRenderer = (GameRenderer) (Object) this;

    //最大方块交互距离适用于实体
    //TODO: 改成WarpOperation
    //#if MC>=12005
    //$$ @WrapOperation(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockInteractionRange()D"))
    //$$ private double getPlayerBlockInteractionRange(ClientPlayerEntity clientPlayerEntity, Operation<Double> original) {
    //$$     return clientPlayerEntity.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
    //$$ }
    //#else
    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    private void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.maxBlockPlaceDistanceReferToEntity) {
            try {
                Entity entity2 = thisGameRenderer.getClient().getCameraEntity();
                if (entity2 == null) {
                    return;
                }
                if (thisGameRenderer.getClient().world == null) {
                    return;
                }
                thisGameRenderer.getClient().getProfiler().push("pick");
                thisGameRenderer.getClient().targetedEntity = null;
                //*
                double d = Objects.requireNonNull(thisGameRenderer.getClient().interactionManager).getReachDistance();
                thisGameRenderer.getClient().crosshairTarget = entity2.raycast(d, tickDelta, false);
                Vec3d vec3d = entity2.getCameraPosVec(tickDelta);
                boolean flag = false;

                //*
                if (thisGameRenderer.getClient().interactionManager.hasExtendedReach()) {
                    d  = 6.0;
                } else {
                    // 服务器最大交互距离
                    if (d > MathUtils.getPlayerMaxInteractionDistance()) {
                        flag = true;
                    }
                }
                double e = d;
                e *= e; //e = 最大交互距离^2
                if (thisGameRenderer.getClient().crosshairTarget != null) {
                    e = thisGameRenderer.getClient().crosshairTarget.getPos().squaredDistanceTo(vec3d);
                }
                Vec3d vec3d2 = entity2.getRotationVec(1.0f);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                Box box = entity2.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
                if (entityHitResult != null) {
                    Entity entity22 = entityHitResult.getEntity();
                    Vec3d vec3d4 = entityHitResult.getPos();
                    double g = vec3d.squaredDistanceTo(vec3d4);
                    // 服务器最大交互平方距离
                    if (flag && g > MathUtils.getMaxBreakSquaredDistance()) {
                        thisGameRenderer.getClient().crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z),
                                //#if MC>=11904
                                BlockPos.ofFloored(vec3d4)
                                //#else
                                //$$ new BlockPos(vec3d4)
                                //#endif
                        );
                    } else if (g < e || thisGameRenderer.getClient().crosshairTarget == null) {
                        thisGameRenderer.getClient().crosshairTarget = entityHitResult;
                        if (entity22 instanceof LivingEntity || entity22 instanceof ItemFrameEntity) {
                            thisGameRenderer.getClient().targetedEntity = entity22;
                        }
                    }
                }
                thisGameRenderer.getClient().getProfiler().pop();
            } finally {
                ci.cancel();
            }
        }
    }
    //#endif
}