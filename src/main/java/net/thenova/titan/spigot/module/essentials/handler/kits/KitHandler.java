package net.thenova.titan.spigot.module.essentials.handler.kits;

import net.thenova.titan.library.Titan;
import net.thenova.titan.spigot.users.user.User;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public enum KitHandler {
    INSTANCE;

    public static String KIT_PERMISSION = "titan.essentials.kit.";

    private final List<Kit> loadedKits = new ArrayList<>();

    public final void load(final File directory) {
        final Logger logger = Titan.INSTANCE.getDebug();
        logger.info("[Module] [Essentials] [KitHandler] - Kit loading beginning...");

        final File[] files = directory.listFiles();
        if(files != null) {
            Arrays.stream(files).forEach(file -> {
                try {
                    this.loadedKits.add(new Kit(file));
                } catch (IOException ex) {
                    final String message = "[Module] [Essentials] [KitHandler] - Failed to load kit file '{}' as file was not correctly formatted for JSON.";
                    logger.info(message, file.getName(), ex);
                    Titan.INSTANCE.getLogger().info(message, file.getName());
                }
            });
        } else {
            logger.info("[Module] [Essentials] [KitHandler] - No kits have been loaded as warps directory was empty.");
        }
    }

    public final boolean isKit(final String name) {
        return this.loadedKits.stream()
                .anyMatch(kit -> kit.getName().toLowerCase().equals(name.toLowerCase()));
    }

    public final Kit getKitByName(final String name) {
        return this.loadedKits.stream()
                .filter(kit -> kit.getName().toLowerCase().equals(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
    public final List<Kit> getLoadedKits() {
        return this.loadedKits;
    }
    public final List<Kit> getUserKits(final User user) {
        return this.loadedKits.stream()
                .filter(kit -> user.hasPermission(KIT_PERMISSION + kit.getName().toLowerCase()))
                .collect(Collectors.toList());
    }
}