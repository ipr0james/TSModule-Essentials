package net.thenova.titan.spigot.module.essentials.commands.teleport.home;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.module.essentials.user.UserHomes;
import net.thenova.titan.spigot.users.user.User;
import net.thenova.titan.spigot.util.ULocation;
import org.bukkit.Location;

import java.util.Locale;

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
public final class CommandSetHome extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandSetHome() {
        super("sethome");
    }

    @Override
    public final void execute(final User user, final CommandContext context) {
        final UserHomes homes = user.getModule(UserHomes.class);
        String name = "home";

        if(context.length() > 0) {
            name = context.getArgument(0).toLowerCase(Locale.ENGLISH);
        }

        final int max;
        if(homes.amount() >= (max = EssentialsHandler.INSTANCE.getHomeLimit(user)) && !homes.exists(name)) {
            MessageHandler.INSTANCE.build("module.essentials.sethome.maximum")
                    .placeholder(new Placeholder("max", max))
                    .send(user);
            return;
        }

        if(name.equals("bed") || UNumber.isInt(name) || name.contains(":")) {
            MessageHandler.INSTANCE.build("module.essentials.sethome.invalid-name")
                    .placeholder(new Placeholder("name", name))
                    .send(user);
            return;
        }

        final Location loc = ULocation.toCentre(user.getPlayer().getLocale());
        homes.set(name);
        MessageHandler.INSTANCE.build("module.essentials.sethome.complete")
                .placeholder(new Placeholder("name", name),
                        new Placeholder("world", loc.getWorld().getName()),
                        new Placeholder("x", loc.getBlockX()),
                        new Placeholder("y", loc.getBlockY()),
                        new Placeholder("z", loc.getBlockZ()))
                .send(user);
    }

    @Override
    public final boolean hasPermission(final User user) {
        return user.hasPermission("titan.command.sethome");
    }
}
