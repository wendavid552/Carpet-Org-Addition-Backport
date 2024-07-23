package org.carpet_org_addition.mixin.rule.playerinteraction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.carpet_org_addition.util.MathUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private final PlayerEntity thisPlayer = (PlayerEntity) (Object) this;

    //#if MC>=12005
    //$$ @Inject(
    //$$         method="Lnet/minecraft/entity/player/PlayerEntity;canInteractWithBlockAt(Lnet/minecraft/util/math/BlockPos;D)Z",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private boolean onCanInteractWithBlockAt(BlockPos pos, double distance, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (MathUtils.isDefaultDistance()) {
    //$$         return cir.getReturnValue();
    //$$     }
    //$$     double d = MathUtils.getMaxBreakSquaredDistance();
    //$$     return (new Box(pos)).squaredMagnitude(thisPlayer.getEyePos()) < d * d;
    //$$ }
    //#endif
}
