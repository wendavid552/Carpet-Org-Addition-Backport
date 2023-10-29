package org.carpet_org_addition.mixin.rule.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin extends ServerPlayerEntity implements FakePlayerActionInterface, FakePlayerProtectInterface {
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;
    //用来决定假人的操作类型
    private FakePlayerActionType action = FakePlayerActionType.STOP;
    //假玩家操作类型的命令参数
    private CommandContext<ServerCommandSource> context = null;
    //假玩家保护类型
    private FakePlayerProtectType protect = FakePlayerProtectType.NONE;

    //私有化构造方法，防止被创建对象
    private EntityPlayerMPFakeMixin(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile);
    }

    //命令上下文
    @Override
    public CommandContext<ServerCommandSource> getContext() {
        return context;
    }

    @Override
    public void setContext(CommandContext<ServerCommandSource> context) {
        this.context = context;
    }

    //假玩家操作类型
    @Override
    public FakePlayerActionType getAction() {
        return action;
    }

    @Override
    public void setAction(FakePlayerActionType action) {
        this.action = action;
    }

    // 假玩家3x3合成时的配方


    @Override
    public Item[] getCraft() {
        return ITEMS;
    }

    @Override
    public void setCraft(Item[] items) {
        //数组拷贝
        System.arraycopy(items, 0, ITEMS, 0, ITEMS.length);
    }

    //假玩家保护类型
    @Override
    public FakePlayerProtectType getProtect() {
        return protect;
    }

    @Override
    public void setProtected(FakePlayerProtectType protect) {
        this.protect = protect;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fakePlayerTick(CallbackInfo ci) {
        if (thisPlayer == null) {
            return;
        }
        //假玩家回血
        if (CarpetOrgAdditionSettings.fakePlayerHeal) {
            long time = thisPlayer.getWorld().getTime();
            if (time % 40 == 0) {
                thisPlayer.heal(1);
            }
        }
        //根据假玩家操作类型执行操作
        try {
            if (action != FakePlayerActionType.STOP && context != null) {
                this.fakePlayerAction();
            }
            //空指针异常
        } catch (NullPointerException e) {
            this.action = FakePlayerActionType.STOP;
            SendMessageUtils.broadcastTextMessage(thisPlayer, TextUtils.appendAll(
                    thisPlayer.getDisplayName(), ": ",
                    TextUtils.getTranslate("carpet.commands.playerTools.action.exception.null_pointer")));
            e.printStackTrace();
        } catch (InfiniteLoopException e) {
            this.action = FakePlayerActionType.STOP;
            SendMessageUtils.broadcastTextMessage(thisPlayer, TextUtils.appendAll(
                    thisPlayer.getDisplayName(), ": ",
                    TextUtils.getTranslate("carpet.commands.playerTools.action.exception.infinite_loop")));
            e.printStackTrace();
        } catch (RuntimeException e) {
            this.action = FakePlayerActionType.STOP;
            SendMessageUtils.broadcastTextMessage(thisPlayer,
                    TextUtils.getTranslate("carpet.commands.playerTools.action.exception.runtime",
                            thisPlayer.getDisplayName()));
            e.printStackTrace();
        }
    }

    //根据假玩家操作类型执行操作
    private void fakePlayerAction() {
        switch (action) {
            //假玩家分拣
            case SORTING -> FakePlayerSorting.sorting(context, thisPlayer);
            //假玩家清空容器
            case CLEAN -> FakePlayerClean.clean(thisPlayer);
            //假玩家填充容器
            case FILL -> FakePlayerMoveItem.moveItem(context, thisPlayer);
            //假玩家自动合成物品（单个材料）
            case CRAFT_ONE -> FakePlayerCraft.craftOne(context, thisPlayer);
            //假玩家自动合成物品（四个相同的材料）
            case CRAFT_FOUR -> FakePlayerCraft.craftFour(context, thisPlayer);
            //假玩家自动合成物品（九个相同的材料）
            case CRAFT_NINE -> FakePlayerCraft.craftNine(context, thisPlayer);
            //假玩家自动合成物品（9x9自定义物品）
            case CRAFT_3X3 -> FakePlayerCraft.craft3x3(context, thisPlayer, ITEMS);
            //假玩家自动合成物品（4x4自定义物品）
            case CRAFT_2X2 -> FakePlayerCraft.craft2x2(context, thisPlayer);
            //假玩家自动重命名
            case RENAME -> FakePlayerRename.rename(context, thisPlayer);
            //假玩家切石机
            case STONE_CUTTING -> FakePlayerStoneCutting.stoneCutting(context, thisPlayer);
            //假玩家交易
            case TRADE -> FakePlayerTrade.trade(context, thisPlayer);
            //以上值都不匹配，设置操作类型为STOP（不应该出现都不匹配的情况）
            default -> action = FakePlayerActionType.STOP;
        }
    }

    //阻止受保护的假玩家受到伤害
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (CarpetOrgAdditionSettings.fakePlayerProtect && FakePlayerProtectManager.isNotDamage(thisPlayer)
                && !(source.getSource() instanceof PlayerEntity) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.damage(source, amount);
    }

    //阻止受保护的假玩家死亡
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.fakePlayerProtect && FakePlayerProtectManager.isNotDeath(thisPlayer)
                && !(source.getSource() instanceof PlayerEntity) && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.setHealth(this.getMaxHealth());
            HungerManager hungerManager = this.getHungerManager();
            hungerManager.setFoodLevel(20);
            hungerManager.setSaturationLevel(5.0f);
            hungerManager.setExhaustion(0);
            ci.cancel();
        }
    }
}
