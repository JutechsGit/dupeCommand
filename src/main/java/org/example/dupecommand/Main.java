package org.example.dupecommand;

import net.fabricmc.api.ModInitializer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerDupeCommand(dispatcher);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerDupeInfoCommand(dispatcher);
        });
    }


    private void registerDupeCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dupe")
                .executes(dupeCommand::execute));
    }

    private void registerDupeInfoCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dupeinfo")
                .executes(dupeInfoCommand::execute));
    }
}
