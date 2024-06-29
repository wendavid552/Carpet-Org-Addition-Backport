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
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;

import java.util.ArrayList;

public class TradeItemFindFeedback extends AbstractTradeFindFeedback<TradeItemFindResult> {
    protected final ItemStack itemStack;

    public TradeItemFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<TradeItemFindResult> list, BlockPos sourceBlockPos, ItemStack itemStack, int maxCount) {
        super(context, list, sourceBlockPos, maxCount);
        this.itemStack = itemStack;
        this.setName("TradeItemFindFeedbackThread");
    }

    @Override
    public void run() {
        // 按照从近到远的顺序排序
        list.sort((o1, o2) -> MathUtils.compareBlockPos(sourceBlockPos, o1.getMerchant().getBlockPos(), o2.getMerchant().getBlockPos()));
        super.run();
    }

    @Override
    protected String getTranslateKey() {
        return "carpet.commands.finder.trade.result.limit";
    }

    @Override
    protected Text getFindItemText() {
        return itemStack.toHoverableText();
    }
}
