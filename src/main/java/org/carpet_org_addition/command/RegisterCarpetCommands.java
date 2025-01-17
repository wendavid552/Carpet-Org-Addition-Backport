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
import org.carpet_org_addition.util.CommandNodeFactory;
//#if MC>11900
import net.minecraft.command.CommandRegistryAccess;
//#endif

public class RegisterCarpetCommands {
    //注册Carpet命令
    public static void registerCarpetCommands(CommandDispatcher<ServerCommandSource> dispatcher
                                                //#if MC>11900
                                                ,CommandRegistryAccess commandBuildContext
                                                //#endif
                                                ) {
        CommandNodeFactory commandNodeFactory = new CommandNodeFactory(
                //#if MC>=11904
                commandBuildContext
                //#else
                //$$ null
                //#endif
        );
        CarpetIntegratedServerCommand.onRegisterCarpetCommands(dispatcher);

        //物品分身命令
        ItemShadowingCommand.register(dispatcher);

        // 保护假玩家命令
        // ProtectCommand.register(dispatcher);

        //假玩家工具命令
        PlayerToolsCommand.register(dispatcher);

        //发送消息命令
        SendMessageCommand.register(dispatcher, commandNodeFactory);

        //苦力怕音效命令
        CreeperCommand.register(dispatcher);

        //经验转移命令
        XpTransferCommand.register(dispatcher);

        //生存旁观切换命令
        SpectatorCommand.register(dispatcher);

        //查找器命令
        FinderCommand.register(dispatcher, commandNodeFactory);

        //自杀命令
        KillMeCommand.register(dispatcher);

        //路径点管理器命令
        LocationsCommand.register(dispatcher);

        // 绘制粒子线命令
        ParticleLineCommand.register(dispatcher);

        // 假玩家动作命令
        PlayerActionCommand.register(dispatcher, commandNodeFactory);

        // 预设管理器命令
        // PresetsCommand.register(dispatcher);

        // 规则搜索命令
        RuleSearchCommand.register(dispatcher);

        // 玩家管理器命令
        PlayerManagerCommand.register(dispatcher);

        // 追踪器命令
        NavigatorCommand.register(dispatcher);

        // 物品栏命令
        // InventoryCommand.register(dispatcher);

        // 假玩家重复上下线
        // ReloginCommand.register(dispatcher);

        /*
          测试用命令
         */
        // CarpetOrgAdditionTestCommand.register(dispatcher, commandBuildContext);
    }
}
