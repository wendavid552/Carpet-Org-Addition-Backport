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

package org.carpet_org_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
//#if MC>=11904
import net.minecraft.command.CommandRegistryAccess;
//#endif

import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carpet_org_addition.command.RegisterCarpetCommands;
import org.carpet_org_addition.logger.WanderingTraderSpawnLogger;
import org.carpet_org_addition.settings.CarpetRuleRegistrar;
import org.carpet_org_addition.translate.Translate;
import org.carpet_org_addition.util.wheel.Waypoint;

import java.util.Map;

public class CarpetOrgAddition implements ModInitializer, CarpetExtension {
    /**
     * 控制玩家登录登出的消息是否显示
     */
    public static boolean hiddenLoginMessages = false;
    // 日志
    public static final Logger LOGGER = LogManager.getLogger("CarpetOrgAddition");
    public static final String MOD_NAME_LOWER_CASE = "carpetorgaddition";
    public static MinecraftServer minecraftServer;

    /**
     * 模组初始化
     */
    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new CarpetOrgAddition());
    }

    // 在游戏开始时
    @Override
    public void onGameStarted() {
        // 解析Carpet设置
        CarpetRuleRegistrar.register(CarpetServer.settingsManager, CarpetOrgAdditionSettings.class);
    }

    // 当玩家登录时
    @Override
    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        CarpetExtension.super.onPlayerLoggedIn(player);
        // 假玩家生成时不保留上一次的击退，着火时间，摔落高度
        if (CarpetOrgAdditionSettings.fakePlayerSpawnNoKnockback && player instanceof EntityPlayerMPFake) {
            // 清除速度
            player.setVelocity(Vec3d.ZERO);
            // 清除着火时间
            player.setFireTicks(0);
            // 清除摔落高度
            player.fallDistance = 0;
            // 清除负面效果
            player.getStatusEffects().removeIf(effect -> effect.getEffectType()
                    //#if MC>=12005
                    //$$ .value()
                    //#endif
                    .getCategory() == StatusEffectCategory.HARMFUL);
        }
    }

    // 服务器启动时调用
    @Override
    public void onServerLoaded(MinecraftServer server) {
        CarpetExtension.super.onServerLoaded(server);
        // 服务器启动时自动将旧的路径点替换成新的
        Waypoint.replaceWaypoint(server);
        minecraftServer = server;
    }

    // 设置模组翻译
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translate.getTranslate();
    }

    // 注册记录器
    @Override
    public void registerLoggers() {
        CarpetExtension.super.registerLoggers();
        WanderingTraderSpawnLogger.registerLoggers();
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher
                //#if MC>=11904
                ,final CommandRegistryAccess commandBuildContext
                //#endif
    ) {
        // 注册Carpet命令
        RegisterCarpetCommands.registerCarpetCommands(dispatcher
                //#if MC>=11904
                ,commandBuildContext
                //#endif
        );
    }
}
