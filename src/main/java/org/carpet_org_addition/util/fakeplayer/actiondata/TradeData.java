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
import com.google.gson.JsonObject;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.SingleThingCounter;
import org.spongepowered.asm.mixin.Mutable;

import java.util.ArrayList;

public class TradeData extends AbstractActionData {
    private static final String INDEX = "index";
    private static final String VOID_TRADE = "void_trade";
    /**
     * 交易GUI中左侧按钮的索引
     */
    private final int index;
    /**
     * 是否为虚空交易，虚空交易会在村民所在区块卸载再等待5个游戏刻后进行
     */
    private final boolean voidTrade;
    /**
     * 虚空交易的计时器
     */
    private final SingleThingCounter timer = new SingleThingCounter();

    public TradeData(int index, boolean voidTrade) {
        this.index = index;
        this.voidTrade = voidTrade;
        timer.set(5);
    }

    public static TradeData load(JsonObject json) {
        int index = json.get(INDEX).getAsInt();
        boolean voidTrade = json.get(VOID_TRADE).getAsBoolean();
        return new TradeData(index, voidTrade);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(INDEX, this.index);
        json.addProperty(VOID_TRADE, this.voidTrade);
        return json;
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取按钮的索引，从1开始
        list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.trade.item", fakePlayer.getDisplayName(), index));
        if (fakePlayer.currentScreenHandler instanceof MerchantScreenHandler) {
            MerchantScreenHandler merchantScreenHandler = (MerchantScreenHandler) fakePlayer.currentScreenHandler;
            // 获取当前交易内容的对象，因为按钮索引从1开始，所以此处减去1
            TradeOffer tradeOffer = merchantScreenHandler.getRecipes().get(index - 1);
            // 将“交易选项”文本信息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.trade.option", index));
            // 将交易的物品和价格添加到集合中

            MutableText second = TextUtils.createText("");;
            if (!tradeOffer.getSecondBuyItem().isEmpty()) {
                second = TextUtils.appendAll(" ",getWithCountHoverText(tradeOffer.getSecondBuyItem()
                ));
            }
            list.add(TextUtils.appendAll("   ",
                    " ", getWithCountHoverText(tradeOffer.getAdjustedFirstBuyItem()),
                    second,
                    " -> ", getWithCountHoverText(tradeOffer.getSellItem())));
            // 如果当前交易已禁用，将交易已禁用的消息添加到集合，然后直接结束方法并返回集合
            if (tradeOffer.isDisabled()) {
                list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.trade.disabled"));
                return list;
            }
            // 将“交易状态”文本信息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.trade.state"));
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(0).getStack()), " ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(1).getStack()), " -> ",
                    getWithCountHoverText(merchantScreenHandler.getSlot(2).getStack())));
        } else {
            // 将假玩家没有打开交易界面的消息添加到集合中
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.trade.no_villager", fakePlayer.getDisplayName()));
        }
        return list;
    }

    public int getIndex() {
        return index;
    }

    public boolean isVoidTrade() {
        return voidTrade;
    }

    public SingleThingCounter getTimer() {
        return timer;
    }
}
