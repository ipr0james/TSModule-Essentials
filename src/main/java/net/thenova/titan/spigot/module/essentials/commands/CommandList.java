package net.thenova.titan.spigot.module.essentials.commands;

import net.milkbowl.vault.permission.Permission;
import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.essentials.handler.EssentialsHandler;
import net.thenova.titan.spigot.module.essentials.handler.vanish.VanishHandler;
import net.thenova.titan.spigot.plugin.users.modules.UserValidation;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.util.UVault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public final class CommandList extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public CommandList() {
        super("list", "who");
    }

    public void execute(SpigotSender sender, CommandContext context) {
        final Map<String, List<String>> listGroups = EssentialsHandler.INSTANCE.getListGroups();
        final Map<String, List<String>> players = new HashMap<>();
        final Permission permissions = UVault.getPermission();

        UserHandler.INSTANCE.getOnlineUsers().stream()
                .filter(user -> !user.getModule(UserValidation.class).contains(VanishHandler.VANISH_KEY))
                .forEach(user -> listGroups.forEach((name, groups) -> {
                    if(groups.contains(permissions.getPrimaryGroup(user.getPlayer()))) {
                        if(players.containsKey(name)) {
                            players.get(name).add(user.getName());
                        } else {
                            List<String> names = new ArrayList<>();
                            names.add(user.getName());
                            players.put(name, names);
                        }
                    } else {
                        if(players.containsKey("default")) {
                            players.get("default").add(user.getName());
                        } else {
                            List<String> names = new ArrayList<>();
                            names.add(user.getName());
                            players.put("default"   , names);
                        }
                    }
                }));

        final String separator = MessageHandler.INSTANCE.get("module.essentials.list.groups.separator");
        final StringBuilder builder = new StringBuilder(MessageHandler.INSTANCE.get("module.essentials.list.header"));

        listGroups.keySet().stream()
                .filter(players::containsKey)
                .forEach(group -> {
            builder.append(MessageHandler.INSTANCE.get("module.essentials.list.groups.format")
                        .replace("%group%", group)
                        .replace("%names%", String.join(separator, players.get(group))))
                    .append("\n");
        });

        builder.append(MessageHandler.INSTANCE.get("module.essentials.list.footer"));

        MessageHandler.INSTANCE.message(builder.toString())
                .placeholder(new Placeholder("%total_players%",
                        UserHandler.INSTANCE.getOnlineUsers().stream()
                                .filter(user -> !user.getModule(UserValidation.class).contains(VanishHandler.VANISH_KEY))
                                .count()))
                .send(sender);
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.list");
    }
}
