package net.thenova.titan.spigot.module.essentials;

import net.thenova.titan.library.command.data.Command;
import net.thenova.titan.library.database.connection.IDatabase;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.essentials.commands.*;
import net.thenova.titan.spigot.module.essentials.commands.items.CommandEnchant;
import net.thenova.titan.spigot.module.essentials.commands.items.CommandItemName;
import net.thenova.titan.spigot.module.essentials.commands.items.CommandSkull;
import net.thenova.titan.spigot.module.essentials.commands.kits.CommandKit;
import net.thenova.titan.spigot.module.essentials.commands.message.CommandBroadcast;
import net.thenova.titan.spigot.module.essentials.commands.message.CommandMessage;
import net.thenova.titan.spigot.module.essentials.commands.teleport.home.CommandDelHome;
import net.thenova.titan.spigot.module.essentials.commands.teleport.home.CommandHome;
import net.thenova.titan.spigot.module.essentials.commands.teleport.home.CommandSetHome;
import net.thenova.titan.spigot.module.essentials.commands.teleport.spawn.CommandSetSpawn;
import net.thenova.titan.spigot.module.essentials.commands.teleport.spawn.CommandSpawn;
import net.thenova.titan.spigot.module.essentials.database.DatabaseEssentials;
import net.thenova.titan.spigot.module.essentials.database.tables.DBTableUserHomes;
import net.thenova.titan.spigot.module.essentials.database.tables.DBTableUserKitCooldowns;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.module.essentials.user.UserHomes;
import net.thenova.titan.spigot.module.essentials.user.UserKits;
import net.thenova.titan.spigot.plugin.IPlugin;
import net.thenova.titan.spigot.users.user.module.UserModule;
import org.bukkit.event.Listener;

import java.util.Arrays;
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
public class Essentials implements IPlugin {

    public String name() {
        return "Essentials";
    }

    public void load() {
        EssentialsHandler.INSTANCE.load();
    }

