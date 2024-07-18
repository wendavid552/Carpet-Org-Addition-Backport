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

package org.carpet_org_addition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
//#if MC>=12002
//$$ import net.minecraft.recipe.RecipeEntry;
//#endif
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.JsonSerial;
import org.carpet_org_addition.util.matcher.Matcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractActionData implements JsonSerial {
    public abstract ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer);

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Item item) {
        if (item == Items.AIR || item == null) {
            return TextUtils.hoverText(TextUtils.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(item.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(TextUtils.literal(capitalizeFirstLetter), item.getName(), null);
    }

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Matcher matcher) {
        if (matcher.isEmpty()) {
            return TextUtils.hoverText(TextUtils.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(matcher.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(TextUtils.literal(capitalizeFirstLetter), matcher.getName(), null);
    }

    // 获取物品堆栈的可变文本形式：物品名称x堆叠数量
    protected static MutableText getWithCountHoverText(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return TextUtils.hoverText(TextUtils.literal("[A]"), TextUtils.appendAll(Items.AIR.getName()), Formatting.DARK_GRAY);
        }
        // 获取物品堆栈对应的物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(itemStack.getItem().toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(TextUtils.literal(capitalizeFirstLetter),
                TextUtils.appendAll(itemStack.getItem().getName(), "x", String.valueOf(itemStack.getCount())), null);
    }

    // 获取配方的输出物品
    protected static Item getCraftOutPut(EntityPlayerMPFake fakePlayer, Matcher[] arr) {
        // 合成格的宽高，如果数组长度为9，则表示在工作台合成，所以宽高为3，否则，物品在生存模式物品栏合成，所以宽高为2
        CraftingInventory craftingInventory = getCraftingInventory(arr);
        // 设置物品栏中每一个物品为配方中的物品
        for (int i = 0; i < arr.length; i++) {
            // 获取一个与对象匹配的物品堆栈对象
            ItemStack itemStack = arr[i].getDefaultStack();
            craftingInventory.setStack(i, itemStack);
        }
        World world = fakePlayer.getWorld();
        // 获取配方的输出
        Optional<?> optional = fakePlayer.getCommandSource().getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        return optional.map(craftingRecipe -> (
                //#if MC>=12002
                //$$ ((RecipeEntry<CraftingRecipe>)craftingRecipe).value()
                //#else
                (CraftingRecipe)craftingRecipe
                //#endif
        ).craft(craftingInventory
                //#if MC>=11904
                ,world.getRegistryManager()
                //#endif
        ).getItem()).orElse(Items.AIR);
    }

    @NotNull
    private static CraftingInventory getCraftingInventory(Matcher[] arr) {
        int widthHeight = arr.length == 9 ? 3 : 2;
        // 获取一个合成物品栏的对象，重写方法仅仅是为了代码不报错
        return new CraftingInventory(new ScreenHandler(null, -1) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        }, widthHeight, widthHeight);
    }
}
