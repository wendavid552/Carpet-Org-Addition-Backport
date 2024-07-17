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

package org.carpet_org_addition.util.predicate;

import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * {@link net.minecraft.command.argument.ItemPredicateArgumentType#getItemStackPredicate(Predicate, NbtCompound)}方法的返回值
 * 是一个ItemStackPredicateArgument接口的实现类对象，它在lambda表达式中被定义，（貌似）无法使用Mixin注入代码，也就无法获取这个类中的成员变量等属性，
 * 因此，为了让/playerAction命令在获取假玩家合成物品动作的状态时正确显示物品的标签，本类重新实现了这个接口，并将原本类中用到这个lambda的重定向到了本类，
 * 然后重写了{@link AbstractItemStackPredicate#toString()}方法用来获取原lambda类中参数的字符串形式，方便其它类的调用
 *
 * @see org.carpet_org_addition.mixin.util.ItemPredicateArgumentTypeMixin
 */
@SuppressWarnings("JavadocReference")
public abstract class AbstractItemStackPredicate implements Predicate<ItemStack> {
    protected AbstractRegistryEntryPredicate predicate;
    protected @Nullable NbtCompound nbt;

    public AbstractItemStackPredicate(AbstractRegistryEntryPredicate predicate, @Nullable NbtCompound nbt) {
        this.predicate = predicate;
        this.nbt = nbt;
    }

    // 获取物品的名称，或者物品标签
    @Override
    public String toString() {
        return predicate.toString();
    }
}
