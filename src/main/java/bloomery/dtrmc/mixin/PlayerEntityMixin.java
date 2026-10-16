package bloomery.dtrmc.mixin;

import bloomery.dtrmc.NicknameManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        String customNick = NicknameManager.getNickname(player.getUuid());
        if (customNick != null && !customNick.isEmpty()) {
            cir.setReturnValue(Text.literal(customNick));
        }
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void modifyName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        String customNick = NicknameManager.getNickname(player.getUuid());
        if (customNick != null && !customNick.isEmpty()) {
            cir.setReturnValue(Text.literal(customNick));
        }
    }

    @Inject(method = "shouldRenderName", at = @At("RETURN"), cancellable = true)
    private void hideNametag(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (NicknameManager.isNametagHidden(player.getUuid())) {
            cir.setReturnValue(false);
        }
    }
}