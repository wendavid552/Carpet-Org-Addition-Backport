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

package org.carpet_org_addition.util.findtask.finder;

import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.List;

public class TradeItemFinder extends AbstractFinder {
    private final ItemStackArgument argument;
    private final ArrayList<TradeItemFindResult> list = new ArrayList<>();

    public TradeItemFinder(World world, BlockPos sourcePos, int range, ItemStackArgument itemStackArgument) {
        super(world, sourcePos, range);
        this.argument = itemStackArgument;
    }

    @Override
    public ArrayList<TradeItemFindResult> startSearch() {
        ItemStack exampleItem;
        try {
            exampleItem = this.argument.createStack(1, false);
        } catch (Exception e) {
            CarpetOrgAddition.LOGGER.warn("TradeItemFinder中从输入argument创建ItemStack失败: {}", e.getMessage());
            return new ArrayList<>();
        }

        SelectionArea selectionArea = new SelectionArea(this.world, this.sourcePos, this.range);
        Box box = selectionArea.toBox();
        // 根据之前的盒子对象获取所有在这个区域内商人实体对象（村民和流浪商人）
        List<MerchantEntity> entities = world.getNonSpectatingEntities(MerchantEntity.class, box);
        for (MerchantEntity merchant : entities) {
            // 获取集合中的每一个实体，并获取每一个实体的交易选项
            TradeOfferList offerList = merchant.getOffers();
            for (TradeOffer offer : offerList) {
                if (ItemStack.canCombine(exampleItem, offer.getSellItem())) {
                    this.list.add(new TradeItemFindResult(merchant, offer, offerList.indexOf(offer) + 1));
                }
            }
        }
        return list;
    }
}
