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

package org.carpet_org_addition.mixin.client.carpet;

import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.mixin.command.CommandNodeInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC>=11904
import net.minecraft.command.CommandRegistryAccess;
//#endif


import java.util.function.Predicate;

@Mixin(value = CarpetServer.class,remap = false)
public class CarpetServerMixin {
    @Inject(
            //#if MC>=11904
            method = "registerCarpetCommands",
            //#else
            //$$ method = "Lcarpet/CarpetServer;registerCarpetCommands(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V",
            //#endif
            at = @At("TAIL")
    )
    private static void onRegisterCarpetCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment
            //#if MC>=11904
            ,CommandRegistryAccess commandBuildContext
            //#endif
            ,CallbackInfo ci) {
        if (CarpetOrgAddition.minecraftServer instanceof IntegratedServer) {
            CarpetOrgAddition.LOGGER.info("[ORG] Has changed permission requirement of /carpet for integrated server");
            Predicate<ServerCommandSource> carpetRequirement = dispatcher.getRoot().getChild("carpet").getRequirement();
            ((CommandNodeInvoker<ServerCommandSource>) dispatcher.getRoot().getChild("carpet")).setRequirement(carpetRequirement.or(source -> CarpetOrgAdditionSettings.openCarpetPermissions));
        }
    }
}
