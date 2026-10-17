package bloomery.dtrmc.command;

import bloomery.dtrmc.NicknameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.UUID;

public class NickCommand {

    private static final SuggestionProvider<ServerCommandSource> PLAYER_SUGGESTIONS = (context, builder) -> {
        ServerCommandSource source = context.getSource();
        if (source.getServer() != null) {
            for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                builder.suggest(player.getGameProfile().getName());

                String nick = NicknameManager.getNickname(player.getUuid());
                if (nick != null && !nick.isEmpty()) {
                    String plainNick = nick.replaceAll("§[0-9a-fk-or]", "").trim();
                    if (plainNick.contains(" ")) {
                        builder.suggest("\"" + plainNick + "\"");
                    } else {
                        builder.suggest(plainNick);
                    }
                }
            }
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] commandNames = {"name", "nick", "customnick"};

        for (String cmdName : commandNames) {
            dispatcher.register(CommandManager.literal(cmdName)
                    .then(CommandManager.literal("player")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.argument("target", StringArgumentType.string())
                                    .suggests(PLAYER_SUGGESTIONS)
                                    .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                String targetInput = StringArgumentType.getString(context, "target");
                                                ServerPlayerEntity foundTarget = findOnlinePlayer(context.getSource(), targetInput);

                                                if (foundTarget == null) {
                                                    context.getSource().sendError(Text.literal("§cไม่พบผู้เล่นจากชื่อหรือ Custom Nickname: " + targetInput));
                                                    return 0;
                                                }

                                                final ServerPlayerEntity targetPlayer = foundTarget;
                                                String newName = StringArgumentType.getString(context, "name");

                                                if (NicknameManager.isNameTaken(newName, targetPlayer.getUuid())) {
                                                    context.getSource().sendError(Text.literal("§cมีคนเอาชื่อนี้ไปแล้ว"));
                                                    return 0;
                                                }

                                                NicknameManager.setNickname(targetPlayer.getUuid(), newName);
                                                context.getSource().sendFeedback(() -> Text.literal("§aเปลี่ยนชื่อเล่นให้ " + targetPlayer.getGameProfile().getName() + " เป็น: " + newName), true);
                                                return 1;
                                            })
                                    )
                            )
                    )
            );

            LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal(cmdName)
                    .executes(context -> {
                        sendHelp(context.getSource());
                        return 1;
                    })
                    .then(CommandManager.literal("help")
                            .executes(context -> {
                                sendHelp(context.getSource());
                                return 1;
                            })
                    )
                    .then(CommandManager.literal("on")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                boolean changed = NicknameManager.setGlobalNametagsHidden(false);
                                if (!changed) {
                                    context.getSource().sendFeedback(() -> Text.literal("§e[DTRMC] Nametag เปิดใช้งานอยู่แล้ว"), false);
                                } else {
                                    context.getSource().sendFeedback(() -> Text.literal("§a[DTRMC] เปิดการแสดงผล Nametag ทั้งหมดเรียบร้อยแล้ว"), true);
                                }
                                return 1;
                            })
                    )
                    .then(CommandManager.literal("off")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                boolean changed = NicknameManager.setGlobalNametagsHidden(true);
                                if (!changed) {
                                    context.getSource().sendFeedback(() -> Text.literal("§e[DTRMC] Nametag ถูกซ่อนไว้อยู่แล้ว"), false);
                                } else {
                                    context.getSource().sendFeedback(() -> Text.literal("§c[DTRMC] ซ่อน Nametag ทั้งหมดในเซิร์ฟเวอร์เรียบร้อยแล้ว"), true);
                                }
                                return 1;
                            })
                    )
                    .then(CommandManager.literal("reset")
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player == null) {
                                    context.getSource().sendError(Text.literal("คำสั่งนี้ใช้ได้เฉพาะผู้เล่น"));
                                    return 0;
                                }
                                NicknameManager.setNickname(player.getUuid(), null);
                                context.getSource().sendFeedback(() -> Text.literal("§aรีเซ็ตชื่อเล่นของคุณกลับเป็นค่าเริ่มต้นแล้ว"), false);
                                return 1;
                            })
                            .then(CommandManager.literal("all")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> {
                                        for (UUID uuid : new java.util.HashSet<>(NicknameManager.getNicknamesMap().keySet())) {
                                            NicknameManager.setNickname(uuid, null);
                                        }
                                        context.getSource().sendFeedback(() -> Text.literal("§aรีเซ็ตชื่อเล่นของผู้เล่นทุกคนในเซิร์ฟเวอร์แล้ว"), true);
                                        return 1;
                                    })
                            )
                            .then(CommandManager.argument("target", StringArgumentType.string())
                                    .suggests(PLAYER_SUGGESTIONS)
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> {
                                        String targetInput = StringArgumentType.getString(context, "target");
                                        ServerPlayerEntity target = findOnlinePlayer(context.getSource(), targetInput);
                                        if (target == null) {
                                            context.getSource().sendError(Text.literal("§cไม่พบผู้เล่น: " + targetInput));
                                            return 0;
                                        }
                                        NicknameManager.setNickname(target.getUuid(), null);
                                        context.getSource().sendFeedback(() -> Text.literal("§aรีเซ็ตชื่อเล่นของ " + target.getGameProfile().getName() + " แล้ว"), true);
                                        return 1;
                                    })
                            )
                    )
                    .then(CommandManager.literal("check")
                            .then(CommandManager.argument("target", StringArgumentType.string())
                                    .suggests(PLAYER_SUGGESTIONS)
                                    .executes(context -> {
                                        String targetInput = StringArgumentType.getString(context, "target");
                                        ServerPlayerEntity target = findOnlinePlayer(context.getSource(), targetInput);
                                        if (target == null) {
                                            context.getSource().sendError(Text.literal("§cไม่พบผู้เล่น: " + targetInput));
                                            return 0;
                                        }
                                        String realName = target.getGameProfile().getName();
                                        String currentNick = NicknameManager.getNickname(target.getUuid());
                                        String displayNick = (currentNick != null && !currentNick.isEmpty()) ? currentNick : realName;

                                        MutableText msg = Text.literal("§e[DTRMC] ชื่อเล่น: §b" + displayNick + " §e| ชื่อจริงในเกม: §a" + realName);
                                        MutableText btn = Text.literal(" §e[คลิกดูชื่อจริง]")
                                                .styled(style -> style
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, realName))
                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("§fชื่อจริง: §a" + realName))));
                                        msg.append(btn);

                                        context.getSource().sendFeedback(() -> msg, false);
                                        return 1;
                                    })
                            )
                    )
                    // /nick <new_name> (เปลี่ยนชื่อตัวเอง)
                    .then(CommandManager.argument("name", StringArgumentType.greedyString())
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player == null) {
                                    context.getSource().sendError(Text.literal("คำสั่งนี้ใช้ได้เฉพาะผู้เล่น"));
                                    return 0;
                                }

                                String newName = StringArgumentType.getString(context, "name");
                                if (NicknameManager.isNameTaken(newName, player.getUuid())) {
                                    context.getSource().sendError(Text.literal("§cมีคนเอาชื่อนี้ไปแล้ว"));
                                    return 0;
                                }

                                NicknameManager.setNickname(player.getUuid(), newName);
                                context.getSource().sendFeedback(() -> Text.literal("§aเปลี่ยนชื่อเล่นเป็น: " + newName), false);
                                return 1;
                            })
                    );

            dispatcher.register(command);
        }
    }

    private static ServerPlayerEntity findOnlinePlayer(ServerCommandSource source, String inputName) {
        if (source.getServer() == null) return null;

        if (inputName.startsWith("\"") && inputName.endsWith("\"") && inputName.length() >= 2) {
            inputName = inputName.substring(1, inputName.length() - 1);
        }

        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(inputName);
        if (player != null) return player;

        String plainInput = inputName.replaceAll("§[0-9a-fk-or]", "").trim();
        for (ServerPlayerEntity p : source.getServer().getPlayerManager().getPlayerList()) {
            String nick = NicknameManager.getNickname(p.getUuid());
            if (nick != null) {
                String plainNick = nick.replaceAll("§[0-9a-fk-or]", "").trim();
                if (plainNick.equalsIgnoreCase(plainInput) || nick.equalsIgnoreCase(inputName)) {
                    return p;
                }
            }
        }
        return null;
    }

    private static void sendHelp(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§e--- DTRMC Nickname Commands ---"), false);
        source.sendFeedback(() -> Text.literal("§b/nick <ชื่อ> §f- เปลี่ยนชื่อเล่นของคุณเอง"), false);
        source.sendFeedback(() -> Text.literal("§b/nick player <ชื่อหรือ CustomNick> <ชื่อใหม่> §f- เปลี่ยนชื่อให้ผู้อื่น (Admin)"), false);
        source.sendFeedback(() -> Text.literal("§b/nick reset §f- ลบชื่อเล่นของคุณเอง"), false);
        source.sendFeedback(() -> Text.literal("§b/nick reset all|<ชื่อ> §f- รีเซ็ตชื่อ (Admin)"), false);
        source.sendFeedback(() -> Text.literal("§b/nick check <ชื่อ> §f- ตรวจสอบชื่อจริง"), false);
        source.sendFeedback(() -> Text.literal("§b/nick on/off §f- เปิด/ปิด Nametag ทั้งเซิร์ฟเวอร์ (Admin)"), false);
        source.sendFeedback(() -> Text.literal("§b/nick help §f- แสดงคำสั่งทั้งหมด"), false);
    }
}