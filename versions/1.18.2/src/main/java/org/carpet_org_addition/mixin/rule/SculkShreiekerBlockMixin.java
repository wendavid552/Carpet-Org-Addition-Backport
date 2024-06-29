package org.carpet_org_addition.mixin.rule;

import org.spongepowered.asm.mixin.Mixin;
import org.carpet_org_addition.util.compat.DummyClass;
import top.byteeeee.annotationtoolbox.annotation.GameVersion;

@GameVersion("Minecraft < 1.19")
@Mixin(DummyClass.class)
public class SculkShreiekerBlockMixin {
}
