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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
//#if MC>11900
import net.minecraft.registry.tag.DamageTypeTags;
//#endif
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    //创造玩家免疫/kill
    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void kill(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.creativeImmuneKill) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            if (livingEntity instanceof PlayerEntity) {
                if (((PlayerEntity)livingEntity).isCreative()) {
                    ci.cancel();
                }
            }
        }
    }

    //禁用伤害免疫
    @WrapOperation(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I", opcode = Opcodes.GETFIELD))
    private int setTimeUntilRegen(LivingEntity instance, Operation<Integer> original) {
        if (CarpetOrgAdditionSettings.disableDamageImmunity) {
            return 0;
        }
        return original.call(instance);
    }

    // 增强不死图腾
    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        // 在一开始就对规则是否开启进行判断，这样当其他Mod也修改了此段代码时，就可以通过关闭改规则来保障其他Mod的正常运行
        if (CarpetOrgAdditionSettings.betterTotemOfUndying) {
            LivingEntity thisLivingEntity = (LivingEntity) (Object) this;
            if (source.
                    //#if MC>11900
                    isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    //#else
                    //$$ isOutOfWorld()
                    //#endif
            ) {
                cir.setReturnValue(false);
                return;
            }
            ItemStack itemStack = null;
            for (Hand hand : Hand.values()) {
                ItemStack itemStack2 = thisLivingEntity.getStackInHand(hand);
                if (!itemStack2.isOf(Items.TOTEM_OF_UNDYING)) continue;
                itemStack = itemStack2.copy();
                itemStack2.decrement(1);
                break;
            }
            // 从玩家物品栏寻找不死图腾
            if (itemStack == null && thisLivingEntity instanceof PlayerEntity playerEntity) {
                DefaultedList<ItemStack> mainInventory = playerEntity.getInventory().main;
                for (ItemStack totemOfUndying : mainInventory) {
                    if (totemOfUndying.isOf(Items.TOTEM_OF_UNDYING)) {
                        itemStack = totemOfUndying.copy();
                        totemOfUndying.decrement(1);
                        break;
                    }
                }
            }
            if (itemStack != null) {
                if (thisLivingEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                    Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
                }
                thisLivingEntity.setHealth(1.0f);
                thisLivingEntity.clearStatusEffects();
                thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                thisLivingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                thisLivingEntity.getWorld().sendEntityStatus(thisLivingEntity, EntityStatuses.USE_TOTEM_OF_UNDYING);
            }
            cir.setReturnValue(itemStack != null);
        }
    }
}