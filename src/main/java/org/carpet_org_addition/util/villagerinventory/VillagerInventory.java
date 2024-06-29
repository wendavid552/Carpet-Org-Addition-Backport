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

package org.carpet_org_addition.util.villagerinventory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import org.carpet_org_addition.util.wheel.AbstractCustomSizeInventory;

public class VillagerInventory extends AbstractCustomSizeInventory {
    private final VillagerEntity villager;
    private final SimpleInventory inventory;

    public VillagerInventory(VillagerEntity villager) {
        this.villager = villager;
        this.inventory = villager.getInventory();
    }

    @Override
    protected int getSize() {
        return 9;
    }

    @Override
    protected int getActualSize() {
        return 8;
    }

    @Override
    protected Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (villager.isDead() || villager.isRemoved()) {
            return false;
        }
        return player.distanceTo(villager) < 8;
    }
}
