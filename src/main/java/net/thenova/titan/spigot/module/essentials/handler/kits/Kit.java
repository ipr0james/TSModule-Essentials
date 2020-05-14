package net.thenova.titan.spigot.module.essentials.handler.kits;

import de.arraying.kotys.JSON;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public final class Kit {

    private final String name;
    private final long cooldown;
    private final List<ItemStack> items = new ArrayList<>();

    public Kit(final File file) throws IOException {
        final JSON json = new JSON(file);

        this.name = json.string("name");
        this.cooldown = json.large("cooldown");
    }

    public final String getName() {
        return this.name;
    }
    public final long getCooldown() {
        return this.cooldown;
    }
    public final List<ItemStack> getItems() {
        return this.items;
    }
}
