package bloomery.dtrmc.addon;

import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;

@Addon(
        id = "pv-dtrmc-registry-addon",
        name = "Plasmovoice x Devathip Registry Addon",
        version = "1.0.0",
        authors = {"Bloomery Network Team"}
)

public class DTRMCAddon implements AddonInitializer {

    private static PlasmoVoiceServer voiceServer;

    @Override
    public void onAddonInitialize() {
    }

    public static PlasmoVoiceServer getVoiceServer() {
        return voiceServer;
    }
}