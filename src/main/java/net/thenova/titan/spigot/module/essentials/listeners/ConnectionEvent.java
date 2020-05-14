package net.thenova.titan.spigot.module.essentials.listeners;

import de.arraying.kotys.JSON;
import net.thenova.titan.spigot.TitanSpigot;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.module.essentials.handler.vanish.VanishHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

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
public final class ConnectionEvent implements Listener {

    @EventHandler
    public final void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Run last so all messages complete
        Bukkit.getScheduler().runTask(TitanSpigot.INSTANCE.getPlugin(), () -> {
            final JSON connection = EssentialsHandler.INSTANCE.getConfig().get("connection", JSON.class);

            if(!player.hasPlayedBefore()) {
                final JSON json = connection.json("first-join");

                if(json.bool("give-items")) {
                    EssentialsHandler.INSTANCE.getStarterItems().load(player);
                }

                final JSON message = json.json("message");
                if(message.bool("enabled")) {
                    EssentialsHandler.INSTANCE.incrementPlayerCount();
                    MessageHandler.INSTANCE.message(message.string("message"))
                            .placeholder(new PlayerPlaceholder(player),
                                    new Placeholder("player-count", EssentialsHandler.INSTANCE.getPlayerCount()))
                            .broadcast();
                    event.setJoinMessage(null);
                }
            } else {
                final JSON message = connection.json("join").json("message");
                if(message.bool("enabled")) {
                    event.setJoinMessage(MessageHandler.INSTANCE.message(message.string("message"))
                            .placeholder(new PlayerPlaceholder(player)).getMessage());
                } else {
                    event.setJoinMessage(null);
                }
            }

            final JSON title = connection.json("join").json("title");
            if(title.bool("enabled")) {
                //TODO
            }

            if(player.hasPermission("titan.essentials.motd")) {
                MessageHandler.INSTANCE.message(EssentialsHandler.INSTANCE.getMOTD())
                        .placeholder(new PlayerPlaceholder(player))
                        .send(player);
            }

            if(!player.hasPermission(VanishHandler.VANISH_PERMISSION)) {
                VanishHandler.INSTANCE.getPlayers().forEach(uuid -> {
                    final Player other = Bukkit.getPlayer(uuid);
                    if(other != null) {
                        player.hidePlayer(other);
                    }
                });
            }
        });
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if(VanishHandler.INSTANCE.isVanished(uuid)) {
            VanishHandler.INSTANCE.removePlayer(uuid);
        }
    }
}
