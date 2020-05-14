package net.thenova.titan.spigot.module.essentials.user;

import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.spigot.module.essentials.database.DatabaseEssentials;
import net.thenova.titan.spigot.users.user.module.UserModule;
import net.thenova.titan.spigot.util.ULocation;
import org.bukkit.Location;

import java.sql.SQLException;
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
public final class UserHomes extends UserModule {

    private final Map<String, Location> homes = new HashMap<>();

    @Override
    public final void load() {
        new SQLQuery(new DatabaseEssentials(), "SELECT `home_name`, `home_location` FROM `user_homes` WHERE `uuid` = ?",
                    this.user.getUUID().toString())
                .execute(res -> {
                    try {
                      while(res.next()) {
                          this.homes.put(res.getString("home_name"), ULocation.fromString(res.getString("home_location")));
                      }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[Module] [Essentials] [UserHomes] - Failed to load user homes for: {}",
                                this.user.getUUID().toString(), ex);
                    }
                });
    }

    public final boolean exists(final String name) {
        return this.homes.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(name));
    }

    public final void set(String name) {
        name = name.toLowerCase();
        final String location = ULocation.toString(this.user.getPlayer().getLocation());

        final String finalName = name;
        if(this.homes.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(finalName))) {
            new SQLQuery(new DatabaseEssentials(), "UPDATE `user_homes` SET `home_location` = ? WHERE (`uuid`, `home_name`)  (?, ?)",
                        location, this.user.getUUID().toString(), name)
                    .execute();
        } else {
            new SQLQuery(new DatabaseEssentials(), "INSERT INTO `user_homes` (`uuid`, `home_name`, `home_location`) VALUES (?, ?, ?)",
                        this.user.getUUID().toString(), name, location)
                    .execute();
        }

        this.homes.put(name, super.user.getPlayer().getLocation());
    }

    public final void delete(String name) {
        name = name.toLowerCase();

        final String finalName = name;
        if(this.homes.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(finalName))) {
            this.homes.remove(name);
            new SQLQuery(new DatabaseEssentials(), "DELETE FROM `user_homes` WHERE (`uuid`, `user_home`) = (?, ?)",
                        this.user.getUUID().toString(), name)
                    .execute();
        }
    }

    public final int amount() {
        return this.homes.size();
    }

    public final Map<String, Location> fetchAll() {
        return this.homes;
    }
}
