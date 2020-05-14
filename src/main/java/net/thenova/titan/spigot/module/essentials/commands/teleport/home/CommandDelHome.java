package net.thenova.titan.spigot.module.essentials.commands.teleport.home;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.CommandTabComplete;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.user.UserHomes;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;

import java.util.ArrayList;
import java.util.List;
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
@CommandUsage(
        min = 1,
        usage = "delhome <home>",
        description = "Delete one of your homes"
)
public final class CommandDelHome extends SpigotCommand<User> implements CommandPermission<User>, CommandTabComplete {

    public CommandDelHome() {
        super("delhome");
    }

    @Override
    public final void execute(final User user, final CommandContext context) {
        if(context.getArgument(0).split(":").length > 1) {
            if(!user.hasPermission("titan.command.delhome.others")) {
                MessageHandler.INSTANCE.build("error.command.permission").send(user);
            } else {
                final String[] args = context.getArgument(0).split(":");
                final User other = UserHandler.INSTANCE.getUser(args[0]);
                final String name = args[1].toLowerCase(Locale.ENGLISH);

                if(other == null) {
                    MessageHandler.INSTANCE.build("error.player.exists").send(user);
                    return;
                }

                final UserHomes homes = other.getModule(UserHomes.class);
                if(!homes.exists(name)) {
                    MessageHandler.INSTANCE.build("module.essentials.delhome.others.exists")
                            .placeholder(new Placeholder("player", other.getName()), new Placeholder("name", name))
                            .send(user);
                    return;
                }

                homes.delete(name);
                MessageHandler.INSTANCE.build("module.essentials.delhome.others.complete")
                        .placeholder(new Placeholder("player", other.getName()), new Placeholder("name", name))
                        .send(user);
            }
            return;
        }

        final String name = context.getArgument(0).toLowerCase(Locale.ENGLISH);
        final UserHomes homes = user.getModule(UserHomes.class);
        if (!homes.exists(name)) {
            MessageHandler.INSTANCE.build("module.essentials.delhome.exists")
                    .placeholder(new Placeholder("name", name))
                    .send(user);
            return;
        }

        homes.delete(name);
        MessageHandler.INSTANCE.build("module.essentials.delhome.complete")
                .placeholder(new Placeholder("name", name))
                .send(user);
    }

    @Override
    public final boolean hasPermission(final User user) {
        return user.hasPermission("titan.command.delhome");
    }

    @Override
    public List<String> tabComplete(User user, String[] strings) {
        return new ArrayList<>(user.getModule(UserHomes.class).fetchAll().keySet());
    }
}
