package bloomery.dtrmc.mixin;

import bloomery.dtrmc.NicknameManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow public abstract GameProfile getProfile();

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
        GameProfile profile = this.getProfile();
        if (profile != null && profile.getId() != null) {
            String customNick = NicknameManager.getNickname(profile.getId());
            if (customNick != null && !customNick.isEmpty()) {
                cir.setReturnValue(Text.literal(customNick));
            }
        }
    }
}