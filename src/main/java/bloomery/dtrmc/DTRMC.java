package bloomery.dtrmc;

import bloomery.dtrmc.command.NickCommand;
import bloomery.dtrmc.addon.DTRMCAddon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;
import su.plo.voice.api.server.PlasmoVoiceServer;

public class DTRMC implements ModInitializer {
    public static final String MOD_ID = "dtrmc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static net.minecraft.server.MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("DTRMC: Initializing DTRMC Mod");

        NicknameManager.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NickCommand.register(dispatcher);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
            NicknameManager.setServer(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER = null;
            NicknameManager.setServer(null);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NicknameManager.onPlayerJoin(handler.getPlayer());
        });

        if (FabricLoader.getInstance().isModLoaded("plasmovoice")) {
            try {
                PlasmoVoiceServer.getAddonsLoader().load(new DTRMCAddon());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static net.minecraft.server.MinecraftServer getServer() {
        return SERVER;
    }
}