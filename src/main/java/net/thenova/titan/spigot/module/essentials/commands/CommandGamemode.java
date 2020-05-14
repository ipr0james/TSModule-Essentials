package net.thenova.titan.spigot.module.essentials.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@CommandUsage(
        min = 0,
        usage = "gamemode <gamemode> [player]",
        description = "Change yours or anothers gamemode."
)
public final class CommandGamemode extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public CommandGamemode() {
        super("gamemode", "adventure", "eadventure", "adventuremode", "eadventuremode", "creative", "eecreative", "creativemode", "ecreativemode", "egamemode", "gm", "egm", "gma", "egma", "gmc", "egmc", "gms", "egms", "gmt", "egmt", "survival", "esurvival", "survivalmode", "esurvivalmode", "gmsp", "sp", "egmsp", "spec", "spectator");
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        String[] args = context.getArguments();

        GameMode gamemode = null;
        Player player = null;

        if(!(sender.getSender() instanceof Player)) {
            if(args.length == 0) {
                MessageHandler.INSTANCE.build("error.command.usage.length")
                        .placeholder(new Placeholder("usage", this.usage.usage()))
                        .send(sender);
                return;
            } else if(args.length == 1) {
                gamemode = matchGameMode(context.getLabel());
                player = Bukkit.getPlayer(args[0]);
            } else if (args.length == 2) {
                gamemode = matchGameMode(args[0]);
                player = Bukkit.getPlayer(args[1]);
            }
        } else {
            if(args.length == 0) {
                gamemode = matchGameMode(context.getLabel());
                player = (Player) sender.getSender();
            } else if(args.length > 1 && args[1].trim().length() > 2 && sender.getSender().hasPermission("nova.command.gamemode.others")) {
                gamemode = matchGameMode(args[0]);
                player = Bukkit.getPlayer(args[1]);
            } else {
                if((gamemode = matchGameMode(args[0])) == null) {
                    if(sender.getSender().hasPermission("nova.command.gamemode.others")) {
                        gamemode = matchGameMode(context.getLabel());
                        player = Bukkit.getPlayer(args[0]);
                    }
                } else {
                    gamemode = matchGameMode(args[0]);
                    player = (Player) sender.getSender();
                }
            }
        }

        if(gamemode == null) {
            MessageHandler.INSTANCE.build("module.essentials.gamemode.invalid.message")
                    .placeholder(new Placeholder("gamemodes", Arrays.stream(GameMode.values())
                            .map(game -> game.toString().toLowerCase())
                            .collect(Collectors.joining(MessageHandler.INSTANCE.get("module.essentials.gamemode.invalid.separator")))))
                    .send(sender);
            return;
        }

        if(player == null) {
            MessageHandler.INSTANCE.build("error.player.offline").send(sender);
            return;
        }

        player.setGameMode(gamemode);
        MessageHandler.INSTANCE.build("module.essentials.gamemode.user")
                .placeholder(new Placeholder("mode", gamemode.toString().toLowerCase()))
                .send(player);

        if(player != sender.getSender()) {
            MessageHandler.INSTANCE.build("module.essentials.gamemode.sender")
                    .placeholder(new Placeholder("mode", gamemode.toString().toLowerCase()),
                            new PlayerPlaceholder(player))
                    .send(sender);
        }
    }

    private GameMode matchGameMode(String modeString) {
        modeString = modeString.toLowerCase();
        final GameMode mode;

        if(modeString.equals("gmc")
                || modeString.equals("egmc")
                || modeString.contains("creat")
                || modeString.equals("1")
                || modeString.equals("c")) {
            mode = GameMode.CREATIVE;
        } else if(modeString.equals("gms")
                || modeString.equals("egms")
                || modeString.contains("survi")
                || modeString.equals("0")
                || modeString.equals("s")) {
            mode = GameMode.SURVIVAL;
        } else if(modeString.equals("gma")
                || modeString.equals("egma")
                || modeString.contains("advent")
                || modeString.equals("2")
                || modeString.equals("a")) {
            mode = GameMode.ADVENTURE;
        } else if(modeString.equals("gmsp")
                || modeString.equals("egmsp")
                || modeString.contains("spec")
                || modeString.equals("3")
                || modeString.equals("sp")) {
            mode = GameMode.SPECTATOR;
        } else {
            mode = null;
        }

        return mode;
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.gamemode");
    }
}
