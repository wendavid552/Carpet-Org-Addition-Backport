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
import net.minecraft.screen.slot.Slot;
import org.carpet_org_addition.util.fakeplayer.actiondata.FillData;

public class FakePlayerFill {
    private FakePlayerFill() {
    }

    public static void fill(FillData fillData, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof ShulkerBoxScreenHandler shulkerBoxScreenHandler) {
            boolean allItem = fillData.isAllItem();
            // 获取要装在潜影盒的物品
            Item item = allItem ? null : fillData.getItem();
            // 只遍历玩家物品栏，不遍历潜影盒容器
            // 前27个格子是潜影盒的槽位
            for (int index = 63 - 36; index < 63; index++) {  // 63-36=27
                // 获取玩家物品栏槽位内每一个槽位对象
                Slot slot = shulkerBoxScreenHandler.slots.get(index);
                // 检查槽位内是否有物品
                if (slot.hasStack()) {
                    ItemStack itemStack = slot.getStack();
                    if ((allItem && itemStack.getItem().canBeNested()) || itemStack.isOf(item)) {
                        // 相当于按住Shift键移动物品
                        FakePlayerUtils.quickMove(shulkerBoxScreenHandler, index, fakePlayer);
                        // 继续判断槽位内是否有物品，如果有说明潜影盒已满，关闭潜影盒，然后直接结束方法
                        if (slot.hasStack()) {
                            // 填充完潜影盒后自动关闭潜影盒
                            fakePlayer.onHandledScreenClosed();
                            return;
                        }
                    } else {
                        //丢弃玩家物品栏中与指定物品不符的物品
                        FakePlayerUtils.throwItem(shulkerBoxScreenHandler, index, fakePlayer);
                    }
                }
            }
        }
    }
}
