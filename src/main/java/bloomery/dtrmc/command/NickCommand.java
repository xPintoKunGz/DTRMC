package bloomery.dtrmc.command;

import bloomery.dtrmc.NicknameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class NickCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] commandNames = {"name", "nick", "customnick"};

        for (String cmdName : commandNames) {
            LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal(cmdName)
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        source.sendFeedback(() -> Text.literal("Usage: /name <new name>"), false);
                        return 1;
                    })
                    .then(CommandManager.argument("name", StringArgumentType.greedyString())
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();
                                // แก้ไขจาก getPlayerOrException() เป็น getPlayer() (หรือ getPlayerOrThrow() ใน 1.20.1)
                                ServerPlayerEntity player = source.getPlayer();
                                if (player == null) {
                                    source.sendFeedback(() -> Text.literal("This command can only be used by players."), false);
                                    return 0;
                                }

                                String newName = StringArgumentType.getString(context, "name");
                                NicknameManager.setNickname(player.getUuid(), newName);

                                source.sendFeedback(() -> Text.literal("Nickname changed to: " + newName), false);
                                return 1;
                            })
                    );

            dispatcher.register(command);
        }
    }
}