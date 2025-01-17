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

package org.carpet_org_addition.util.wheel;

//#if MC>=12005
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.ContainerComponent;
//#endif
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShulkerBoxInventory implements Inventory {

    private final DefaultedList<ItemStack> stacks;
    private final List<ItemStack> shulkerBoxList;

    public ShulkerBoxInventory(List<ItemStack> shulkerBoxList) {
        this.shulkerBoxList = new ArrayList<>();
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack shulkerbox : shulkerBoxList) {
            if (InventoryUtils.isNonEmptyShulkerBox(shulkerbox)) {
                this.shulkerBoxList.add(shulkerbox);
                list.addAll(InventoryUtils.getInventoryList(shulkerbox));
            }
        }
        this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, list.toArray(value -> new ItemStack[0]));
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.stacks) {
            if (itemStack.isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.stacks, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    /**
     * 为潜影盒内的物品整理并排序，当前物品栏为多个潜影盒时，物品可以跨潜影盒整理排序
     */
    public void sort() {
        InventoryUtils.sortInventory(this.stacks);
    }

    /**
     * 清除空潜影盒的物品栏NBT
     */
    public void removeInventoryNbt() {
        //#if MC<12005
        for (ItemStack itemStack : this.shulkerBoxList) {
            if (InventoryUtils.isEmptyShulkerBox(itemStack)) {
                itemStack.removeSubNbt(InventoryUtils.BLOCK_ENTITY_TAG);
            }
        }
        //#endif
    }

    /**
     * 应用对潜影盒的更改：将物品集合写入NBT
     */
    public void application() {
        int number = 0;
        for (ItemStack itemStack : this.shulkerBoxList) {
            /**
             * 对每个潜影盒进行操作，将this.stacks中每27个物品视作一个潜影盒的物品栏
             */

            DefaultedList<ItemStack> defaultedList;
            if (number < this.size()) {
                List<ItemStack> list = this.stacks.subList(number, (number + 27) > this.size() ? this.size() : (number + 27));
                number += 27;
                ItemStack[] arr = list.toArray(new ItemStack[0]);
                defaultedList = DefaultedList.copyOf(ItemStack.EMPTY, arr);
            } else {
                defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
            }
            //#if MC<12005
            itemStack.setSubNbt(InventoryUtils.BLOCK_ENTITY_TAG, Inventories.writeNbt(new NbtCompound(), defaultedList));
            //#else
            //$$ itemStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(defaultedList));
            //#endif
        }
    }
}
