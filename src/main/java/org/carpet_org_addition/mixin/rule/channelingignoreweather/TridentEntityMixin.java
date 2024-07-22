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

package org.carpet_org_addition.mixin.rule.channelingignoreweather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//强化引雷
@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    private TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world
                               //#if MC>=12003
                               //$$ ,ItemStack stack
                               //#endif
    ) {
        super(entityType, world
        //#if MC>=12003
        //$$ ,stack
        //#endif
        );
    }

    //#if MC<12100
    @WrapOperation(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    //击中实体时产生闪电
    private boolean isThundering(World world, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.channelingIgnoreWeather) {
            return true;
        }
        return original.call(world);
    }
    //#else
    //$$ @Shadow
    //$$ public abstract ItemStack getWeaponStack();
    //$$
    //$$ // 击中实体
    //$$ @WrapOperation(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;)V"))
    //$$ private void onEnhityHit(ServerWorld world, Entity target, DamageSource damageSource, ItemStack weapon, Operation<Void> original) {
    //$$     original.call(world, target, damageSource, weapon);
    //$$     spwnLighining(world, target.getBlockPos());
    //$$ }
    //$$
    //$$ // 击中避雷针
    //$$ @Inject(method = "onBlockHitEnchantmentEffects", at = @At(value = "TAIL"))
    //$$ private void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack, CallbackInfo ci) {
    //$$     BlockPos blockPos = blockHitResult.getBlockPos();
    //$$     if (world.getBlockState(blockPos).isOf(Blocks.LIGHTNING_ROD)) {
    //$$         spwnLighining(world, blockPos.up());
    //$$     }
    //$$ }
    //$$
    //$$ // 生成闪电
    //$$ @Unique
    //$$ private void spwnLighining(ServerWorld world, BlockPos blockPos) {
    //$$     // 只需要在晴天生成，因为雷雨天的引雷三叉戟本来就会生成闪电
    //$$     if (world.isRaining() && world.isThundering()) {
    //$$         return;
    //$$     }
    //$$     boolean hasChanneling = EnchantmentUtils.hasEnchantment(world, Enchantments.CHANNELING, this.getWeaponStack());
    //$$     if (CarpetOrgAdditionSettings.channelingIgnoreWeather && World.isValid(blockPos) && hasChanneling) {
    //$$         LightningEntity lightning = EntityType.LIGHTNING_BOLT.spawn(world, blockPos, SpawnReason.TRIGGERED);
    //$$         if (lightning == null) {
    //$$             return;
    //$$         }
    //$$         if (this.getOwner() instanceof ServerPlayerEntity player) {
    //$$             lightning.setChanneler(player);
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
