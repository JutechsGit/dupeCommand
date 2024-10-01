package org.example.dupecommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class dupeCommand {

    // Cooldown map for players
    private static final Map<UUID, Long> cooldownMap = new HashMap<>();
    private static final long COOLDOWN_TIME = ConfigManager.config.DupeCommandCooldown; // 5 seconds in milliseconds

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();

        if (cooldownMap.containsKey(playerId)) {
            long lastUseTime = cooldownMap.get(playerId);
            if (currentTime - lastUseTime < COOLDOWN_TIME) {
                long timeLeft = (COOLDOWN_TIME - (currentTime - lastUseTime)) / 1000;
                player.sendMessage(Text.literal("Please wait " + timeLeft + " more seconds before using /dupe again.")
                        .formatted(Formatting.RED), false);
                return Command.SINGLE_SUCCESS;
            }
        }
        dupeInventory(player);

        cooldownMap.put(playerId, currentTime);

        return Command.SINGLE_SUCCESS;
    }

    private static void dupeInventory(ServerPlayerEntity player) {
        var inventory = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack currentStack = inventory.getStack(i);
            if (!currentStack.isEmpty()) {
                ItemStack copy = currentStack.copy();
                player.dropItem(copy, false);
            }
        }
        player.sendMessage(Text.literal("Your inventory has been duplicated!").formatted(Formatting.GREEN), false);
    }
}
