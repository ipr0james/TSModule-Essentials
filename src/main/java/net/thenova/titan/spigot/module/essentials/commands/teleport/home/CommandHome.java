package net.thenova.titan.spigot.module.essentials.commands.teleport.home;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.CommandTabComplete;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.user.UserHomes;
import net.thenova.titan.spigot.users.user.User;

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
@CommandUsage(
        min = 1,
        usage = "home <name>",
        description = "Teleport to a home."
)
public class CommandHome extends SpigotCommand<User> implements CommandPermission<User>, CommandTabComplete {

    public CommandHome() {
        super("home", "homes");
    }

    @Override
    public final void execute(final User user, final CommandContext context) {
        final String name = context.getArgument(0);
        if(name.contains(":") && user.hasPermission("titan.command.home.others")) {
            return;
        }

        final UserHomes homes = user.getModule(UserHomes.class);
        if (!homes.exists(name)) {
            MessageHandler.INSTANCE.build("module.essentials.home.exists")
                    .placeholder(new Placeholder("name", name))
                    .send(user);
            return;
        }
    }

    @Override
    public final boolean hasPermission(final User user) {
        return user.hasPermission("titan.command.home");
    }

    @Override
    public List<String> tabComplete(final User user, final String[] args) {
        return new ArrayList<>(user.getModule(UserHomes.class).fetchAll().keySet());
    }
}
