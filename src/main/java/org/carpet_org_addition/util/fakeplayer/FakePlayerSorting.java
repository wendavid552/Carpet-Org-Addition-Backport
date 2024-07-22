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
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.exception.NoNbtException;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.fakeplayer.actiondata.SortingData;

public class FakePlayerSorting {
    private FakePlayerSorting() {
    }

    public static void sorting(SortingData sortingData, EntityPlayerMPFake fakePlayer) {
        //获取要分拣的物品对象
        Item item = sortingData.getItem();
        //获取分拣物品要丢出的方向
        Vec3d thisVec = sortingData.getThisVec();
        //获取非分拣物品要丢出的方向
        Vec3d otherVec = sortingData.getOtherVec();
        //获取玩家物品栏对象
        PlayerInventory inventory = fakePlayer.getInventory();
        //遍历玩家物品栏，找到要丢出的物品
        for (int index = 0; index < inventory.size(); index++) {
            //定义变量记录当前槽位的物品堆栈对象
            ItemStack itemStack = inventory.getStack(index);
            if (itemStack.isEmpty()) {
                continue;
            }
            //如果是要分拣的物品，就转向一边，否则转身向另一边
            if (itemStack.getItem() == item) {
                fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, thisVec);
            } else {
                //丢弃潜影盒内的物品
                //判断当前物品是不是潜影盒
                if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                    InfiniteLoopException exception = new InfiniteLoopException(100);
                    itemStack = pickItemFromShulkerBox(fakePlayer, exception, inventory, index, otherVec, item, thisVec);
                } else {
                    //设置当前朝向为丢出非指定物品朝向
                    fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                }
            }
            //丢弃该物品堆栈
            FakePlayerUtils.dropItem(fakePlayer, itemStack);
        }
    }

    // 从潜影盒中拿取并分拣物品
    private static ItemStack pickItemFromShulkerBox(EntityPlayerMPFake fakePlayer, InfiniteLoopException exception,
                                                    PlayerInventory inventory, int index, Vec3d otherVec, Item item, Vec3d thisVec) {
        ItemStack itemStack;
        while (true) {
            exception.checkLoopCount();
            // 一轮循环结束后，再重新将当前物品设置为物品栏中的潜影盒
            itemStack = inventory.getStack(index);
            //判断潜影盒是否为空
            if (InventoryUtils.isNonEmptyShulkerBox(itemStack)) {
                // 获取潜影盒内第一个非空气物品，获取后，该物品会在潜影盒内删除
                // 设置当前物品为潜影盒内容物的第一个非空物品
                if(InventoryUtils.isShulkerBoxItem(itemStack)) {
                    itemStack = InventoryUtils.getShulkerBoxItem(itemStack);
                }
                else {
                    // 空潜影盒异常，潜影盒可能没有NBT，如直接从创造模式物品栏中拿取出的潜影盒
                    // 丢出这个异常的潜影盒
                    itemStack = inventory.getStack(index);
                    //设置当前朝向为丢出非指定物品朝向
                    fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                    break;
                }
                // 根据当前物品设置朝向
                fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,
                        itemStack.getItem() == item ? thisVec : otherVec);
            }
            else {
                // 如果为空，将朝向设置为丢出非指定物品的方向，然后结束循环
                // 设置当前朝向为丢出非指定物品朝向
                fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                break;
            }
            // 丢弃潜影盒内物品堆栈
            FakePlayerUtils.dropItem(fakePlayer, itemStack);
        }
        return itemStack;
    }
}
