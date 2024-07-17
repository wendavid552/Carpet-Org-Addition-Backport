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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WetSpongeBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.rulevalue.WetSpongeImmediatelyDry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WetSpongeBlock.class)
public class WetSpongeBlockMixin {
    // 湿海绵立即干燥
    @Inject(method = "onBlockAdded", at = @At(value = "HEAD"), cancellable = true)
    private void dry(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        switch (CarpetOrgAdditionSettings.wetSpongeImmediatelyDry) {
            case DISABLE:
                break;
            case ARID:
                if (world.getBiome(pos).value().
                        //#if MC>=11904
                        hasPrecipitation()
                        //#else
                        //$$ getPrecipitation() != Biome.Precipitation.NONE
                        //#endif
                ) {
                    break;
                }
            case FALSE:
                if (!world.getDimension().ultrawarm()
                        && CarpetOrgAdditionSettings.wetSpongeImmediatelyDry != WetSpongeImmediatelyDry.ARID) {
                    break;
                }
            case ALL:
                world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(WorldEvents.WET_SPONGE_DRIES_OUT, pos, 0);
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                        1.0f, (1.0f + world.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
        ci.cancel();
    }
}
