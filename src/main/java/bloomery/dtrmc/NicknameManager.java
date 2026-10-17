package bloomery.dtrmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class NicknameManager {
    public static final Identifier SYNC_PACKET_ID = new Identifier("dtrmc", "sync_nickname");

    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("dtrmc_nicknames.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<UUID, String> nicknames = new HashMap<>();
    private static Set<UUID> hiddenNametags = new HashSet<>();
    private static MinecraftServer SERVER;

    public static void setServer(MinecraftServer server) {
        SERVER = server;
    }

    public static Map<UUID, String> getNicknamesMap() {
        return nicknames;
    }

    public static Set<UUID> getHiddenSet() {
        return hiddenNametags;
    }

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                Data data = GSON.fromJson(reader, Data.class);
                if (data != null) {
                    if (data.nicknames != null) nicknames = data.nicknames;
                    if (data.hiddenNametags != null) hiddenNametags = data.hiddenNametags;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        Data data = new Data();
        data.nicknames = nicknames;
        data.hiddenNametags = hiddenNametags;
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setNickname(UUID uuid, String name) {
        if (name == null || name.trim().isEmpty()) {
            nicknames.remove(uuid);
        } else {
            nicknames.put(uuid, name.trim());
        }
        save();
        syncDataToAll(uuid);
    }

    public static String getNickname(UUID uuid) {
        return nicknames.get(uuid);
    }

    public static void setNametagHidden(UUID uuid, boolean hidden) {
        if (hidden) {
            hiddenNametags.add(uuid);
        } else {
            hiddenNametags.remove(uuid);
        }
        save();
        syncDataToAll(uuid);
    }

    public static boolean isNametagHidden(UUID uuid) {
        return hiddenNametags.contains(uuid);
    }

    public static UUID getOriginalByCustom(String customName) {
        for (Map.Entry<UUID, String> entry : nicknames.entrySet()) {
            String plain = entry.getValue().replaceAll("§[0-9a-fk-or]", "");
            if (entry.getValue().equalsIgnoreCase(customName) || plain.equalsIgnoreCase(customName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void syncDataToAll(UUID uuid) {
        String customName = nicknames.get(uuid);
        boolean isHidden = hiddenNametags.contains(uuid) || globalNametagsHidden;

        if (SERVER != null) {
            ServerPlayerEntity player = SERVER.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                if (customName == null || customName.isEmpty()) {
                    player.setCustomName(null);
                    player.setCustomNameVisible(false);
                } else {
                    player.setCustomName(Text.literal(customName));
                    // แก้ไขจุดนี้: เคารพสถานะซ่อน Nametag ทั้งแบบส่วนตัวและ Global
                    player.setCustomNameVisible(!isHidden);
                }
                SERVER.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
            }

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(uuid);
            buf.writeBoolean(customName != null && !customName.isEmpty());
            if (customName != null && !customName.isEmpty()) {
                buf.writeString(customName);
            }
            buf.writeBoolean(isHidden);

            for (ServerPlayerEntity onlinePlayer : SERVER.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(onlinePlayer, SYNC_PACKET_ID, buf);
            }
        }
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        String customName = nicknames.get(uuid);
        boolean isHidden = hiddenNametags.contains(uuid);

        if (customName != null && !customName.isEmpty()) {
            player.setCustomName(Text.literal(customName));
            player.setCustomNameVisible(!isHidden);
        }

        if (globalNametagsHidden && SERVER != null) {
            net.minecraft.scoreboard.ServerScoreboard scoreboard = SERVER.getScoreboard();
            net.minecraft.scoreboard.Team team = scoreboard.getTeam("dtrmc_nametags");
            if (team != null) {
                scoreboard.addPlayerToTeam(player.getGameProfile().getName(), team);
            }

            PacketByteBuf globalBuf = new PacketByteBuf(Unpooled.buffer());
            globalBuf.writeBoolean(globalNametagsHidden);
            ServerPlayNetworking.send(player, GLOBAL_NAMETAG_PACKET_ID, globalBuf);
        }

        for (Map.Entry<UUID, String> entry : nicknames.entrySet()) {
            UUID targetUuid = entry.getKey();
            String nick = entry.getValue();
            boolean hidden = hiddenNametags.contains(targetUuid);

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeUuid(targetUuid);
            buf.writeBoolean(true);
            buf.writeString(nick);
            buf.writeBoolean(hidden);

            ServerPlayNetworking.send(player, SYNC_PACKET_ID, buf);
        }
    }

    private static class Data {
        Map<UUID, String> nicknames;
        Set<UUID> hiddenNametags;
    }

    public static boolean isNameTaken(String name, UUID excludeUuid) {
        String plainNew = name.replaceAll("§[0-9a-fk-or]", "").trim();

        if (SERVER != null) {
            for (ServerPlayerEntity p : SERVER.getPlayerManager().getPlayerList()) {
                if (!p.getUuid().equals(excludeUuid)) {
                    if (p.getGameProfile().getName().equalsIgnoreCase(plainNew)) {
                        return true;
                    }
                }
            }
        }

        for (Map.Entry<UUID, String> entry : nicknames.entrySet()) {
            if (!entry.getKey().equals(excludeUuid)) {
                String plainExisting = entry.getValue().replaceAll("§[0-9a-fk-or]", "").trim();
                if (plainExisting.equalsIgnoreCase(plainNew)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final Identifier GLOBAL_NAMETAG_PACKET_ID = new Identifier("dtrmc", "sync_global_nametag");
    private static boolean globalNametagsHidden = false;

    public static boolean setGlobalNametagsHidden(boolean hidden) {
        if (globalNametagsHidden == hidden) {
            return false;
        }
        globalNametagsHidden = hidden;
        syncGlobalNametagStateToAll();
        return true;
    }

    public static boolean isGlobalNametagsHidden() {
        return globalNametagsHidden;
    }

    public static void syncGlobalNametagStateToAll() {
        if (SERVER != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(globalNametagsHidden);
            for (ServerPlayerEntity onlinePlayer : SERVER.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(onlinePlayer, GLOBAL_NAMETAG_PACKET_ID, buf);
            }
        }
    }
}