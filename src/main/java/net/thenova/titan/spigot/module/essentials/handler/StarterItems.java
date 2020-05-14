package net.thenova.titan.spigot.module.essentials.handler;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.spigot.util.lib.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
public final class StarterItems {

    private final Map<ArmorType, ItemStack> armor = new HashMap<>();
    private final Map<Integer, ItemStack> items = new HashMap<>();

    public StarterItems(final JSONFile file) {
        final JSON json = file.getJSON();

        Arrays.stream(ArmorType.values())
                .forEach(type -> {
            if(json.has(type.toString())) {
                this.armor.put(type, ItemBuilder.create(json.json(type.toString())).build());
            }
        });

        final JSONArray array = json.array("items");
        for(int i = 0; i < array.length(); i++) {
            final JSON item = array.json(i);
            this.items.put(item.integer("slot"), ItemBuilder.create(item).build());
        }
     }

    public void load(final Player player) {
        final PlayerInventory inventory = player.getInventory();

        this.armor.forEach((type, item) -> {
            switch(type) {
                case HELMET:
                    inventory.setHelmet(item);
                    break;
                case CHESTPLATE:
                    inventory.setChestplate(item);
                    break;
                case LEGGINGS:
                    inventory.setLeggings(item);
                    break;
                case BOOTS:
                    inventory.setBoots(item);
                    break;
            }
        });

        this.items.forEach(inventory::setItem);
    }

    public enum ArmorType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }
}
