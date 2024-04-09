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

package club.mcams.carpet.mixin.rule.ironGolemNoDropFlower;

import club.mcams.carpet.AmsServerSettings;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    @Nullable
    public abstract ItemEntity dropStack(ItemStack stack);

    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void onDeath(ItemStack stack, CallbackInfoReturnable<ItemEntity> cir) {
        if (AmsServerSettings.ironGolemNoDropFlower) {
            Entity entity = (Entity)(Object)this;
            if (entity instanceof IronGolemEntity && stack.getItem().equals(Items.POPPY)) {
                cir.setReturnValue(dropStack(ItemStack.EMPTY));
            }
        }
    }
}
