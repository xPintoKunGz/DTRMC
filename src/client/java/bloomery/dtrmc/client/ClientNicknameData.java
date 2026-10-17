package bloomery.dtrmc.client;

public class ClientNicknameData {
    private static boolean globalNametagsHidden = false;

    public static void setGlobalNametagsHidden(boolean hidden) {
        globalNametagsHidden = hidden;
    }

    public static boolean isGlobalNametagsHidden() {
        return globalNametagsHidden;
    }
}
