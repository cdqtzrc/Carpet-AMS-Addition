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

package club.mcams.carpet.mixin.rule.craftableLight_craftableBarrier;

import club.mcams.carpet.AmsServerSettings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//#if MC>12002
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

import static net.minecraft.block.Block.dropStack;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "onBreak", at = @At("HEAD"))
    //#if MC>12002
    //$$ private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
    //#else
    private void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        //#endif
        //#if MC>=11700
        if (AmsServerSettings.craftableLight || AmsServerSettings.craftableBarrier) {
            ItemStack lightStack = new ItemStack(Items.LIGHT);
            ItemStack barrierStack = new ItemStack(Items.BARRIER);
            if (!player.isCreative()) {
                if (state.getBlock() == Blocks.LIGHT) {
                    dropStack(world, pos, lightStack);
                }
                if (state.getBlock() == Blocks.BARRIER) {
                    dropStack(world, pos, barrierStack);
                }
            }
        }
        //#else
        //$$ if (AmsServerSettings.craftableBarrier && state.getBlock() == Blocks.BARRIER && !player.isCreative()) {
        //$$     ItemStack barrierStack = new ItemStack(Items.BARRIER);
        //$$     dropStack(world, pos, barrierStack);
        //$$ }
        //#endif
    }
}
