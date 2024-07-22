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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;


public class RegistryTagEntryPredicate extends AbstractRegistryEntryPredicate {
    private final TagResult tagResult;

    public RegistryTagEntryPredicate(TagResult tagResult) {
        this.tagResult = tagResult;
    }

    @Override
    public boolean test(ItemStack item) {
        return item.isIn(tagResult.tag());
    }

    @Override
    public String toString() {
//        Optional<TagKey<Item>> tagKey = this.tagResult.tag().getStorage().left();
//        return tagKey.map(itemTagKey -> "#" + itemTagKey.id().toString()).orElse("#");
        return "#" + tagResult.tag().
                //#if MC>=11800
                id();
                //#else
                //$$ getId();
                //#endif
    }

    public static record TagResult(TagKey<Item> tag,
                                   @Nullable
                                      //#if MC<12005
                                      NbtCompound
                                      //#else
                                      //$$ ComponentMap
                                      //#endif
                                      nbt
    ) {

    }
}
