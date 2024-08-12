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

package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.mixin.command.CommandNodeInvoker;


import java.util.function.Predicate;

public class CarpetIntegratedServerCommand {
    public static void onRegisterCarpetCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (CarpetOrgAddition.minecraftServer instanceof IntegratedServer) {
            CarpetOrgAddition.LOGGER.info("[ORG] Has changed permission requirement of /carpet for integrated server");
            Predicate<ServerCommandSource> carpetRequirement = dispatcher.getRoot().getChild("carpet").getRequirement();
            ((CommandNodeInvoker<ServerCommandSource>) dispatcher.getRoot().getChild("carpet")).setRequirement(carpetRequirement.or(source -> CarpetOrgAdditionSettings.openCarpetPermissions));
        }
    }
}
