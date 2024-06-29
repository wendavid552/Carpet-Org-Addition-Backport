/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.carpet_org_addition.util;

import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAddition;

import static carpet.utils.Messenger.s;

import net.minecraft.server.command.ServerCommandSource;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class MixinUtil {
    public static boolean audit(@Nullable ServerCommandSource source) {
        boolean ok;
        MutableText response;
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            response = (MutableText) s("Mixin environment audited successfully");
            ok = true;
        } catch (Exception e) {
            CarpetOrgAddition.LOGGER.error("Error when auditing mixin", e);
            response = (MutableText) s(String.format("Mixin environment auditing failed, check console for more information (%s)", e));
            ok = false;
        }
        if (source != null) {
            MutableText finalResponse = response;
            //#if MC>=12000
            source.sendFeedback(() -> finalResponse, false);
            //#else
            //$$ source.sendFeedback(finalResponse, false);
            //#endif
        }
        return ok;
    }
}