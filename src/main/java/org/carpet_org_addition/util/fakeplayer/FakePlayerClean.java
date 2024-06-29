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

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.carpet_org_addition.util.fakeplayer.actiondata.CleanData;

public class FakePlayerClean {
    private FakePlayerClean() {
    }

    public static void clean(CleanData cleanData, EntityPlayerMPFake fakePlayer) {
        Item item = cleanData.isAllItem() ? null : cleanData.getItem();
        //判断假玩家打开的界面是不是潜影盒的GUI
        if (fakePlayer.currentScreenHandler instanceof ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
            // 使用循环一次丢弃一组丢出潜影盒中的物品
            for (int index = 0; index < 27; index++) {
                ItemStack itemStack = shulkerBoxScreenHandler.getSlot(index).getStack();
                if (itemStack.isEmpty()) {
                    continue;
                }
                if (cleanData.isAllItem() || itemStack.isOf(item)) {
                    // 丢弃一组物品
                    FakePlayerUtils.throwItem(shulkerBoxScreenHandler, index, fakePlayer);
                }
            }
            // 物品全部丢出后自动关闭潜影盒
            fakePlayer.closeHandledScreen();
        }
    }
}
