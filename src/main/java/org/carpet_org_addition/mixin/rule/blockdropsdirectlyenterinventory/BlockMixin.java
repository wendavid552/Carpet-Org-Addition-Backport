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

package org.carpet_org_addition.mixin.rule.blockdropsdirectlyenterinventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {
    // 方块被采集后直接进入物品栏
    @WrapOperation(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"))
    private void drops(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, Operation<Void> original) {
        if (CarpetOrgAdditionSettings.blockDropsDirectlyEnterInventory) {
            if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity) {
                ServerWorld serverWorld = (ServerWorld) world;
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                List<ItemStack> list = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, entity, tool);
                if (list == null) {
                    original.call(state, world, pos, blockEntity, entity, tool);
                    return;
                }
                for (ItemStack itemStack : list) {
                    // 将物品直接放入物品栏
                    if (player.getInventory().insertStack(itemStack)) {
                        continue;
                    }
                    // 将部分物品放入物品栏后，剩下的物品直接掉落
                    Block.dropStack(world, pos, itemStack);
                }
                state.onStacksDropped(serverWorld, pos, tool
                //#if MC>=11904
                        ,true
                //#endif
                );
            }
        } else {
            original.call(state, world, pos, blockEntity, entity, tool);
        }
    }
}
