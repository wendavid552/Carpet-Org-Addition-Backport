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

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
//#if MC>11900
import carpet.api.settings.RuleHelper;
//#endif

import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.mixin.rule.carpet.SettingsManagerAccessor;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.TextUtils;

import java.util.List;

public class RuleSearchCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ruleSearch")
                .requires(source -> CommandUtils.canUseCommand(source, CarpetOrgAdditionSettings.commandRuleSearch))
                .then(CommandManager.argument("rule", StringArgumentType.string())
                        .executes(RuleSearchCommand::listRule)));
    }

    // 列出符合条件的规则
    private static int listRule(CommandContext<ServerCommandSource> context) {
        String rule = StringArgumentType.getString(context, "rule");
        if (CarpetServer.settingsManager == null) {
            return 0;
        }
        List<CarpetRule<?>> list = CarpetServer.settingsManager.getCarpetRules().stream().toList();
        MutableText text = TextUtils.getTranslate("carpet.commands.ruleSearch.feedback", rule);
        // 将文本设置为粗体
        text.styled(style -> style.withBold(true));
        //#if MC>=12000
        context.getSource().sendFeedback(() -> text, false);
        //#else
        //$$ context.getSource().sendFeedback(text, false);
        //#endif
        int ruleCount = 0;
        for (CarpetRule<?> carpet : list) {
            String translatedName =
                    //#if MC>11900
                    RuleHelper.translatedName(carpet);
                    //#else
                    //$$ carpet.translatedName();
                    //#endif
            if (translatedName.contains(rule)) {
                Messenger.m(context.getSource(),
                        ((SettingsManagerAccessor) CarpetServer.settingsManager).displayInteractiveSettings(carpet));
                ruleCount++;
            }
        }
        return ruleCount;
    }
}
