package bloomery.dtrmc.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTRMCClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("dtrmc-client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("DTR: Client Initialized!");
	}
}