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

//#if MC>=12005
//$$ import net.minecraft.component.ComponentMap;
//#endif
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WithNbtItemStackPredicate extends AbstractItemStackPredicate {
    public WithNbtItemStackPredicate(AbstractRegistryEntryPredicate predicate,
                                     @Nullable
                                      //#if MC<12005
                                      NbtCompound
                                      //#else
                                      //$$ ComponentMap
                                      //#endif
                                      nbt) {
        super(predicate, nbt);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return predicate.test(itemStack) &&
                //#if MC>=12005
                //$$ Objects.equals(nbt, itemStack.getComponents());
                //#else
                NbtHelper.matches(nbt, itemStack.getNbt(), true);
                //#endif
    }
}
