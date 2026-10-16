package bloomery.dtrmc;

import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;

@Addon(
        id = "dtrmc-voice-addon",
        name = "Devathip Voice Addon",
        version = "1.0.0",
        authors = {"Bloomery"}
)
public class DTRMCAddon implements AddonInitializer {

    private static PlasmoVoiceServer voiceServer;

    @Override
    public void onAddonInitialize(su.plo.voice.api.addon.AddonScope scope) {
        if (scope instanceof PlasmoVoiceServer) {
            voiceServer = (PlasmoVoiceServer) scope;
        }
    }

    public static PlasmoVoiceServer getVoiceServer() {
        return voiceServer;
    }
}