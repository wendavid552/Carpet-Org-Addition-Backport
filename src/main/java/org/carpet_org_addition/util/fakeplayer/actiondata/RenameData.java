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
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;

public class RenameData extends AbstractActionData {
    private static final String ITEM = "item";
    private static final String NEW_NAME = "new_name";
    /**
     * 要进行重命名的物品
     */
    private final Item item;
    /**
     * 物品的新名称
     */
    private final String newName;

    public RenameData(Item item, String newName) {
        this.item = item;
        this.newName = newName;
    }

    public static RenameData load(JsonObject json) {
        Item item = Matcher.asItem(json.get(ITEM).getAsString());
        String newName = json.get(NEW_NAME).getAsString();
        return new RenameData(item, newName);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(ITEM, Registries.ITEM.getId(item).toString());
        json.addProperty(NEW_NAME, this.newName);
        return json;
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取假玩家的显示名称
        Text playerName = fakePlayer.getDisplayName();
        // 将假玩家要重命名的物品和物品新名称的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.rename.item",
                playerName, this.item.getDefaultStack().toHoverableText(), newName));
        // 将假玩家剩余经验的信息添加到集合
        list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.rename.xp",
                fakePlayer.experienceLevel));
        if (fakePlayer.currentScreenHandler instanceof AnvilScreenHandler) {
            AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) fakePlayer.currentScreenHandler;
            // 将铁砧GUI上的物品信息添加到集合
            list.add(TextUtils.appendAll("    ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(0).getStack()), " ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(1).getStack()), " -> ",
                    getWithCountHoverText(anvilScreenHandler.getSlot(2).getStack())));
        } else {
            // 将假玩家没有打开铁砧的信息添加到集合
            list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.rename.no_anvil",
                    playerName, Items.ANVIL.getName()));
        }
        return list;
    }

    public Item getItem() {
        return item;
    }

    public String getNewName() {
        return newName;
    }
}
