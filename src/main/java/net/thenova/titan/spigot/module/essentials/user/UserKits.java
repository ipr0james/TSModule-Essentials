package net.thenova.titan.spigot.module.essentials.user;

import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.spigot.module.essentials.database.DatabaseEssentials;
import net.thenova.titan.spigot.module.essentials.handler.kits.Kit;
import net.thenova.titan.spigot.users.user.module.UserModule;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public final class UserKits extends UserModule {

    private final Map<String, Long> nextUse = new HashMap<>(); // Name lowerCase, Long timeMillis

    @Override
    public final void load() {
        new SQLQuery(new DatabaseEssentials(),"SELECT `kit_name`, `kit_cooldown` FROM `user_kit_cooldowns` WHERE `uuid` = ?", super.user.getUUID())
                .execute(res -> {
                    try {
                        while(res.next()) {
                            final String name = res.getString("kit_name");
                            final long time = res.getLong("kit_cooldown");

                            if(time < System.currentTimeMillis()) {
                                this.remove(name);
                            } else {
                                this.nextUse.put(name, time);
                            }
                        }
                    } catch (final SQLException ex) {
                        Titan.INSTANCE.getLogger().info("[Module-Essentials] [UserKits] - Failed to load user kit cooldowns", ex);
                    }
                });
    }

    /**
     * Return the cooldown status of the provided {@param net.thenova.titan.spigot.module.essentials.handler.kits.Kit}
     *
     * @param kit - Kit provided
     * @return - Return whether the Kit is currently on cooldown.
     */
    public final boolean onCooldown(final Kit kit) {
        final String name = kit.getName().toLowerCase();
        if(!this.nextUse.containsKey(name)) {
            return false;
        }

        if(this.nextUse.get(name) > System.currentTimeMillis()) {
            return true;
        } else {
            this.nextUse.remove(name);
            this.remove(name);
            return false;
        }
    }

    /**
     * Return the cooldown duration of the provided {@param net.thenova.titan.spigot.module.essentials.handler.kits.Kit}
     *
     * @param kit - Kit provided
     * @return - Return the cooldown, or 0L if the kit is not currently on cooldown
     */
    public final long getCooldown(final Kit kit) {
        final String name = kit.getName().toLowerCase();
        if(!this.nextUse.containsKey(name)) {
            return 0;
        }

        if(this.nextUse.get(name) > System.currentTimeMillis()) {
            return this.nextUse.get(name);
        } else {
            this.nextUse.remove(name);
            this.remove(name);
            return 0;
        }
    }

    /**
     * Add the provided {@param net.thenova.titan.spigot.module.essentials.handler.kits.Kit} to the list of cooldown kits
     *
     * @param kit - Kit provided
     */
    public final void cooldown(final Kit kit) {
        final String name = kit.getName().toLowerCase();
        final long cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(kit.getCooldown());

        this.nextUse.put(kit.getName().toLowerCase(), cooldown);
        new SQLQuery(new DatabaseEssentials(),"INSERT INTO `user_kit_cooldowns` (`uuid`, `kit_name`, `kit_cooldown`) VALUES (?, ?, ?)",
                    this.user.getUUID().toString(), name, cooldown
                ).execute();
    }

    /**
     * Remove a kit from cooldown.
     *
     * @param name - Kit name being removed
     */
    private void remove(String name) {
        name = name.toLowerCase();
        this.nextUse.remove(name);
        new SQLQuery(new DatabaseEssentials(), "DELETE FROM `user_kit_cooldowns` WHERE (`uuid`, `kit_name`) = (?, ?)",
                    super.user.getUUID().toString(), name)
                .execute();
    }
}
