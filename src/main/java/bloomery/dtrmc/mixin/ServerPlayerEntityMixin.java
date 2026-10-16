package bloomery.dtrmc.mixin;

import bloomery.dtrmc.NicknameManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void modifyPlayerListName(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String customNick = NicknameManager.getNickname(player.getUuid());
        if (customNick != null && !customNick.isEmpty()) {
            cir.setReturnValue(Text.literal(customNick));
        }
    }
}