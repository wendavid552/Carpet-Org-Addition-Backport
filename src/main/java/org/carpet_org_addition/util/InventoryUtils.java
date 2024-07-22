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

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.DefaultedList;
//#if MC>=12005
//$$ import net.minecraft.component.DataComponentTypes;
//$$ import net.minecraft.component.type.ContainerComponent;
//#endif
import org.carpet_org_addition.exception.NoNbtException;
import org.carpet_org_addition.util.wheel.ImmutableInventory;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryUtils {
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String ITEMS = "Items";
    public static final String INVENTORY = "Inventory";

    /**
     * 潜影盒工具类，私有化构造方法
     */
    private InventoryUtils() {
    }


    /**
     * 获取潜影盒内物品列表，注意会去掉ItemStack.empty
     * @param shulkerBox 当前要获取的潜影盒
     * @return 潜影盒内的物品列表
     */
    public static Stream<ItemStack> getInventoryStream(ItemStack shulkerBox) {
//        if (!InventoryUtils.isShulkerBoxItem(shulkerBox)) {
//            // 物品不是潜影盒，自然不会有潜影盒的NBT
//            throw new NoNbtException();
//            //TODO: 相关异常处理，这里只是懒得改了
//        }
        Stream<ItemStack> containing = Stream.empty();
        // 正常情况下有物品的潜影盒不可堆叠，所以可堆叠的潜影盒内部没有物品
        if (shulkerBox.getCount() == 1 && isShulkerBoxItem(shulkerBox)) {
            //#if MC<12005
            NbtCompound blockEntityCompounds = shulkerBox.getSubNbt(BLOCK_ENTITY_TAG);
            if(blockEntityCompounds != null) {
                containing = blockEntityCompounds.getList(ITEMS, NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
            }
            //#else
            //$$  containing = shulkerBox.getComponents().get(DataComponentTypes.CONTAINER).stream();
            //#endif
        }
        return containing;
    }

    public static List<ItemStack> getInventoryList(ItemStack shulkerBox) {
        return getInventoryStream(shulkerBox).toList();
    }

    public static Stream<ItemStack> getInventoryNonEmptyStream(ItemStack shulkerBox) {
        return getInventoryStream(shulkerBox).filter(itemStack -> !itemStack.isEmpty());
    }

    public static List<ItemStack> getNonemptyInventoryList(ItemStack shulkerBox) {
        return getInventoryNonEmptyStream(shulkerBox).toList();
    }

        /**
     * 判断当前潜影盒是否是空潜影盒
     *
     * @param shulkerBox 当前要检查是否为空的潜影盒物品
     * @return 潜影盒内没有物品返回true，有物品返回false
     */
//    public static boolean isEmptyShulkerBox(ItemStack shulkerBox) {
//        // 正常情况下有物品的潜影盒无法堆叠
//        if (shulkerBox.getCount() != 1) {
//            return true;
//        }
//
//        return containing.isEmpty();
//    }
    public static boolean isNonEmptyShulkerBox(ItemStack shulkerBox) {
        return shulkerBox.getCount() == 1
                && isShulkerBoxItem(shulkerBox)
                && !getInventoryNonEmptyStream(shulkerBox).allMatch(ItemStack::isEmpty);
    }

    public static boolean isEmptyShulkerBox(ItemStack shulkerBox) {
        return shulkerBox.getCount() != 1 ||
                (isShulkerBoxItem(shulkerBox) && getInventoryStream(shulkerBox).allMatch(ItemStack::isEmpty));
    }

    /**
     * 取出并删除潜影盒内容物的第一个非空气物品，堆叠的潜影盒会被视为空潜影盒。<br/>
     * <br/>
     * 因为正常情况下有物品的潜影盒无法堆叠（原版的潜影盒不可堆叠，但是空潜影盒可以通过Carpet或Tweakeroo的功能堆叠），即便是有物品，也不能使用
     * 本方法取出物品，如果需要取出，应该现将物品堆分开，否则如果直接取出，则堆叠的所有潜影盒都会受影响。假设有一个堆叠数为10的潜影盒，内含一组物品，
     * 如果将物品分成10份后再取出，则每个潜影盒都可以取出1组物品，总共可以取出10组物品。但是如果直接使用本方法取出物品，则只能取出一组物品，然后获得
     * 一个10堆叠的空潜影盒，这会损失一些物品。因为本方法操作的整组物品堆栈，操作时并不会考虑物品堆叠数量，所以需要事先将堆叠潜影盒分开。
     *
     * @param shulkerBox 当前要操作的潜影盒
     * @return 潜影盒内第一个非空气物品，如果潜影盒内没有物品，返回ItemStack.EMPTY
     */
    //TODO:用Collectors写，提高性能
    public static ItemStack getShulkerBoxItem(ItemStack shulkerBox) {
        ItemStack firstStack = ItemStack.EMPTY;
        if (isNonEmptyShulkerBox(shulkerBox)) {
            List<ItemStack> containing = getNonemptyInventoryList(shulkerBox);
            for (ItemStack itemStack : containing) {
                //拷贝第一个ItemStack
                firstStack = itemStack.copy();
                containing.remove(itemStack);
                //从shulkerbox中删除找到的itemStack
                //#if MC<12005
                itemStack.setCount(0);
                if (isEmptyShulkerBox(shulkerBox)) {
                    shulkerBox.removeSubNbt(BLOCK_ENTITY_TAG);
                }
                //#else
                //$$ shulkerBox.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(containing));
                //#endif
                break;
            }
        }
        return firstStack;
    }

    /**
     * 从物品形式的潜影盒中获取第一个指定的物品，并将该物品从潜影盒的NBT中删除，使用时，为避免不必要的物品浪费，取出来的物品必须使用或丢出
     *
     * @param shulkerBox 潜影盒物品
     * @param matcher    一个物品匹配器对象，用来指定要从潜影盒中拿取的物品
     * @return 潜影盒中获取的指定物品
     */
    public static ItemStack pickItemFromShulkerBox(ItemStack shulkerBox, Matcher matcher) {
        ItemStack firstStack = ItemStack.EMPTY;
        if (isNonEmptyShulkerBox(shulkerBox)) {
            List<ItemStack> containing = getNonemptyInventoryList(shulkerBox);
            for (ItemStack itemStack : containing) {
                if (matcher.test(itemStack)) {
                    //拷贝第一个ItemStack
                    firstStack = itemStack.copy();
                    containing.remove(itemStack);
                    //从shulkerbox中删除找到的itemStack
                    //#if MC<12005
                    itemStack.setCount(0);
                    if (isEmptyShulkerBox(shulkerBox)) {
                        shulkerBox.removeSubNbt(BLOCK_ENTITY_TAG);
                    }
                    //#else
                    //$$ shulkerBox.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(containing));
                    //#endif
                    break;
                }
            }
        }
        return firstStack;
    }

    /**
     * 获取潜影盒物品的物品栏
     *
     * @param shulkerBox 要获取物品栏的潜影盒
     * @return 潜影盒内的物品栏
     * @throws NoNbtException 物品不是潜影盒，或者潜影盒没有NBT时抛出
     */
    public static ImmutableInventory getInventory(ItemStack shulkerBox) {
//        try {
//            // 获取潜影盒NBT
//            //#if MC<12005
//            NbtCompound nbt = Objects.requireNonNull(shulkerBox.getNbt()).getCompound(BLOCK_ENTITY_TAG);
//            if (nbt != null && nbt.contains(ITEMS, NbtElement.LIST_TYPE)) {
//                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
//                // 读取潜影盒NBT
//                Inventories.readNbt(nbt, defaultedList);
//                return new ImmutableInventory(defaultedList);
//            }
//            //#else
//            //$$ ContainerComponent component = shulkerBox.get(DataComponentTypes.CONTAINER);
//            //$$ if (component != null) {
//            //$$    List<ItemStack> list = component.stream().toList();
//            //$$    return new ImmutableInventory((DefaultedList<ItemStack>) list);
//            //$$ }
//            //#endif
//            throw new NoNbtException();
//        } catch (NullPointerException e) {
//            // 潜影盒物品没有NBT，说明该潜影盒物品为空
//            throw new NoNbtException();
//        }
        return new ImmutableInventory((DefaultedList<ItemStack>) getInventoryList(shulkerBox));
    }

//    /**
//     * 从NBT中获取一个物品栏对象
//     *
//     * @param nbt 从这个NBT中获取物品栏
//     */
//    @SuppressWarnings("unused")
//    public static ImmutableInventory getInventoryFromNbt(NbtCompound nbt) {
//        NbtList inventory = nbt.getList(INVENTORY, NbtElement.COMPOUND_TYPE);
//        int size = inventory.size();
//        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(size, ItemStack.EMPTY);
//        for (int index = 0; index < size; index++) {
//            defaultedList.set(index, ItemStack.fromNbt(inventory.getCompound(index)));
//        }
//        return new ImmutableInventory(defaultedList);
//    }

    /**
     * 判断指定物品是否为潜影盒
     *
     * @param shulkerBox 要判断是否为潜影盒的物品
     * @return 指定物品是否是潜影盒
     */
    public static boolean isShulkerBoxItem(ItemStack shulkerBox) {
        return shulkerBox.isOf(Items.SHULKER_BOX)
                || shulkerBox.isOf(Items.WHITE_SHULKER_BOX)
                || shulkerBox.isOf(Items.ORANGE_SHULKER_BOX)
                || shulkerBox.isOf(Items.MAGENTA_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIGHT_BLUE_SHULKER_BOX)
                || shulkerBox.isOf(Items.YELLOW_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIME_SHULKER_BOX)
                || shulkerBox.isOf(Items.PINK_SHULKER_BOX)
                || shulkerBox.isOf(Items.GRAY_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIGHT_GRAY_SHULKER_BOX)
                || shulkerBox.isOf(Items.CYAN_SHULKER_BOX)
                || shulkerBox.isOf(Items.PURPLE_SHULKER_BOX)
                || shulkerBox.isOf(Items.BLUE_SHULKER_BOX)
                || shulkerBox.isOf(Items.BROWN_SHULKER_BOX)
                || shulkerBox.isOf(Items.GREEN_SHULKER_BOX)
                || shulkerBox.isOf(Items.RED_SHULKER_BOX)
                || shulkerBox.isOf(Items.BLACK_SHULKER_BOX);
    }

    /**
     * 整理物品栏，合并未堆叠满的物品，然后物品按照ID排序，空气物品放在最后
     *
     * @param list 包含物品的集合
     */
    public static void sortInventory(List<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemStack = list.get(i);
            // 物品到最大堆叠还需要多少物品
            int count = itemStack.getMaxCount() - itemStack.getCount();
            // 该物品堆叠已满，跳过该物品的合并
            if (count <= 0) {
                continue;
            }
            // 合并物品
            for (int j = i + 1; j < list.size(); j++) {
                ItemStack otherStack = list.get(j);
                if (otherStack.isEmpty()) {
                    continue;
                }
                // 物品是否可以合并
                if (ItemStack.canCombine(itemStack, otherStack)) {
                    if (count - otherStack.getCount() > 0) {
                        // 合并后堆叠数量仍然不满
                        itemStack.increment(otherStack.getCount());
                        list.set(j, ItemStack.EMPTY);
                        count = itemStack.getMaxCount() - itemStack.getCount();
                    } else {
                        // 合并后堆叠数量已满
                        // 一共需要移动多少个物品
                        int moveCount = itemStack.getMaxCount() - itemStack.getCount();
                        itemStack.increment(moveCount);
                        otherStack.decrement(moveCount);
                        break;
                    }
                }
            }
        }
        // 物品排序
        list.sort((o1, o2) -> {
            // 空物品放在最后
            if (!o1.isEmpty() && o2.isEmpty()) {
                return -1;
            }
            if (o1.isEmpty() && !o2.isEmpty()) {
                return 1;
            }
            // 按物品ID排序
            return Registries.ITEM.getId(o1.getItem()).toString().compareTo(Registries.ITEM.getId(o2.getItem()).toString());
        });
    }

    public static List<ItemStack> toList(Inventory inventory) {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int index = 0; index < inventory.size(); index++) {
            list.add(inventory.getStack(index));
        }
        return list;
    }
}
