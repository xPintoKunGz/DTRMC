package bloomery.dtrmc.client.mixin;

import bloomery.dtrmc.NicknameManager;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin {
    @Shadow(remap = false)
    public abstract UUID getId();

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true, remap = false)
    private void dtrmc$modifyProfileName(CallbackInfoReturnable<String> cir) {
        UUID uuid = this.getId();
        if (uuid != null) {
            String customNick = NicknameManager.getNickname(uuid);
            if (customNick != null && !customNick.isEmpty()) {
                String plainNick = customNick.replaceAll("§[0-9a-fk-or]", "");
                cir.setReturnValue(plainNick);
            }
        }
    }
}