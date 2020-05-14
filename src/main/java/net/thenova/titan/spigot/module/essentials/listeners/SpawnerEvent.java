package net.thenova.titan.spigot.module.essentials.listeners;

import de.arraying.kotys.JSON;
import net.thenova.titan.spigot.data.compatability.model.CompMaterial;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.util.UValidate;
import net.thenova.titan.spigot.util.lib.ItemBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
public final class SpawnerEvent implements Listener {

    private final boolean enabled;
    private final String name;

    public SpawnerEvent() {
        final JSON config = EssentialsHandler.INSTANCE.getConfig().getJSON().json("silk-spawners");

        this.enabled = config.bool("enabled");
        this.name = config.string("name");
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public final void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if(!this.enabled
                || event.isCancelled()
                || block.getType() != CompMaterial.SPAWNER.getMaterial()
                || !UValidate.notNull(player.getItemInHand())
                || !UValidate.isTool(UValidate.ToolType.PICKAXE, CompMaterial.fromMaterial(player.getItemInHand().getType()))
                || !(player.getItemInHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0)) {
            return;
        }

        event.setExpToDrop(0);

        block.getWorld().dropItemNaturally(block.getLocation(), this.build(((CreatureSpawner)block.getState()).getSpawnedType()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onEntityExplode(final EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!this.enabled
                || event.isCancelled()
                || entity instanceof EnderDragon) {
            return;
        }

        event.blockList().stream()
                .filter(block -> block.getType() == CompMaterial.SPAWNER.getMaterial())
                .forEach(block -> block.getWorld().dropItemNaturally(block.getLocation(), this.build(((CreatureSpawner)block.getState()).getSpawnedType())));
    }

    private ItemStack build(final EntityType type) {
        assert type.getEntityClass() != null;

        return ItemBuilder.create(CompMaterial.SPAWNER)
                .name(this.name.replace("%type%", type.getEntityClass().getName()))
                .asSpawner(type)
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onBlockPlace(final BlockPlaceEvent event) {
        if(event.isCancelled()
                || event.getBlockPlaced().getType() != CompMaterial.SPAWNER.getMaterial()
                || !UValidate.notNull(event.getItemInHand())) {
            return;
        }

        final CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        final EntityType type = spawner.getSpawnedType();

        spawner.setSpawnedType(type);
        spawner.update();
    }
}
