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
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * 此物品匹配器尝试匹配物品时，只检查目标物品堆栈对应的物品是否与该物品匹配器内包含的物品相同，不考虑物品的NBT
 */
public class ItemMatcher implements Matcher {
    public static final ItemMatcher AIR_ITEM_MATCHER = new ItemMatcher(Items.AIR);
    private final Item item;

    public ItemMatcher(Item item) {
        this.item = item;
    }

    public ItemMatcher(ItemStack itemStack) {
        this.item = itemStack.getItem();
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.isOf(item);
    }

    @Override
    public boolean isEmpty() {
        return this.item == Items.AIR;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public Text getName() {
        return this.item.getName();
    }

    @Override
    public ItemStack getDefaultStack() {
        return this.item.getDefaultStack();
    }

    @Override
    public MutableText toText() {
        return this.item.getDefaultStack().toHoverableText().copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ItemMatcher itemMatcher) {
            return this.item == itemMatcher.item;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.item.hashCode();
    }

    @Override
    public String toString() {
        return Registries.ITEM.getId(this.item).toString();
    }
}
