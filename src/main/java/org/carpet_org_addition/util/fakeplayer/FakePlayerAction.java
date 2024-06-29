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

package org.carpet_org_addition.util.fakeplayer;

import org.carpet_org_addition.util.fakeplayer.actiondata.*;

public enum FakePlayerAction {
    /**
     * 停止操作
     */
    STOP,
    /**
     * 物品分拣
     */
    SORTING,
    /**
     * 清空潜影盒
     */
    CLEAN,
    /**
     * 填充潜影盒
     */
    FILL,
    /**
     * 在工作台合成物品
     */
    CRAFTING_TABLE_CRAFT,
    /**
     * 在生存模式物品栏合成物品
     */
    INVENTORY_CRAFT,
    /**
     * 自动重命名物品
     */
    RENAME,
    /**
     * 自动使用切石机
     */
    STONECUTTING,
    /**
     * 自动交易
     */
    TRADE;

    // 检查当前动作是否与指定动作数据匹配
    public void checkActionData(Class<? extends AbstractActionData> clazz) {
//        if (clazz != switch (this) {
//            case STOP -> StopData.class;
//            case SORTING -> SortingData.class;
//            case CLEAN -> CleanData.class;
//            case FILL -> FillData.class;
//            case INVENTORY_CRAFT -> InventoryCraftData.class;
//            case CRAFTING_TABLE_CRAFT -> CraftingTableCraftData.class;
//            case RENAME -> RenameData.class;
//            case STONECUTTING -> StonecuttingData.class;
//            case TRADE -> TradeData.class;
//        }) {
//            throw new IllegalArgumentException();
//        }
        Class<? extends AbstractActionData> dataClass = null;
        switch(this) {
            case STOP: {
                dataClass = StopData.class;
                break;
            }
            case SORTING: {
                dataClass = SortingData.class;
                break;
            }
            case CLEAN: {
                dataClass = CleanData.class;
                break;
            }
            case FILL: {
                dataClass = FillData.class;
                break;
            }
            case INVENTORY_CRAFT: {
                dataClass = InventoryCraftData.class;
                break;
            }
            case CRAFTING_TABLE_CRAFT: {
                dataClass = CraftingTableCraftData.class;
                break;
            }
            case RENAME: {
                dataClass = RenameData.class;
                break;
            }
            case STONECUTTING: {
                dataClass = StonecuttingData.class;
                break;
            }
            case TRADE: {
                dataClass = TradeData.class;
                break;
            }
        }
        if (clazz != dataClass) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
//        return switch (this) {
//            case STOP -> "停止";
//            case SORTING -> "分拣";
//            case CLEAN -> "清空潜影盒";
//            case FILL -> "填充潜影盒";
//            case CRAFTING_TABLE_CRAFT -> "在工作台合成物品";
//            case INVENTORY_CRAFT -> "在生存模式物品栏合成物品";
//            case RENAME -> "重命名";
//            case STONECUTTING -> "切石";
//            case TRADE -> "交易";
//        };
        switch (this) {
            case STOP: {
                return "停止";
            }
            case SORTING: {
                return "分拣";
            }
            case CLEAN: {
                return "清空潜影盒";
            }
            case FILL: {
                return "填充潜影盒";
            }
            case CRAFTING_TABLE_CRAFT: {
                return "在工作台合成物品";
            }
            case INVENTORY_CRAFT: {
                return "在生存模式物品栏合成物品";
            }
            case RENAME: {
                return "重命名";
            }
            case STONECUTTING: {
                return "切石";
            }
            case TRADE: {
                return "交易";
            }
        }
        return "";
    }
}
