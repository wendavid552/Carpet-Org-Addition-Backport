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

package org.carpet_org_addition.util.findtask.result;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;

public class TradeEnchantedBookResult extends AbstractTradeFindResult {
    /**
     * 获取到的附魔
     */
    private final Enchantment enchantment;
    /**
     * 获取到附魔的等级
     */
    private final int level;

    public TradeEnchantedBookResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex, Enchantment enchantment, int level) {
        super(merchant, tradeOffer, tradeIndex);
        this.enchantment = enchantment;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public MutableText toText() {
        return TextUtils.getTranslate("carpet.commands.finder.trade.enchanted_book.each",
                TextUtils.blockPos(merchant.getBlockPos(), Formatting.GREEN), merchantName, tradeIndex, enchantment.getName(level));
    }
}
