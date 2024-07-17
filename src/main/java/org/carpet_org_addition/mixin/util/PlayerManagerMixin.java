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

package org.carpet_org_addition.mixin.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#if MC<11904
//$$ import net.minecraft.network.MessageType;
//#endif
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAddition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
//    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
//    private void broadcast(Text message, boolean overlay, CallbackInfo ci) {
//        if (CarpetOrgAddition.hiddenLoginMessages) {
//            ci.cancel();
//        }
//    }
    @WrapOperation(
            method = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target =
                            //#if MC>=11904
                            "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
                            //#else
                            //$$ "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"
                            //#endif
            )
    )
    private void onPlayerConnect(PlayerManager instance,
                                 //#if MC>=11904
                                 Text message, boolean overlay,
                                 //#else
                                 //$$ Text message, MessageType type, UUID sender,
                                 //#endif
                                 Operation<Void> original) {
        if (CarpetOrgAddition.hiddenLoginMessages) {
            return;
        }
        //#if MC>=11904
        original.call(instance, message, overlay);
        //#else
        //$$ original.call(instance, message, type, sender);
        //#endif
    }
}
