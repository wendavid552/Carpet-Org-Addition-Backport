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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.TextUtils;

public abstract class AbstractCustomSizeInventory implements Inventory {
    /**
     * 用来占位的物品
     */
    public static final ItemStack PLACEHOLDER;

    static {
        ItemStack itemStack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        itemStack.setCustomName(TextUtils.setColor(TextUtils.getTranslate("carpet.inventory.item.placeholder"), Formatting.RED));
        PLACEHOLDER = itemStack;
    }

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(this.getSize() - this.getActualSize(), PLACEHOLDER);

    /**
     * @return 物品栏的大小
     */
    protected abstract int getSize();

    /**
     * @return 物品栏的实际大小，超出此大小的索引都是在GUI中用来占位的，没有实际用途
     */
    protected abstract int getActualSize();

    /**
     * @return 实际可用的物品栏
     */
    protected abstract Inventory getInventory();

    @Override
    public final int size() {
        return this.getSize();
    }

    @Override
    public final boolean isEmpty() {
        return this.getInventory().isEmpty();
    }

    @Override
    public final ItemStack getStack(int slot) {
        if (slot < this.getActualSize()) {
            return this.getInventory().getStack(slot);
        }
        return stacks.get(slot - this.getActualSize());
    }

    @Override
    public final ItemStack removeStack(int slot, int amount) {
        if (slot < this.getActualSize()) {
            return this.getInventory().removeStack(slot, amount);
        }
        ItemStack itemStack = Inventories.splitStack(this.stacks, getAmendSlotIndex(slot), amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public final ItemStack removeStack(int slot) {
        if (slot < this.getActualSize()) {
            return this.getInventory().removeStack(slot);
        }
        ItemStack itemStack = this.stacks.get(getAmendSlotIndex(slot));
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stacks.set(getAmendSlotIndex(slot), ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public final void setStack(int slot, ItemStack stack) {
        if (slot < this.getActualSize()) {
            this.getInventory().setStack(slot, stack);
            return;
        }
        this.stacks.set(getAmendSlotIndex(slot), stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public final void markDirty() {
        this.getInventory().markDirty();
    }

    @Override
    public final void clear() {
        // 只清空物品栏即可，不需要清空用来占位的stacks
        this.getInventory().clear();
    }

    @Override
    public final boolean isValid(int slot, ItemStack stack) {
        return slot < this.getActualSize();
    }

    // 丢弃多余的槽位中的物品
    public void dropExcess(PlayerEntity player) {
        for (ItemStack itemStack : stacks) {
            // 不丢弃占位用的物品
            if (itemStack == PLACEHOLDER) {
                continue;
            }
            player.dropItem(itemStack, false, false);
        }
    }

    private int getAmendSlotIndex(int slotIndex) {
        return slotIndex - this.getActualSize();
    }
}
