package bloomery.dtrmc.command;

import bloomery.dtrmc.NicknameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.UUID;

public class NickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("nick")
                .requires(source -> source.hasPermissionLevel(2)) // เฉพาะ OP

                // /nick <player> <name>
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                    String newName = StringArgumentType.getString(context, "name");

                                    // ตรวจสอบความยาวชื่อ (ไม่รวมสี §) ระหว่าง 3 ถึง 24 ตัวอักษร
                                    String plainName = newName.replaceAll("§[0-9a-fk-or]", "");
                                    if (plainName.length() < 3 || plainName.length() > 24) {
                                        context.getSource().sendError(Text.literal("§cชื่อต้องมีความยาวระหว่าง 3 ถึง 24 ตัวอักษร!"));
                                        return 0;
                                    }

                                    NicknameManager.setNickname(target.getUuid(), newName);
                                    context.getSource().sendFeedback(() -> Text.literal("§aเปลี่ยนชื่อของ " + target.getName().getString() + " เป็น " + newName + " สำเร็จ!"), true);
                                    return 1;
                                })
                        )
                )

                // /nick reset <player>
                .then(CommandManager.literal("reset")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                    NicknameManager.setNickname(target.getUuid(), null);
                                    context.getSource().sendFeedback(() -> Text.literal("§aรีเซ็ตชื่อของ " + target.getName().getString() + " กลับเป็นค่าเริ่มต้นแล้ว"), true);
                                    return 1;
                                })
                        )
                )

                // /nick off <player>
                .then(CommandManager.literal("off")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                    NicknameManager.setNametagHidden(target.getUuid(), true);
                                    context.getSource().sendFeedback(() -> Text.literal("§eซ่อน Nametag ของ " + target.getName().getString() + " แล้ว"), true);
                                    return 1;
                                })
                        )
                )

                // /nick on <player>
                .then(CommandManager.literal("on")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                    NicknameManager.setNametagHidden(target.getUuid(), false);
                                    context.getSource().sendFeedback(() -> Text.literal("§aแสดง Nametag ของ " + target.getName().getString() + " แล้ว"), true);
                                    return 1;
                                })
                        )
                )

                // /nick see <customName>
                .then(CommandManager.literal("see")
                        .then(CommandManager.argument("customName", StringArgumentType.string())
                                .executes(context -> {
                                    String query = StringArgumentType.getString(context, "customName");
                                    UUID uuid = NicknameManager.getOriginalByCustom(query);
                                    if (uuid == null) {
                                        context.getSource().sendError(Text.literal("§cไม่พบผู้เล่นที่ใช้ชื่อ Custom นี้!"));
                                        return 0;
                                    }
                                    ServerPlayerEntity found = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                    String realName = found != null ? found.getName().getString() : uuid.toString();
                                    context.getSource().sendFeedback(() -> Text.literal("§aชื่อจริง/เกมจริงของผู้เล่นนี้คือ: §e" + realName), false);
                                    return 1;
                                })
                        )
                )
        );
    }
}