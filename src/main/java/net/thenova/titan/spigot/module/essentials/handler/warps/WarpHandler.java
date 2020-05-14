package net.thenova.titan.spigot.module.essentials.handler.warps;

import net.thenova.titan.library.Titan;
import net.thenova.titan.spigot.users.user.User;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

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
public enum WarpHandler {
    INSTANCE;

    public static final UUID SYSTEM = UUID.fromString("0");

    private final Map<UUID, List<Warp>> warps = new HashMap<>();

    public void load(final File directory) {
        final Logger logger = Titan.INSTANCE.getDebug();
        logger.info("[Module] [Essentials] - Warp loading beginning...");

        final File[] files = directory.listFiles();
        if(files != null) {
            Arrays.stream(files).forEach(file -> {
                if(file.isDirectory()) {
                    final UUID uuid = UUID.fromString(file.getName());
                    final List<Warp> warps = new ArrayList<>();

                    final File[] playerWarps = file.listFiles();
                    if(playerWarps != null) {
                        Arrays.stream(playerWarps).forEach(f -> {
                            warps.add(new Warp(f));
                            logger.info("[WarpHandler] - Loaded warp '{}' for '{}'", file.getName(), uuid.toString());
                        });
                        this.warps.put(uuid, warps);
                    } else {
                        logger.info("[WarpHandler] - Warps directory {} was deleted {} as it was empty.",
                                file.getName(), file.delete() ? "successfully" : "unsuccessfully");
                    }
                }
            });
        } else {
            logger.info("[WarpHandler] - No warps have been loaded as warps directory was empty.");
        }
    }

    public boolean isWarp(final String name, UUID owner) {
        return this.warps.getOrDefault(owner, Collections.emptyList()).stream().anyMatch(warp -> warp.getName().equalsIgnoreCase(name));
    }

    public void createWarp(final User user, final String name, boolean system) {

    }

    public final Warp fetch(final UUID uuid, final String name) {
        return this.warps.getOrDefault(uuid, Collections.emptyList())
                .stream()
                .filter(warp -> warp.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
