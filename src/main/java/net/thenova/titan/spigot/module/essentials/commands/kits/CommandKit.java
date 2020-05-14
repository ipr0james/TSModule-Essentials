package net.thenova.titan.spigot.module.essentials.commands.kits;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.handler.kits.Kit;
import net.thenova.titan.spigot.module.essentials.handler.kits.KitHandler;
import net.thenova.titan.spigot.module.essentials.user.UserKits;
import net.thenova.titan.spigot.users.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public final class CommandKit extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandKit() {
        super("kit", "kits");
    }

    @Override
    public void execute(User user, CommandContext context) {
        final int length = context.getArguments().length;

        final List<Kit> userKits = KitHandler.INSTANCE.getUserKits(user);
        final UserKits kitsModule = user.getModule(UserKits.class);

        if(length < 1) {
            if(userKits.isEmpty()) {
                MessageHandler.INSTANCE.build("module.essentials.kit.list.no-kits").send(user);
            } else {
                MessageHandler.INSTANCE.build("module.essentials.kit.list.message")
                        .placeholder(new Placeholder("kits", userKits.stream()
                                .map(kit -> (kitsModule.onCooldown(kit)
                                        ? MessageHandler.INSTANCE.get("module.essentials.kit.list.on-cooldown") : "")
                                        + kit.getName()).collect(Collectors.joining(MessageHandler.INSTANCE.get("module.essentials.kit.list.separator")))))
                        .send(user);
            }
            return;
        }

        if(!KitHandler.INSTANCE.isKit(context.getArgument(0))) {
            MessageHandler.INSTANCE.build("module.essentials.kit.not-exist").send(user);
            return;
        }

        final Kit kit = KitHandler.INSTANCE.getKitByName(context.getArgument(0));
        final Player player;

        if (length > 1 && user.hasPermission("titan.command.kit.others")) {
            if((player = Bukkit.getPlayer(context.getArgument(1))) == null) {
                MessageHandler.INSTANCE.build("error.player.offline").send(user);
                return;
            }
        } else {
            player = user.getPlayer();
            if(!user.hasPermission(KitHandler.KIT_PERMISSION + kit.getName().toLowerCase())) {
                MessageHandler.INSTANCE.build("module.essentials.kit.no-permission").send(user);
                return;
            }

            if(kitsModule.onCooldown(kit)) {
                MessageHandler.INSTANCE.build("module.essentials.kit.on-cooldown")
                        .placeholder(new Placeholder("cooldown",
                                UNumber.getTimeFull(TimeUnit.MILLISECONDS.toSeconds(kitsModule.getCooldown(kit)))))
                        .send(user);
                return;
            }
        }

        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = kit.getItems();

        user.getModule(UserKits.class).cooldown(kit);

        final Map<Integer, ItemStack> spew = inventory.addItem(items.toArray(new ItemStack[0]));
        spew.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

        if(spew.isEmpty()) {
            MessageHandler.INSTANCE.build("module.essentials.kit.success.inventory").send(user);
        } else {
            MessageHandler.INSTANCE.build("module.essentials.kit.success.overflow").send(user);
        }
    }

    @Override
    public boolean hasPermission(User user) {
        return user.hasPermission("titan.command.kit");
    }
}
