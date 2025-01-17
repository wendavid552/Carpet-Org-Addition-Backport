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

package org.carpet_org_addition.util;

//#if MC>11900
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.command.argument.RegistryEntryArgumentType;
//#else
//$$ import net.minecraft.command.argument.EnchantmentArgumentType;
//#endif
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class CommandNodeFactory {
        private final Object context;

        public CommandNodeFactory(@Nullable Object context) {
            this.context = context;
        }


        public ItemStackArgumentType itemStack() {
            return ItemStackArgumentType.itemStack(
                    //#if MC>11900
                    (CommandRegistryAccess) this.context
                    //#endif
            );
        }

        public ItemPredicateArgumentType itemPredicate() {
            return ItemPredicateArgumentType.itemPredicate(
                    //#if MC>11900
                    (CommandRegistryAccess) this.context
                    //#endif
            );
        }

        public BlockStateArgumentType blockState() {
            return BlockStateArgumentType.blockState(
                    //#if MC>11900
                    (CommandRegistryAccess) this.context
                    //#endif
            );
        }

        public
        //#if MC>=11904
        RegistryEntryArgumentType<Enchantment>
        //#else
        //$$ EnchantmentArgumentType
        //#endif
        enchantment() {
            //#if MC>=11904
            return RegistryEntryArgumentType.registryEntry((CommandRegistryAccess) this.context, RegistryKeys.ENCHANTMENT);
            //#else
            //$$ return EnchantmentArgumentType.enchantment();
            //#endif
        }
    }
