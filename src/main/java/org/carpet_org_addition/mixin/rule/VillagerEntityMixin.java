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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.villagerinventory.VillagerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 村民立即补货
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    @Shadow
    public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    @Unique
    private final VillagerEntity thisVillager = (VillagerEntity) (Object) this;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    // 阻止村民变成女巫
    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    private void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.disableVillagerWitch) {
            super.onStruckByLightning(world, lightning);
            ci.cancel();
        }
    }

    // 打开村民物品栏
    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void clearVillagerInventory(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (CarpetOrgAdditionSettings.openVillagerInventory && player.isSneaking()) {
            SimpleNamedScreenHandlerFactory screen =
                    new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity)
                            -> new VillagerScreenHandler(i, inventory, thisVillager), thisVillager.getName());
            player.openHandledScreen(screen);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    // 村民回血
    @Inject(method = "mobTick", at = @At("HEAD"))
    private void heal(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.villagerHeal) {
            long worldTime = thisVillager.getWorld().getTime();
            // 每四秒回一次血
            if (worldTime % 80 == 0) {
                thisVillager.heal(1.0F);
            }
        }
    }
}
