package org.example.dupecommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class dupeCommand {

    // Cooldown map for players
    private static final Map<UUID, Long> cooldownMap = new HashMap<>();
    private static final long COOLDOWN_TIME = ConfigManager.config.DupeCommandCooldown; // 5 seconds in milliseconds

    // Command count map for players
    private static final Map<UUID, Integer> commandCountMap = new HashMap<>();
    private static final File COMMAND_COUNT_FILE = new File("commandCounts.json");

    static {
        loadCommandCounts();
    }

    // Load command counts from file
    private static void loadCommandCounts() {
        if (!COMMAND_COUNT_FILE.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(COMMAND_COUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(":");
                if (split.length == 2) {
                    UUID playerId = UUID.fromString(split[0]);
                    int count = Integer.parseInt(split[1]);
                    commandCountMap.put(playerId, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save command counts to file
    private static void saveCommandCounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMMAND_COUNT_FILE))) {
            for (Map.Entry<UUID, Integer> entry : commandCountMap.entrySet()) {
                writer.write(entry.getKey().toString() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return Command.SINGLE_SUCCESS;
        }
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();

        // Cooldown check
        if (cooldownMap.containsKey(playerId)) {
            long lastUseTime = cooldownMap.get(playerId);
            if (currentTime - lastUseTime < COOLDOWN_TIME) {
                long timeLeft = (COOLDOWN_TIME - (currentTime - lastUseTime)) / 1000;
                player.sendMessage(Text.literal("Please wait " + timeLeft + " more seconds before using /dupe again.")
                        .formatted(Formatting.RED), false);
                return Command.SINGLE_SUCCESS;
            }
        }

        // Dupe the player's inventory
        dupeInventory(player);

        // Update cooldown
        cooldownMap.put(playerId, currentTime);

        // Increment command count for the player
        commandCountMap.put(playerId, commandCountMap.getOrDefault(playerId, 0) + 1);
        saveCommandCounts(); // Save updated counts to file

        // Notify the player how many times they've used the command
        int usageCount = commandCountMap.get(playerId);
        //player.sendMessage(Text.literal("You have used /dupe " + usageCount + " times.").formatted(Formatting.YELLOW), false);

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
