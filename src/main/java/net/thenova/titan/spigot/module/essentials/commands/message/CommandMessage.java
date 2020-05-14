package net.thenova.titan.spigot.module.essentials.commands.message;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import org.bukkit.Bukkit;

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
        min = 2,
        usage = "message <player> <message>",
        description = "Send a message to a specific user."
)
public final class CommandMessage extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public CommandMessage() {
        super("message", "msg", "m", "whisper", "w");
    }

    public void execute(SpigotSender sender, CommandContext context) {
        if(Bukkit.getPlayer(context.getArgument(0)) == null) {
            MessageHandler.INSTANCE.build("error.player.offline").send(sender);
            return;
        }

        final StringBuilder builder = new StringBuilder();
        final String[] args = context.getArguments();
        for(int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        MessageHandler.INSTANCE.message(builder.toString()).send(Bukkit.getPlayer(context.getArgument(0)));
    }

    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.message");
    }
}