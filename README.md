# 🏷️ Devathip Registry Mod (Nickname & Utilities System)

A robust and lightweight **Minecraft Fabric Mod (1.20.1)** designed to provide seamless custom nickname management, global nametag visibility control, and client-side utility features for both servers and players.

---

## ✨ Key Features

- **Custom Nickname Management:** Easily change your own nickname or manage other players' nicknames. Fully supports Unicode characters (e.g., Thai language, symbols) and formatting codes (`§a`, `§l`, etc.).
- **Global Nametag Control:** Dynamically hide or show player nametags server-wide via optimized client-server packet synchronization and rendering mixins.
- **Real-Time Name Reveal Shortcut:** Hold **`Left Alt`** to instantly reveal players' original GameProfile names across both world nametags and the Tab list.
- **Smart Tab-Completion & Quotes Support:** Advanced suggestion providers built on the Brigadier Command API to auto-complete online usernames and custom nicknames (automatically wraps nicknames containing spaces in quotes `"..."`).
- **Interactive Check Utility:** Inspect players' actual game profiles and custom aliases with click-to-copy/suggest chat buttons.

---

## 💡 Usage Guide & Examples

### 1. Handling Spaces & Quotation Marks (`"..."`)
When targeting or setting nicknames that contain spaces or special characters, encapsulate the name inside double quotation marks:
* Set a nickname with spaces:  
  `/nick "Test"`
* Target a player with spaces in their nickname:  
  `/nick player "Test" "Test123"`

### 2. Using Color Codes & Formatting
The mod supports standard Minecraft formatting codes using the `§` symbol:
* `/nick §aGreenName` (Green text)
* `/nick §c§lRedBold` (Red and Bold text)

---

## 📜 Command Reference

All commands can be accessed using aliases: `/nick`, `/name`, or `/customnick`.

| Command | Description | Permission Level |
| :--- | :--- | :--- |
| `/nick <new_name>` | Changes your personal custom nickname. | Everyone (`0`) |
| `/nick player <target> <new_name>` | Changes another player's nickname (Supports real names & active nicknames). | Admin (`2`) |
| `/nick reset` | Resets your personal nickname back to default. | Everyone (`0`) |
| `/nick reset <target>` | Resets a specific player's nickname back to default. | Admin (`2`) |
| `/nick reset all` | Resets nicknames for all players on the server. | Admin (`2`) |
| `/nick check <target>` | Inspects a player's real GameProfile name and active nickname. | Everyone (`0`) |
| `/nick on` / `/nick off` | Globally enables or disables player nametags server-wide. | Admin (`2`) |
| `/nick help` | Displays the complete interactive command guide. | Everyone (`0`) |

---

## ⌨️ Keybindings

- **`Left Alt` (Hold):** Dynamically toggles and reveals the original real usernames of all online players on your screen and Tab overlay (active when nametags are hidden via `/nick off`).

---

## ⚙️ Configuration & Data Storage

All player nickname data and global nametag visibility states are persistent across server restarts.

- **Data File Location:** `./dtrmc_nicknames.json` (Root directory of the server/world)
- **Automatic Synchronization:** Nickname changes are automatically broadcasted to all connected clients in real-time.

---

## 🏗️ Technical Stack & Architecture

- **Platform:** Minecraft Fabric Loader (`1.20.1`)
- **Command Engine:** Mojang Brigadier Command API
- **Networking:** Fabric API Custom Payload Packets for client-server state synchronization.
- **Rendering & Mixins:** 
  - `LivingEntityRendererMixin` / `PlayerEntityRenderer` (Nametag control & custom rendering)
  - `PlayerListEntryMixin` (Tab list overlay manipulation)
- **Data Persistence:** Local JSON storage (`dtrmc_nicknames.json`)

---

## 🚀 Installation

1. Ensure **Fabric Loader** and **Fabric API** for Minecraft `1.20.1` are installed.
2. Place the `dtrmc-registry.jar` file into the `mods` directory of both your **Server** and **Client**.
3. Launch your game or server.
