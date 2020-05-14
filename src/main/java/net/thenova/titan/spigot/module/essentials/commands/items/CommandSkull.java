package net.thenova.titan.spigot.module.essentials.commands.items;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.compatability.model.CompMaterial;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.users.user.User;
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
        min = 0,
        usage = "skull <name>",
        description = "Receive a skull of a given player."
)
public final class CommandSkull extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandSkull() {
        super("skull");
    }

    public void execute(User sender, CommandContext context) {
        if(!sender.hasPermission("titan.command.skull.others") && context.getArguments().length > 0) {
            MessageHandler.INSTANCE.build("module.essentials.skull.no-permission-others")
                    .send(sender);
            return;
        }

        final ItemBuilder builder = ItemBuilder.create(CompMaterial.SKELETON_SKULL);
        final String name = context.getArguments().length > 0 ? context.getArgument(0) : sender.getName();

        builder.skull(name);
        builder.name("Skull of " + name);

        sender.getPlayer().getInventory().addItem(builder.build());

        if(context.getArguments().length > 0) {
            MessageHandler.INSTANCE.build("module.essentials.skull.receive.other")
                    .placeholder(new Placeholder("name", name))
                    .send(sender);
        } else {
            MessageHandler.INSTANCE.build("module.essentials.skull.receive.self")
                    .send(sender);
        }
    }

    public boolean hasPermission(User sender) {
        return sender.hasPermission("titan.command.skull");
    }
}
