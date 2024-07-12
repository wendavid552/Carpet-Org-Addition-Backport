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
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.predicate.AbstractItemStackPredicate;

import java.util.function.Predicate;

public class ItemPredicateMatcher implements Matcher {
    private final Predicate<ItemStack> predicate;

    public ItemPredicateMatcher(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return this.predicate.test(itemStack);
    }

    @Override
    public boolean isEmpty() {
        return this.predicate.test(ItemStack.EMPTY);
    }

    @Override
    public boolean isItem() {
//        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
//            // “#”开头的是物品标签
//            return !itemStackPredicate.toString().startsWith("#");
//        }

        return false;
    }

    @Override
    public Item getItem() {
        if (this.predicate instanceof ItemPredicate) {
            String itemOrTag = (this.predicate).toString();
            if (itemOrTag.startsWith("#")) {
                return Items.AIR;
            }
            return Matcher.asItem(itemOrTag);
        }
        return Items.AIR;
    }

    @Override
    public Text getName() {
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String nameOrTag = itemStackPredicate.toString();
            if (nameOrTag.startsWith("#")) {
                return TextUtils.createText(nameOrTag);
            }
            return Matcher.asItem(nameOrTag).getName();
        }
        return TextUtils.getTranslate("carpet.commands.playerAction.info.craft.item_tag");
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
    public MutableText toText() {
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String itemOrTag = itemStackPredicate.toString();
            if (itemOrTag.startsWith("#")) {
                return TextUtils.createText(itemOrTag);
            } else {
                return Matcher.asItem(itemOrTag).getDefaultStack().toHoverableText().copy();
            }
        }
        return TextUtils.getTranslate("carpet.commands.playerAction.info.craft.item_tag");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ItemPredicateMatcher predicateMatcher) {
            return this.predicate.equals(predicateMatcher.predicate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.predicate.hashCode();
    }

    @Override
    public String toString() {
        if (this.predicate instanceof AbstractItemStackPredicate itemStackPredicate) {
            String string = itemStackPredicate.toString();
            if (string.startsWith("#")) {
                return string;
            }
            String[] split = string.split(":");
            return split.length == 2 ? split[1] : split[0];
        }
        return Items.AIR.toString();
    }
}
