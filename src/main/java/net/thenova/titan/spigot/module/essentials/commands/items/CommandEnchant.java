package net.thenova.titan.spigot.module.essentials.commands.items;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.users.user.User;
import net.thenova.titan.spigot.util.UValidate;
import net.thenova.titan.spigot.util.lib.Enchantments;
import net.thenova.titan.spigot.util.lib.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;
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
@CommandUsage(
        min = 0,
        usage = "enchant <name> [level]",
        description = "Enchant the item currently being held."
)
public final class CommandEnchant extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandEnchant() {
        super("enchant", "enchantment");
    }

    @Override
    public final void execute(User user, CommandContext context) {
        final ItemStack item;
        if(!UValidate.notNull(item = user.getPlayer().getItemInHand())) {
            MessageHandler.INSTANCE.build("module.essentials.hand-empty").send(user);
            return;
        }

        if(context.getArguments().length == 0) {
            MessageHandler.INSTANCE.build("module.essentials.enchant.list.message")
                    .placeholder(new Placeholder("enchants", Arrays.stream(Enchantment.values())
                            .map(enchant -> enchant.getName().toLowerCase(Locale.ENGLISH).replace("_", " "))
                            .collect(Collectors.joining(MessageHandler.INSTANCE.get("module.essentials.enchant.list.separator")))))
                    .send(user);
            return;
        }

        final int level;
        if(context.getArguments().length > 1) {
            try {
                level = Integer.parseInt(context.getArgument(1));
            } catch (NumberFormatException ex) {
                MessageHandler.INSTANCE.build("module.essentials.enchant.invalid-level.numerical").send(user);
                return;
            }
        } else {
            level = 1;
        }

        final Enchantment enchant = Enchantments.getByName(context.getArgument(0));

        if(!user.hasPermission("titan.command.enchant.unsafe") && level > enchant.getMaxLevel()) {
            MessageHandler.INSTANCE.build("module.essentials.enchant.invalid-level.unsafe")
                    .placeholder(new Placeholder("max", enchant.getMaxLevel()))
                    .send(user);
            return;
        }

        ItemBuilder.create(item).enchant(enchant, level).build();
        MessageHandler.INSTANCE.build("module.essentials.enchant.success")
                .placeholder(new Placeholder("status", level == 0 ? "add" : "removed"),
                        new Placeholder("name", enchant.getName().toLowerCase(Locale.ENGLISH).replace("_", " ")))
                .send(user);
    }

    @Override
    public final boolean hasPermission(User user) {
        return user.hasPermission("titan.command.enchant");
    }
}
