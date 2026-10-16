package bloomery.dtrmc.client;

import bloomery.dtrmc.NicknameManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DTRMCClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("dtrmc-client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("DTRMC: Client Initialized!");

		if (net.fabricmc.api.EnvType.CLIENT.equals(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType())) {
			net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
					NicknameManager.SYNC_PACKET_ID,
					(client, handler, buf, responseSender) -> {
						UUID uuid = buf.readUuid();
						boolean hasNick = buf.readBoolean();
						String nick = hasNick ? buf.readString() : null;
						boolean hidden = buf.readBoolean();

						client.execute(() -> {
							// บันทึกลง Client Cache เพื่อให้เรนเดอร์ถูกต้องทันที
							if (nick != null) {
								NicknameManager.getNicknamesMap().put(uuid, nick);
							} else {
								NicknameManager.getNicknamesMap().remove(uuid);
							}

							if (hidden) {
								NicknameManager.getHiddenSet().add(uuid);
							} else {
								NicknameManager.getHiddenSet().remove(uuid);
							}
						});
					}
			);
		}
	}
}