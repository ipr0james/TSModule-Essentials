package net.thenova.titan.spigot.module.essentials.listeners;

import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.essentials.handler.vanish.VanishHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

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
public final class VanishEvent implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if(event.isCancelled()
                || !VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if(event.isCancelled()
                || !VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if(event.isCancelled()
                || !(event.getEntity() instanceof Player)
                || !VanishHandler.INSTANCE.isVanished(event.getEntity().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if(player.isSneaking()
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || !VanishHandler.INSTANCE.isVanished(player.getUniqueId())
                || block == null) {
            return;
        }

        final BlockState blockState = block.getState();

        Inventory inventory = null;
        boolean fake = false;

        switch (block.getType()) {
            case TRAPPED_CHEST:
            case CHEST:
                Chest chest = (Chest) blockState;
                inventory = Bukkit.getServer().createInventory(player, chest.getInventory().getSize(), "Fake Chest");
                inventory.setContents(chest.getInventory().getContents());
                fake = true;
                break;
            case ENDER_CHEST:
                inventory = player.getEnderChest();
                break;
            case DISPENSER:
                inventory = ((Dispenser) blockState).getInventory();
                break;
            case HOPPER:
                inventory = ((Hopper) blockState).getInventory();
                break;
            case DROPPER:
                inventory = ((Dropper) blockState).getInventory();
                break;
            case FURNACE:
                inventory = ((Furnace) blockState).getInventory();
                break;
            case BREWING_STAND:
                inventory = ((BrewingStand) blockState).getInventory();
                break;
            case BEACON:
                return;
        }

        if (inventory != null) {
            event.setCancelled(true);
            if (fake) {
                MessageHandler.INSTANCE.build("module.essentials.vanish.open-inventory").send(player);
            }
            player.openInventory(inventory);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!VanishHandler.INSTANCE.isVanished(event.getWhoClicked().getUniqueId())
                || !event.getView().getTitle().equals("Fake Chest")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(event.isCancelled()
                || !VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if(!VanishHandler.INSTANCE.isVanished(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity smacked = event.getEntity();

        if (smacked instanceof Player) {
            Player player = (Player)smacked;
            if (VanishHandler.INSTANCE.isVanished(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }

        if ((event instanceof EntityDamageByEntityEvent)) {
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            Entity damager = ev.getDamager();
            Player player = null;

            if ((damager instanceof Player)) {
                player = (Player) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile)damager;

                if ((projectile.getShooter() != null) && ((projectile.getShooter() instanceof Player))) {
                    player = (Player) projectile.getShooter();
                }
            }

            if (player != null && VanishHandler.INSTANCE.isVanished(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

}
