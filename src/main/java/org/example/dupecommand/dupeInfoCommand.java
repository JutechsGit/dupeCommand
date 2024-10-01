package org.example.dupecommand;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class dupeInfoCommand {

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        player.sendMessage(Text.literal("The current delay for using /dupe is ")
                        .formatted(Formatting.GOLD)
                        .append(Text.literal(String.valueOf(ConfigManager.config.DupeCommandCooldown)).formatted(Formatting.YELLOW))
                        .append(Text.literal(" milliseconds.").formatted(Formatting.GOLD)), false);
        return SINGLE_SUCCESS;
    }
}
