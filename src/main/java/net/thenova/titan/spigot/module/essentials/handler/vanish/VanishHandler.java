package net.thenova.titan.spigot.module.essentials.handler.vanish;

import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.plugin.users.modules.UserValidation;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
public enum VanishHandler {
    INSTANCE;

    public static final String VANISH_PERMISSION = "titan.vanish.bypass";
    public static final String VANISH_KEY = "module_vanish";
    private final Set<UUID> players = new HashSet<>();

    /**
     * Toggle a players vanish status
     *
     * @param user - User being toggled
     */
    public void toggleVanish(User user) {
        final Player player = user.getPlayer();
        final boolean isVanished = this.players.contains(user.getUUID());
        final UserValidation validation = user.getModule(UserValidation.class);

        if(!isVanished) {
            player.getNearbyEntities(100, 100, 100)
                    .stream()
                    .filter(entity -> entity instanceof Creature
                            && ((Creature) entity).getTarget() == player)
                    .forEach(entity -> ((Creature) entity).setTarget(null));
            this.players.add(user.getUUID());
            validation.add("module_vanish");
        } else {
            this.players.remove(user.getUUID());
            validation.unset("module_vanish");
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p != player)
                .forEach(p -> {
                    if(isVanished) {
                        p.showPlayer(player);
                    } else {
                        if(!p.hasPermission(VANISH_PERMISSION)) {
                            p.hidePlayer(player);
                        }
                    }
                });

        MessageHandler.INSTANCE.build("module.essentials.vanish.toggled")
                .placeholder(new Placeholder("status", !isVanished ? "vanished" : "become visible"))
                .send(user);
        MessageHandler.INSTANCE.build("module.essentials.vanish.staff")
                .placeholder(new PlayerPlaceholder(user),
                        new Placeholder("status", !isVanished ? "vanished" : "became visible"))
                .send(UserHandler.INSTANCE.getOnlineUsers().stream()
                        .filter(p -> p.hasPermission(VANISH_PERMISSION) && !p.getUUID().equals(user.getUUID()))
                        .collect(Collectors.toList()));
    }

    /**
     * Check if the player is currently vanished
     *
     * @param uuid - Player uuid being checked
     * @return - return result
     */
    public boolean isVanished(final UUID uuid) {
        return this.players.contains(uuid);
    }

    /**
     * Remove a player from the vanished players store
     *
     * @param uuid - Player being removed
     */
    public void removePlayer(final UUID uuid) {
        this.players.remove(uuid);
    }

    /**
     * Get the Set<Player> of players currently vanished
     *
     * @return - Return Set<Player> of vanished players
     */

    public Set<UUID> getPlayers() {
        return this.players;
    }
}
