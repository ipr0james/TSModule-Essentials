package net.thenova.titan.spigot.module.essentials.commands.teleport.spawn;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.module.essentials.handler.warps.Warp;
import net.thenova.titan.spigot.module.essentials.handler.warps.WarpHandler;
import net.thenova.titan.spigot.users.user.User;
import net.thenova.titan.spigot.util.lib.DelayedTeleport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
public final class CommandSpawn extends SpigotCommand<SpigotSender> implements CommandPermission<User> {

    public CommandSpawn() {
        super("spawn");
    }

    @Override
    public void execute(final SpigotSender sender, final CommandContext context) {
        final Warp warp = WarpHandler.INSTANCE.fetch(WarpHandler.SYSTEM, "spawn");

        if(warp == null) {
            MessageHandler.INSTANCE.build("module.essentials.teleport.spawn.not-set")
                    .send(sender);
            return;
        }

        if(context.length() > 0 && sender.hasPermission("titan.command.spawn.others")) {
            final Player player = Bukkit.getPlayer(context.getArgument(0));

            if(player == null) {
                MessageHandler.INSTANCE.build("error.player.offline").send(sender);
                return;
            }

            DelayedTeleport.create(player, warp.getLocation())
                    .queue(() -> {
                        MessageHandler.INSTANCE.build("module.essentials.teleport.spawn.complete.player")
                                .send(player);

                        MessageHandler.INSTANCE.build("module.essentials.teleport.spawn.complete.other")
                                .placeholder(new PlayerPlaceholder(player))
                                .send(sender);
                    });
        } else {
            if(!(sender.getSender() instanceof Player)) {
                MessageHandler.INSTANCE.build("error.usage.length")
                        .placeholder(new Placeholder("usage", "spawn <player>"))
                        .send(sender);
                return;
            }

            DelayedTeleport.create((Player) sender.getSender(), warp.getLocation())
                    .bypass("titan.command.spawn.bypass")
                    .delay(EssentialsHandler.INSTANCE.getConfig().get("teleport.spawn", Integer.class))
                    .queue(() -> {
                        MessageHandler.INSTANCE.build("module.essentials.teleport.spawn.complete.player")
                                .send(sender);
                    });

        }
    }

    @Override
    public boolean hasPermission(User user) {
        return user.hasPermission("titan.command.spawn");
    }
}
