package bloomery.dtrmc.client.mixin.plasmovoice;

import bloomery.dtrmc.NicknameManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.plo.slib.api.chat.component.McTextComponent;
import su.plo.voice.api.client.audio.line.ClientSourceLine;
import su.plo.voice.proto.data.audio.source.DirectSourceInfo;
import su.plo.voice.proto.data.audio.source.PlayerSourceInfo;
import su.plo.voice.proto.data.audio.source.SourceInfo;

import java.util.UUID;

@Pseudo
@Mixin(targets = "su.plo.voice.client.render.voice.OverlayRenderer", remap = false)
public abstract class PlasmoVoiceRealtimeMixin {

    @Inject(
            method = "getSourceSenderName",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void dtrmc$forceRealtimeSenderName(SourceInfo sourceInfo, ClientSourceLine line, CallbackInfoReturnable<McTextComponent> cir) {
        UUID playerId = null;

        if (sourceInfo instanceof DirectSourceInfo direct) {
            if (direct.getSender() != null) {
                playerId = direct.getSender().getId();
            }
        } else if (sourceInfo instanceof PlayerSourceInfo psi) {
            if (psi.getPlayerInfo() != null) {
                playerId = psi.getPlayerInfo().getPlayerId();
            }
        }

        if (playerId != null) {
            String customNick = NicknameManager.getNickname(playerId);
            if (customNick != null && !customNick.isEmpty()) {
                String plainNick = customNick.replaceAll("§[0-9a-fk-or]", "");
                cir.setReturnValue(McTextComponent.Companion.literal(plainNick));
            }
        }
    }
}