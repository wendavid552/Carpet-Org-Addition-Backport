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

package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.TradeEnchantedBookResult;

import java.util.ArrayList;

public class TradeEnchantedBookFeedback extends AbstractTradeFindFeedback<TradeEnchantedBookResult> {
    private final Enchantment enchantment;

    public TradeEnchantedBookFeedback(CommandContext<ServerCommandSource> context, ArrayList<TradeEnchantedBookResult> list, BlockPos sourceBlockPos, Enchantment enchantment, int maxCount) {
        super(context, list, sourceBlockPos, maxCount);
        this.enchantment = enchantment;
        this.setName("TradeEnchantedBookFindFeedbackThread");
    }

    @Override
    public void run() {
        // 按照附魔书等级从高到低进行排序，如果等级相同，按照玩家离村民的距离从近到远的顺序排序
        list.sort((o1, o2) -> {
            int compare = Integer.compare(o1.getLevel(), o2.getLevel());
            if (compare == 0) {
                return MathUtils.compareBlockPos(sourceBlockPos, o1.getMerchant().getBlockPos(), o2.getMerchant().getBlockPos());
            }
            return -compare;
        });
        super.run();
    }

    @Override
    protected String getTranslateKey() {
        return "carpet.commands.finder.trade.enchanted_book.result.limit";
    }

    @Override
    protected MutableText getFindItemText() {
        MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
        // 如果是诅咒附魔，设置为红色
        if (enchantment.isCursed()) {
            mutableText.formatted(Formatting.RED);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }
        return TextUtils.appendAll(mutableText, Items.ENCHANTED_BOOK.getName());
    }
}
