package bloomery.dtrmc;

import bloomery.dtrmc.command.NickCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTRMC implements ModInitializer {
    public static final String MOD_ID = "dtrmc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing DTRMC Mod");

        NicknameManager.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NickCommand.register(dispatcher);
        });
    }
}