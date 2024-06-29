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

package org.carpet_org_addition.util.fakeplayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

// 假玩家末影箱GUI
public class PlayerEnderChestScreenHandler extends GenericContainerScreenHandler {
    /**
     * 不一定是假玩家，也有可能是/playerTools命令的执行者自己
     */
    private final ServerPlayerEntity playerEntity;

    public PlayerEnderChestScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity playerEntity) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, playerEntity.getEnderChestInventory(), 3);
        this.playerEntity = playerEntity;
    }

    //假玩家死亡时，自动关闭GUI
    @Override
    public boolean canUse(PlayerEntity player) {
        if (playerEntity == null) {
            return false;
        }
        return !playerEntity.isRemoved();
    }
}
