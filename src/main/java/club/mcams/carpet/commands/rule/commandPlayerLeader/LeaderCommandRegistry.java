/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  A Minecraft Server and contributors
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

package club.mcams.carpet.commands.rule.commandPlayerLeader;

import club.mcams.carpet.AmsServerSettings;
import club.mcams.carpet.translations.Translator;
import club.mcams.carpet.utils.CommandHelper;
import club.mcams.carpet.utils.Messenger;
import club.mcams.carpet.config.rule.commandLeader.LeaderConfig;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LeaderCommandRegistry {
    private static final Translator translator = new Translator("command.leader");
    private static final String MSG_HEAD = "<commandPlayerLeader> ";
    private static final int GLOWING_TIME = Integer.MAX_VALUE;
    public static final StatusEffectInstance HIGH_LIGHT = new StatusEffectInstance(StatusEffects.GLOWING, GLOWING_TIME);
    public static final Map<String, String> LEADER_LIST = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("leader")
            .requires(source -> CommandHelper.canUseCommand(source, AmsServerSettings.commandPlayerLeader))
            .then(literal("add")
            .then(argument("player", EntityArgumentType.player())
            .executes(context -> add(context.getSource().getServer(), EntityArgumentType.getPlayer(context, "player")))))
            .then(literal("remove")
            .then(argument("player", EntityArgumentType.player())
            .executes(context -> remove(context.getSource().getServer(), EntityArgumentType.getPlayer(context, "player")))))
            .then(literal("removeAll")
            .executes(context -> removeAll(context.getSource().getServer(), context.getSource().getPlayer())))
            .then(literal("list")
            .executes(context -> list(context.getSource().getPlayer())))
            .then(literal("help")
            .executes(context -> help(context.getSource().getPlayer())))
        );
    }

    private static int add(MinecraftServer server, PlayerEntity targetPlayer) {
        if (!LEADER_LIST.containsValue(getPlayerUUID(targetPlayer))) {
            targetPlayer.addStatusEffect(HIGH_LIGHT);
            Messenger.sendServerMessage(
                server,
                Messenger.s(
                    String.format("%s %s %s", MSG_HEAD, getPlayerName(targetPlayer), translator.tr("add").getString())
                ).formatted(Formatting.GRAY)
            );
            LEADER_LIST.put(getPlayerName(targetPlayer), getPlayerUUID(targetPlayer));
            saveToJson(server);
        } else {
            Messenger.sendServerMessage(
                server,
                Messenger.s(
                    MSG_HEAD + getPlayerName(targetPlayer) + " " + translator.tr("is_already_leader").getString()
                ).formatted(Formatting.RED, Formatting.ITALIC)
            );
        }
        return 1;
    }

    private static int remove(MinecraftServer server, PlayerEntity targetPlayer) {
        if (LEADER_LIST.containsValue(getPlayerUUID(targetPlayer))) {
            targetPlayer.removeStatusEffect(HIGH_LIGHT.getEffectType());
            Messenger.sendServerMessage(
                server,
                Messenger.s(
                    String.format("%s %s %s", MSG_HEAD, getPlayerName(targetPlayer), translator.tr("remove").getString())
                ).formatted(Formatting.GRAY)
            );
            LEADER_LIST.remove(getPlayerName(targetPlayer), getPlayerUUID(targetPlayer));
            saveToJson(server);
        } else {
            Messenger.sendServerMessage(
                server,
                Messenger.s(
                    String.format(MSG_HEAD + getPlayerName(targetPlayer) + " " + translator.tr("is_not_leader").getString())
                ).formatted(Formatting.RED, Formatting.ITALIC)
            );
        }
        return 1;
    }

    private static int removeAll(MinecraftServer server, PlayerEntity player) {
        Iterator<Map.Entry<String, String>> iterator = LEADER_LIST.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String playerUUID = entry.getValue();
            PlayerEntity targetPlayer = server.getPlayerManager().getPlayer(UUID.fromString(playerUUID));
            if (targetPlayer != null) {
                targetPlayer.removeStatusEffect(HIGH_LIGHT.getEffectType());
            }
            iterator.remove();
        }
        LEADER_LIST.clear();
        saveToJson(server);
        player.sendMessage(
            Messenger.s(MSG_HEAD + translator.tr("removeAll").getString()).formatted(Formatting.YELLOW), false
        );
        return 1;
    }

    private static int list(PlayerEntity player) {
        final String LINE = "-----------------------------------------";
        player.sendMessage(
            Messenger.s(
                translator.tr("list").getString() + "\n" + LINE
            ).formatted(Formatting.AQUA, Formatting.BOLD), false
        );
        for (Map.Entry<String, String> entry : LEADER_LIST.entrySet()) {
            String playerName = entry.getKey();
            String playerUUID = getPlayerUUID(player);
            player.sendMessage(Messenger.s(playerName + " - " + playerUUID).formatted(Formatting.DARK_AQUA), false);
        }
        return 1;
    }

    private static int help(PlayerEntity player) {
        String addHelp = translator.tr("help.add").getString();
        String removeHelp = translator.tr("help.remove").getString();
        String removeAllHelp = translator.tr("help.removeAll").getString();
        String listHelp = translator.tr("help.list").getString();
        player.sendMessage(
            Messenger.s(addHelp + "\n" + removeHelp + "\n" + removeAllHelp + "\n" + listHelp).
            setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
            false
        );
        return 1;
    }

    private static String getPlayerName(PlayerEntity player) {
        return player.getName().getString();
    }

    private static String getPlayerUUID(PlayerEntity player) {
        return player.getUuidAsString();
    }

    private static void saveToJson(MinecraftServer server) {
        String CONFIG_FILE_PATH = LeaderConfig.getPath(server);
        LeaderConfig.saveToJson(LEADER_LIST, CONFIG_FILE_PATH);
    }
}
