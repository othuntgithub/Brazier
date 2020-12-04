package com.possible_triangle.brazier;

import com.possible_triangle.brazier.item.BrazierIndicator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class Brazier implements ModInitializer, ClientModInitializer {

    public Brazier() {

        ServerTickEvents.START_WORLD_TICK.register(world -> world.getPlayers().forEach(BrazierIndicator::playerTick));

        // TODO Config
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BrazierConfig.CLIENT_SPEC);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BrazierConfig.SERVER_SPEC);
    }

    @Override
    public void onInitialize() {
        Content.setup();
    }

    @Override
    public void onInitializeClient() {
        Content.clientSetup();
    }

}
