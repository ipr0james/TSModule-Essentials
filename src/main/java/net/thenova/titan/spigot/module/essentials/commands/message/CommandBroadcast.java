package net.thenova.titan.spigot.module.essentials.commands.message;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;

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
        usage = "broadcast <message>",
        description = "Display a message to all online users."
)
public final class CommandBroadcast extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public CommandBroadcast() {
        super("broadcast", "bc");
    }

    public void execute(SpigotSender sender, CommandContext context) {
        final StringBuilder builder = new StringBuilder();
        for(String arg : context.getArguments()) {
            builder.append(arg).append(" ");
        }

        MessageHandler.INSTANCE.message(builder.toString()).broadcast();
    }

    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.broadcast");
    }
}
