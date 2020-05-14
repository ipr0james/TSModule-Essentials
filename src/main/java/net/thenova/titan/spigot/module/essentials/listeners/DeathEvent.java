package net.thenova.titan.spigot.module.essentials.listeners;

import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.util.UValidate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
public final class DeathEvent implements Listener {

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer;

        if((killer = player.getKiller()) == null
            || !EssentialsHandler.INSTANCE.getConfig().get("death-message.enabled", Boolean.class)) {
            return;
        }

        @SuppressWarnings("deprecation") final ItemStack item = killer.getItemInHand();
        @SuppressWarnings("ConstantConditions") final String weapon = UValidate.notNull(item)
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                    ? MessageHandler.INSTANCE.build("module.essentials.death-message.weapon")
                        .placeholder(new Placeholder("item", item.getItemMeta().getDisplayName()))
                        .getMessage() : "";

        MessageHandler.INSTANCE.build("module.essentials.death-message.message")
                .placeholder(new Placeholder("weapon", weapon),
                        new PlayerPlaceholder(player.getName()),
                        new Placeholder("killer", killer.getName()))
                .broadcast();
    }
}
