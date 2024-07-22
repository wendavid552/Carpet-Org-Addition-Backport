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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
//#if MC>=12100
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import net.minecraft.registry.tag.EnchantmentTags;
//#endif
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class EnchantmentUtils {
    //#if MC>=12100
    //$$ /**
    //$$  * @return 指定物品上是否有指定附魔
    //$$  */
    //$$ public static boolean hasEnchantment(World world, RegistryKey<Enchantment> key, ItemStack itemStack) {
    //$$     Enchantment enchantment = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).get(key);
    //$$     return getLevel(world, enchantment, itemStack) > 0;
    //$$ }
    //$$
    //$$ /**
    //$$  * @return 获取指定物品上指定附魔的等级
    //$$  */
    //$$ public static int getLevel(World world, Enchantment enchantment, ItemStack itemStack) {
    //$$     RegistryEntry<Enchantment> entry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchantment);
    //$$     return EnchantmentHelper.getLevel(entry, itemStack);
    //$$ }
    //$$
    //$$ /**
    //$$  * @return 获取一个附魔的名字，不带等级
    //$$  */
    //$$ public static MutableText getName(Enchantment enchantment) {
    //$$     MutableText mutableText = enchantment.description().copy();
    //$$     // 如果是诅咒附魔，设置为红色
    //$$     if (RegistryEntry.of(enchantment).isIn(EnchantmentTags.CURSE)) {
    //$$         Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
    //$$     } else {
    //$$         Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
    //$$     }
    //$$     return mutableText;
    //$$ }
    //$$
    //$$ /**
    //$$  * @param level 附魔的等级
    //$$  * @return 获取一个附魔的名字，带有等级
    //$$  */
    //$$ public static MutableText getName(Enchantment enchantment, int level) {
    //$$     MutableText mutableText = getName(enchantment);
    //$$     if (level != 1 || enchantment.getMaxLevel() != 1) {
    //$$         mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + level));
    //$$     }
    //$$     return mutableText;
    //$$ }
    //#endif
}