package org.carpet_org_addition.util;

//#if MC>11900
import net.minecraft.command.CommandRegistryAccess;
//#endif
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;

public class CommandNodeFactory {
        //#if MC>11900
        private final CommandRegistryAccess context;

        public CommandNodeFactory(CommandRegistryAccess context) {
            this.context = context;
        }
        //#else
        //$$ public CommandNodeFactory() {
        //$$ }
        //#endif

        public ItemStackArgumentType itemStack() {
            return ItemStackArgumentType.itemStack(
                    //#if MC>11900
                    this.context
                    //#endif
            );
        }

        public ItemPredicateArgumentType itemPredicate() {
            return ItemPredicateArgumentType.itemPredicate(
                    //#if MC>11900
                    this.context
                    //#endif
            );
        }
    }
