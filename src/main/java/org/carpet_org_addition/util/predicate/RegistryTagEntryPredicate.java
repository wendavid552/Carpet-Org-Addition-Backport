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

import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RegistryTagEntryPredicate extends AbstractRegistryEntryPredicate {
    private final TagResult tagResult;

    public RegistryTagEntryPredicate(TagResult tagResult) {
        this.tagResult = tagResult;
    }

    @Override
    public boolean test(RegistryEntry<Item> itemRegistryEntry) {
        return this.tagResult.tag().contains(itemRegistryEntry);
    }

    @Override
    public String toString() {
        Optional<TagKey<Item>> tagKey = this.tagResult.tag().getTagKey();
        return tagKey.map(itemTagKey -> "#" + itemTagKey.id().toString()).orElse("#");
    }

    public static record TagResult(RegistryEntryList<Item> tag, @Nullable NbtCompound nbt) {

    }
}
