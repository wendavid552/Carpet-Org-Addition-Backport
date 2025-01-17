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
import org.carpet_org_addition.util.TextUtils;

import java.util.List;
import java.util.Objects;

public class ItemTagMatcher implements Matcher {
    private final String tag;

    public ItemTagMatcher(String tag) {
        boolean hasSymbol = tag.startsWith("#");
        if (hasSymbol && tag.contains(":")) {
            this.tag = tag.substring(1);
            return;
        }
        this.tag = "minecraft:" + (hasSymbol ? tag.substring(1) : tag);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        //TODO: 等待重构
        //#if MC>=11800
        List<String> list = itemStack.streamTags().map(tag -> tag.id().toString()).toList();
        for (String tag : list) {
            if (Objects.equals(this.tag, tag)) {
                return true;
            }
        }
        //#endif

        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.test(Items.AIR.getDefaultStack());
    }

    @Override
    public MutableText toText() {
        return (MutableText) this.getName().copy();
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public Item getItem() {
        return Items.AIR;
    }

    @Override
    public Text getName() {
        return TextUtils.createText(this.tag);
    }

    @Override
    public ItemStack getDefaultStack() {
        for (Item item : Registries.ITEM) {
            ItemStack defaultStack = item.getDefaultStack();
            if (this.test(defaultStack)) {
                return defaultStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            return this.tag.equals(((ItemTagMatcher) obj).tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.tag.hashCode();
    }

    @Override
    public String toString() {
        return "#" + this.tag;
    }
}
