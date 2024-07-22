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

package org.carpet_org_addition.mixin.compat.minecraft.PlayerEntityLastDeathPosition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.GlobalPos;
import org.carpet_org_addition.util.compat.minecraft.PlayerEntityLastDeathPosition.PlayerEntityLastDeathPositionRecorder;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityLastDeathPositionRecorder {
    @Unique
    private Optional<GlobalPos> lastDeathPos;

    @Unique
    private static final Logger LOGGER = null;

    @Override
    public Optional<GlobalPos> org$getLastDeathPos() {
        return lastDeathPos;
    }

    @Override
    public void org$setLastDeathPos(Optional<GlobalPos> pos) {
        lastDeathPos = pos;
    }

    //#if MC<11904
    //$$ @Inject(
    //$$         method = "onDeath",
    //$$         at = @At("TAIL")
    //$$ )
    //$$ private void setLastDeathPosOnDeath(CallbackInfo ci) {
    //$$     org$setLastDeathPos(Optional.of(GlobalPos.create(((EntityInvoker)this).getWorld().getRegistryKey(), ((EntityInvoker)this).getBlockPos())));
    //$$ }
    //$$ 
    //$$ @Inject(
    //$$         method = "readCustomDataFromNbt",
    //$$         at = @At("TAIL")
    //$$ )
    //$$ private void readLastDeathPositionFromNbt(NbtCompound nbt, CallbackInfo ci) {
    //$$     if (nbt.contains("LastDeathLocation", NbtElement.COMPOUND_TYPE)) {
    //$$         org$setLastDeathPos(GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("LastDeathLocation")).resultOrPartial(LOGGER::error));
    //$$     }
    //$$ }
    //$$ 
    //$$ @Inject(
    //$$         method="writeCustomDataToNbt",
    //$$         at=@At("TAIL")
    //$$ )
    //$$ private void writeLastDeathPositionToNbt(NbtCompound nbt, CallbackInfo ci) {
    //$$     org$getLastDeathPos()
	//$$ 		.flatMap(globalPos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).resultOrPartial(LOGGER::error))
	//$$ 		.ifPresent(nbtElement -> nbt.put("LastDeathLocation", nbtElement));
    //$$ }
    //#endif
}
