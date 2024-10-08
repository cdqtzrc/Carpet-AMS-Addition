/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.mcams.carpet.mixin.rule.maxPlayerInteractionRange;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import top.byteeeee.annotationtoolbox.annotation.GameVersion;

import club.mcams.carpet.AmsServerSettings;
import club.mcams.carpet.helpers.rule.maxPlayerInteractionDistance_maxClientInteractionReachDistance.MaxInteractionDistanceMathHelper;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.Mixin;

@GameVersion(version = "Minecraft < 1.20.5")
@Mixin(value = ServerPlayNetworkHandler.class, priority = 168)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyExpressionValue(method = "onPlayerInteractBlock", at = @At(value = "CONSTANT", args = "doubleValue=64.0D"))
    private double onPlayerInteractBlock1(double constant) {
        if (AmsServerSettings.maxPlayerBlockInteractionRange != -1.0D) {
            return MaxInteractionDistanceMathHelper.getMaxSquaredReachDistance(AmsServerSettings.maxPlayerBlockInteractionRange);
        } else {
            return constant;
        }
    }

    @GameVersion(version = "Minecraft < 1.19")
    @ModifyExpressionValue(method = "onPlayerInteractEntity", at = @At(value = "CONSTANT", args = "doubleValue=36.0D"))
    private double onPlayerInteractEntity(double constant) {
        if (AmsServerSettings.maxPlayerEntityInteractionRange != -1.0D) {
            return MaxInteractionDistanceMathHelper.getMaxSquaredReachDistance(AmsServerSettings.maxPlayerEntityInteractionRange);
        } else {
            return constant;
        }
    }
}