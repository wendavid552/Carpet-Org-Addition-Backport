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

package org.carpet_org_addition.util.matcher;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * 不同与{@link ItemMatcher}，此匹配器匹配的物品堆栈除了要求物品要相同，物品的NBT也要相同，如果物品堆栈没有达到最大堆叠数，则这两个物品可以合并
 */
@SuppressWarnings({"unused", "ClassCanBeRecord"})
public class ItemStackMatcher implements Matcher {
    public final ItemStack itemStack;

    public ItemStackMatcher(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ItemStack.canCombine(this.itemStack, itemStack);
    }

    @Override
    public boolean isEmpty() {
        return this.itemStack.isEmpty();
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public Item getItem() {
        return this.itemStack.getItem();
    }

    @Override
    public Text getName() {
        return this.getItem().getName();
    }

    @Override
    public ItemStack getDefaultStack() {
        return this.itemStack;
    }

    @Override
    public MutableText toText() {
        return this.itemStack.toHoverableText().copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ItemStackMatcher itemStackMatcher) {
            return ItemStack.canCombine(this.itemStack, itemStackMatcher.itemStack);
        }
        return false;
    }

    @Override
    public int hashCode() {
        NbtCompound nbt = this.itemStack.getNbt();
        return this.itemStack.getItem().hashCode() + (nbt == null || nbt.isEmpty() ? 0 : nbt.hashCode());
    }

    @Override
    public String toString() {
        return Registries.ITEM.getId(this.getItem()).toString();
    }
}
