/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package club.mcams.carpet.mixin.rule.pointedDripstoneCollisionBoxDisabled;

import top.byteeeee.annotationtoolbox.annotation.GameVersion;

//#if MC>=11700
import club.mcams.carpet.AmsServerSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.block.AbstractBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
//#else
//$$ import club.mcams.carpet.utils.compat.DummyClass;
//#endif

import org.spongepowered.asm.mixin.Mixin;

@GameVersion(version = "Minecraft >= 1.17")
//#if MC>=11700
@Mixin(AbstractBlock.AbstractBlockState.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public abstract class AbstractBlockMixin {
//#if MC>=11700
    @Shadow
    public abstract Block getBlock();

    @ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
    private VoxelShape getCollisionShape(VoxelShape original) {
        if (AmsServerSettings.pointedDripstoneCollisionBoxDisabled && this.getBlock() == Blocks.POINTED_DRIPSTONE) {
            return VoxelShapes.empty();
        } else {
            return original;
        }
    }
//#endif
}
