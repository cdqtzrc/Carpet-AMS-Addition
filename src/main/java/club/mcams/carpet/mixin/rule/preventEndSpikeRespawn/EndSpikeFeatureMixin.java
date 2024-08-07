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

package club.mcams.carpet.mixin.rule.preventEndSpikeRespawn;

import club.mcams.carpet.AmsServerSettings;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.feature.EndSpikeFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndSpikeFeature.class)
public class EndSpikeFeatureMixin {
    @WrapOperation(
        method="generateSpike",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/EndSpikeFeature;setBlockState(Lnet/minecraft/world/ModifiableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
        )
    )
    private void onSetBlockState(EndSpikeFeature endSpikeFeature, ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState, Operation<Void> original) {
        if (AmsServerSettings.preventEndSpikeRespawn.equals("false")) {
            original.call(endSpikeFeature, modifiableWorld, blockPos, blockState);
        }
    }

    @Inject(
        method="generateSpike",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ServerWorldAccess;spawnEntity(Lnet/minecraft/entity/Entity;)Z"
        ),
        cancellable = true
    )
    private void onSpawnEntity(CallbackInfo ci) {
        if (AmsServerSettings.preventEndSpikeRespawn.equals("true")) {
            ci.cancel();
        }
    }
}
