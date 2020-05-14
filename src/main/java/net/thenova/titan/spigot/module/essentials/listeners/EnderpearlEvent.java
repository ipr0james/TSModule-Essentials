package net.thenova.titan.spigot.module.essentials.listeners;

import de.arraying.kotys.JSON;
import net.thenova.titan.library.util.cooldown.Cooldown;
import net.thenova.titan.spigot.data.compatability.model.CompMaterial;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.util.UValidate;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
public final class EnderpearlEvent implements Listener {

    private static final String COOLDOWN_KEY = "enderpearl_cd";

    private final boolean enabled;
    private final int duration;
    private final String message;

    public EnderpearlEvent() {
        final JSON config = EssentialsHandler.INSTANCE.getConfig().getJSON().json("enderpearl-.cooldown");

        this.enabled = config.bool("enabled");
        this.duration = config.integer("duration");
        this.message = config.string("message");
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if(!this.enabled
                || !UValidate.notNull(player.getItemInHand())
                || player.getItemInHand().getType() != Material.ENDER_PEARL
                || (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK)
                || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getClickedBlock() != null && !player.isSneaking()) {
            if (CompMaterial.fromMaterial(event.getClickedBlock().getType()).isInteractable()) {
                return;
            }
        }

        if(!Cooldown.inCooldown(player.getUniqueId().toString(), COOLDOWN_KEY)) {
            new Cooldown(player.getUniqueId().toString(), COOLDOWN_KEY, this.duration);
        } else {
            int timeLeft = (int) Cooldown.get(player.getUniqueId().toString(), COOLDOWN_KEY);
            event.setCancelled(true);
            player.updateInventory();

            MessageHandler.INSTANCE.message(this.message)
                    .placeholder(new Placeholder("time", timeLeft),
                        new Placeholder("value", "second" + (timeLeft > 1 ? "s" : "")))
            .send(player);
        }
    }
}