    @Override
    public void messages(final MessageHandler handler) {
        /* Item Commands */
        handler.add("module.essentials.hand-empty", "%prefix.error% You must be holding an item to do this.");

        // Enchant command
        handler.add("module.essentials.enchant.list.message", "%prefix.error% Invalid enchantment: &d%enchants%");
        handler.add("module.essentials.enchant.list.separator", "&8, &d");

        handler.add("module.essentials.enchant.invalid-level.numerical", "%prefix.error% The provided level value was not a number.");
        handler.add("module.essentials.enchant.invalid-level.unsafe", "%prefix.error% You do not have permission to apply unsafe enchantments.");

        handler.add("module.essentials.enchant.success", "%prefix.info% The enchantment &d%name% &7has been &d%status% &7from your item.");

        // Item name command
        handler.add("module.essentials.itemname.updated", "%prefix.info% The item name has been set to '&d%name%&7'");

        // Skull Command
        handler.add("module.essentials.skull.no-permission-others", "%prefix.error% You do not have permission to give yourself other peoples skulls.");

        handler.add("module.essentials.skull.receive.self", "%prefix.error% Your head has been added to your inventory");
        handler.add("module.essentials.skull.receive.other", "%prefix.info% &d%name%'s &7head has been added to your inventory");

        /* Kits */

        // Kit command
        handler.add("module.essentials.kit.list.no-kits", "%prefix.error% You do not have any kits.");
        handler.add("module.essentials.kit.list.message", "%prefix.info% Your kits: &d%kits%&7.");
        handler.add("module.essentials.kit.list.on-cooldown", "&m");
        handler.add("module.essentials.kit.list.separator", "&8, &d");

        handler.add("module.essentials.kit.not-exist", "%prefix.error% The provided kit does not exist.");
        handler.add("module.essentials.kit.no-permission", "%prefix.error% You do not have permission to use this kit.");
        handler.add("module.essentials.kit.on-cooldown", "%prefix.error% That kit is on cooldown for &c%cooldown%&7.");

        handler.add("module.essentials.kit.success.inventory", "%prefix.info% You kit has been added to your inventory.");
        handler.add("module.essentials.kit.success.overflow", "%prefix.error% Your inventory was full so your kit has been placed on the floor.");

        // CreateKit command

        /* Generic */

        // Gamemode command
        handler.add("module.essentials.gamemode.invalid.message", "%prefix.error% Invalid Gamemode provided. Valid Gamemodes: &c%gamemodes%");
        handler.add("module.essentials.gamemode.invalid.separator", "&7, &c");

        handler.add("module.essentials.gamemode.user", "%prefix.info% You gamemode has been to changed to &d%mode%");
        handler.add("module.essentials.gamemode.sender", "%prefix.info% Set gamemode &d%mode% &7for &d%player%&7.");

        // List command
        handler.add("module.essentials.list.header", "");
        handler.add("module.essentials.list.groups.format", "&d%group%&8: &7%names%");
        handler.add("module.essentials.list.groups.separator", "&8, &7");
        handler.add("module.essentials.list.footer", "");

        // Vanish
        handler.add("module.essentials.vanish.open-inventory", "%prefix.info% Opening chest silently. Can not edit.");

        handler.add("module.essentials.vanish.toggled", "%prefix.info% You have %status%.");
        handler.add("module.essentials.vanish.staff", "%prefix.info% &d%player% &7has %status%.");

        // Death Message
        handler.add("module.essentials.death-message.message", "&3* &b%player% &7has been slain by &b%killer%%weapon% &3*");
        handler.add("module.essentials.death-message.weapon", "&7using [&3&l%item%&7]");

        /* Teleport */

        // Home - DelHome command
        handler.add("module.essentials.delhome.others.exists", "%prefix.error% &c%player% &7does not have a home &8'&c%name%&8'&7.");
        handler.add("module.essentials.delhome.others.complete", "%prefix.info% &d%player%'s &7home &8'&d%name%&8' &7has been removed.");
        handler.add("module.essentials.delhome.exists", "%prefix.error% You do not have a home named &8'&d%name%&8'&7.");
        handler.add("module.essentials.delhome.complete", "%prefix.info% Your home &8'&d%name%&8' &7has been removed.");

        // Home - Home command
        // Home - SetHome command
        handler.add("module.essentials.sethome.maximum", "%prefix.error% You can only set a maximum of &c%max% &7homes.");
        handler.add("module.essentials.sethome.invalid-name", "%prefix.error% You cannot have a home with that name.");
        handler.add("module.essentials.sethome.complete", "%prefix.info% Your new home &d%name% &7has been set. &8(&d%world%&7, X: &d%x%, Y: &d%y%, Z: &d%z%&8)");

        // Spawn - SetSpawn command
        handler.add("module.essentials.teleport.setspawn.set", "%prefix.info% Spawn point has been set at your current location.");

        // Spawn - Spawn command
        handler.add("module.essentials.teleport.spawn.not-set", "%prefix.error% Spawn has not yet been set. Please contact an administrator.");
        handler.add("module.essentials.teleport.spawn.complete.player", "%prefix.info% You have been teleported to spawn&7.");
        handler.add("module.essentials.teleport.spawn.complete.other", "%prefix.info% %player% has been teleported to spawn.");


    }

    public void reload() {
        EssentialsHandler.INSTANCE.load();
    }

    public void shutdown() {

    }

    public IDatabase database() {
        return new DatabaseEssentials();
    }

    public List<DatabaseTable> tables() {
        return Arrays.asList(
                new DBTableUserHomes(),
                new DBTableUserKitCooldowns()
        );
    }

    public List<Listener> listeners() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public List<Command> commands() {
        return Arrays.asList(
                /* Item Commands */
                new CommandEnchant(),
                //new CommandGive(),
                //new CommandItem(),
                new CommandItemName(),
                new CommandSkull(),
                /* Kits */
                //new CommandCreateKit(),
                new CommandKit(),
                /* Messages */
                new CommandBroadcast(),
                new CommandMessage(),
                /* Teleport */

                /* Teleport - Home */
                new CommandDelHome(),
                new CommandHome(),
                new CommandSetHome(),

                /* Teleport - Spawn */
                new CommandSetSpawn(),
                new CommandSpawn(),

                /* Teleport - Warps */
                //new CommandDelWarp(),
                //new CommandSetWarp(),
                //new CommandWarp(),
                //new CommandWarps(),
                /* Generic */
                new CommandGamemode(),
                new CommandList(),
                new CommandMOTD(),
                new CommandRules(),
                new CommandVanish()

        );
    }

    public List<Class<? extends UserModule>> user() {
        return Arrays.asList(
                UserHomes.class,
                UserKits.class
        );
    }
}
