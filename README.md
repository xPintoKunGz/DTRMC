# 🛡️ Devathip Registry (Nickname & Utilities System)

A robust and lightweight **Minecraft Fabric Mod (1.20.1)** designed to provide seamless custom nickname management, global nametag visibility control, and client-side utility features for both servers and players.

---

## ✨ Key Features

- **Custom Nickname Management:** Easily change your own nickname or manage other players' nicknames. Fully supports Unicode characters (e.g., Thai language) and spaces using quotation marks (`"..."`).
- **Global Nametag Control:** Dynamically hide or show player nametags server-wide via optimized client-server packet synchronization and rendering mixins.
- **Real-Time Name Reveal Shortcut:** Hold **`Left Alt`** to instantly reveal players' original GameProfile names across both world nametags and the Tab list.
- **Smart Tab-Completion:** Advanced suggestion providers built on the Brigadier Command API to auto-complete online usernames and custom nicknames.
- **Interactive Check Utility:** Inspect players' actual game profiles and custom aliases with click-to-copy/suggest chat buttons.

---

## 📜 Command Reference

All commands can be accessed using aliases: `/nick`, `/name`, or `/customnick`.

| Command | Description | Permission Level |
| :--- | :--- | :--- |
| `/nick <new_name>` | Changes your personal custom nickname. | Everyone (`0`) |
| `/nick player <target> <new_name>` | Changes another player's nickname. Supports spaces/Unicode. | Admin (`2`) |
| `/nick reset` | Resets your personal nickname back to default. | Everyone (`0`) |
| `/nick reset all` | Resets nicknames for all players on the server. | Admin (`2`) |
| `/nick reset <target>` | Resets a specific player's nickname. | Admin (`2`) |
| `/nick check <target>` | Inspects a player's real GameProfile name and active nickname. | Everyone (`0`) |
| `/nick on` / `/nick off` | Globally enables or disables player nametags server-wide. | Admin (`2`) |
| `/nick help` | Displays the complete interactive command guide. | Everyone (`0`) |

---

## ⌨️ Keybindings

- **`Left Alt` (Hold):** Dynamically toggles and reveals the original real usernames of all online players on your screen and Tab overlay.

---

## 🏗️ Technical Stack & Architecture

- **Platform:** Minecraft Fabric Loader (`1.20.1`)
- **Command Engine:** Mojang Brigadier Command API
- **Networking:** Fabric API Custom Payload Packets for client-server state synchronization.
- **Rendering & Mixins:** 
  - `LivingEntityRendererMixin` / `PlayerEntityRenderer` (Nametag control)
  - `PlayerListEntryMixin` (Tab list overlay manipulation)
- **Data Persistence:** Local JSON storage (`dtrmc_nicknames.json`)

---

## 🚀 Installation

1. Ensure **Fabric Loader** and **Fabric API** for Minecraft `1.20.1` are installed.
2. Place the `dtrmc-registry.jar` file into your server's and client's `mods` directory.
3. Restart your game or server.
