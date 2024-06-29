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

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * 不可变的物品栏，一旦创建，里面的内容都是不可以改变的，只能进行查询操作，否则抛出{@link UnsupportedOperationException}
 */
public final class ImmutableInventory extends SimpleInventory implements Iterable<ItemStack> {
    /**
     * 当前物品栏是否已锁定，锁定后，物品栏不能改变
     */
    private boolean lock = false;

    public ImmutableInventory(DefaultedList<ItemStack> list) {
        super(list.size());
        for (int i = 0; i < list.size(); i++) {
            // 不能用super.setStack(i, list.get(i))，编译器会自动把super设置为this
            this.setStack(i, list.get(i));
        }
        this.lock = true;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeStack(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (this.lock) {
            throw new UnsupportedOperationException();
        } else {
            super.setStack(slot, stack);
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ItemStack> clearToList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeItem(Item item, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack addStack(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    /**
     * 潜影盒内的物品共占了多少个槽位，不是指潜影盒内物品的总数
     */
    public int slotCount() {
        int count = 0;
        for (int index = 0; index < this.size(); index++) {
            if (!this.getStack(index).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        StringJoiner inventory = new StringJoiner(", ", "{", "}");
        for (int index = 0; index < this.size(); index++) {
            ItemStack itemStack = this.getStack(index);
            if (itemStack.isEmpty()) {
                continue;
            }
            inventory.add(itemStack.getItem().toString() + "*" + itemStack.getCount());
        }
        return inventory.toString();
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            // 要返回的下一个元素的索引
            private int cursor = 0;

            // 迭代器的大小
            private final int size = ImmutableInventory.this.size();

            @Override
            public boolean hasNext() {
                return this.cursor < this.size;
            }

            @Override
            public ItemStack next() {
                // 由于对象不可变，所以是线程安全的，不需要考虑并发修改的问题
                ItemStack itemStack = ImmutableInventory.this.getStack(cursor);
                this.cursor++;
                return itemStack;
            }
        };
    }
}
