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

package org.carpet_org_addition.mixin.command;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionInterface;
import org.carpet_org_addition.util.fakeplayer.FakePlayerActionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin implements FakePlayerActionInterface {
    @Unique
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;

    @Unique
    private final FakePlayerActionManager actionManager = new FakePlayerActionManager(thisPlayer);

    @Override
    public FakePlayerActionManager getActionManager() {
        return this.actionManager;
    }

    @Override
    public void copyActionManager(EntityPlayerMPFake oldPlayer) {
        this.actionManager.copyActionData(oldPlayer);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fakePlayerTick(CallbackInfo ci) {
        try {
            // 根据假玩家动作类型执行动作
            this.getActionManager().executeAction();
        } catch (RuntimeException e) {
            // 将错误信息写入日志
            CarpetOrgAddition.LOGGER.error("{}在执行操作“{}”时遇到意外错误:", thisPlayer.getName().getString(),
                    this.getActionManager().getAction().toString(), e);
            // 向聊天栏发送错误消息的反馈
            MutableText message = TextUtils.getTranslate("carpet.commands.playerAction.exception.runtime",
                    thisPlayer.getDisplayName(), this.getActionManager().getAction().getDisplayName());
            MessageUtils.broadcastTextMessage(thisPlayer, TextUtils.setColor(message, Formatting.RED));
            // 让假玩家停止当前操作
            this.getActionManager().stop();

        }
    }
}
