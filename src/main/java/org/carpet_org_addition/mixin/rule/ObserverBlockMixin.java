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
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
//#if MC>=12005
//$$ import net.minecraft.util.ItemActionResult;
//#endif
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin extends FacingBlock {
    @Shadow
    protected abstract void scheduleTick(WorldAccess world, BlockPos pos);

    private ObserverBlockMixin(Settings settings) {
        super(settings);
    }

    //可激活侦测器，打火石右键激活
    @SuppressWarnings("deprecation")
    @Override
    public
    //#if MC>=12005
    //$$ ItemActionResult onUseWithItem
    //#else
    ActionResult onUse
    //#endif
    (
            //#if MC>=12005
            //$$ ItemStack stack,
            //#endif
            BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (CarpetOrgAdditionSettings.canActivatesObserver) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.FLINT_AND_STEEL) && !player.isSneaking()) {
                this.scheduleTick(world, pos);
                itemStack.damage(1, player,
                        //#if MC>=12005
                        //$$ LivingEntity.getSlotForHand(hand)
                        //#else
                        player1 -> player1.sendToolBreakStatus(hand)
                        //#endif
                );
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, 1);
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                return
                        //#if MC>=12005
                        //$$ ItemActionResult
                        //#else
                        ActionResult
                        //#endif
                        .SUCCESS;
            }
        }
        return super.
                //#if MC>=12005
                //$$ onUseWithItem
                //#else
                onUse
                //#endif
                (
                //#if MC>=12005
                //$$ stack,
                //#endif
                state, world, pos, player, hand, hit);
    }
}
