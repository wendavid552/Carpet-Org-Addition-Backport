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
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.CraftPresets;

import java.io.File;

@SuppressWarnings("unused")
public class PresetsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("presets")
                .then(CommandManager.literal("craft")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .then(CommandManager.argument("presets", StringArgumentType.string())
                                                .executes(PresetsCommand::addCraftPresets))))
                        .then(CommandManager.literal("list").executes(PresetsCommand::listCraftPreset))));
    }

    private static int addCraftPresets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取命令中设定的文件名
        String name = StringArgumentType.getString(context, "name");
        // 获取命令中设定的配方
        String presets = StringArgumentType.getString(context, "presets");
        // 创建一个合成预设对象
        CraftPresets craftPresets = new CraftPresets(name, presets);
        // 将合成预设对象写入本地文件
        craftPresets.saveCraftRecipe(context.getSource().getServer());
        return 1;
    }

    // 列出所有合成预设
    private static int listCraftPreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        File file = CraftPresets.getFile(context.getSource().getServer());
        File[] files = file.listFiles();
        int i = 0;
        if (files != null) {
            for (; i < files.length; i++) {
                // 只列出json文件
                if (!files[i].getName().endsWith(".json")) {
                    continue;
                }
                String fileName = CraftPresets.extractFileName(files[i].getName());
                // 列出目录下的每一个文件
                MessageUtils.sendTextMessage(context.getSource(), TextUtils.literal(fileName));
            }
        }
        return i;
    }
}