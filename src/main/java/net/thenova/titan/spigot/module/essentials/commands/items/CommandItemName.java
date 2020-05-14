package net.thenova.titan.spigot.module.essentials.commands.items;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.users.user.User;
import net.thenova.titan.spigot.util.UValidate;
import net.thenova.titan.spigot.util.lib.ItemBuilder;

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
        usage = "itemname <name>",
        description = "Change the name of the item currently being held."
)
public final class CommandItemName extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandItemName() {
        super("itemname");
    }

    public void execute(User sender, CommandContext context) {
        if(!UValidate.notNull(sender.getPlayer().getItemInHand())) {
            MessageHandler.INSTANCE.build("module.essentials.hand-empty").send(sender);
            return;
        }

        final String name = String.join(" ", context.getArguments());

        ItemBuilder.create(sender.getPlayer().getItemInHand()).name(name).build();
        MessageHandler.INSTANCE.build("module.essentials.itemname.updated")
                .placeholder(new Placeholder("name", name))
                .send(sender);
    }

    public boolean hasPermission(User sender) {
        return sender.hasPermission("titan.command.itemname");
    }
}