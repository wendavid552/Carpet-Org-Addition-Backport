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
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

//可采集刷怪笼
@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BlockWithEntity {
    protected SpawnerBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "onStacksDropped", at = @At("HEAD"), cancellable = true)
    // 使用精准采集工具挖掘时不会掉落经验
    private void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.canMineSpawner && EnchantmentHelper.hasSilkTouch(tool)) {
            super.onStacksDropped(state, world, pos, tool, dropExperience);
            ci.cancel();
        }
    }

    @Override
    // 使用精准采集挖掘时掉落带NBT的物品
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (CarpetOrgAdditionSettings.canMineSpawner && !player.isCreative() && EnchantmentHelper.hasSilkTouch(player.getMainHandStack())) {
            if (world.getBlockEntity(pos) instanceof MobSpawnerBlockEntity) {
                MobSpawnerBlockEntity mobSpawnerBlock = (MobSpawnerBlockEntity) world.getBlockEntity(pos);
                ItemStack itemStack = new ItemStack(Items.SPAWNER);
                mobSpawnerBlock.setStackNbt(itemStack);
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
    }

    @Override
    // 放置刷怪笼时读取NBT
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) {
            return;
        }
        if (CarpetOrgAdditionSettings.canMineSpawner && placer instanceof PlayerEntity player && !player.isCreative()) {
            NbtCompound nbt;
            try {
                nbt = Objects.requireNonNull(itemStack.getNbt()).getCompound("BlockEntityTag");
            } catch (NullPointerException e) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                blockEntity.readNbt(nbt);
            }
        }
    }
}
