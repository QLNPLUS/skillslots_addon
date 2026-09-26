# SkillSlots Addon

SkillSlots Addon is a Forge 1.20.1 addon for [SkillSlots](https://github.com/Snownee/SkillSlots). It expands the skill wheel to support up to 8 slots, adds configurable layouts and right-click behavior, and provides optional KubeJS client GUI events.

## Features

### Up to 8 skill slots

- Raise the maximum number of SkillSlots slots from 4 to 8.
- Configure how many slots are unlocked for new players.
- Preserve slot data, synchronization, commands, and passive-skill scanning for all configured slots.

### Configurable skill wheel

- `AUTO`: the original diamond layout for 1-4 slots, then a two-row layout for 5-8 slots.
- `DIAMOND`: keep the original layout where possible.
- `HORIZONTAL`: use a centered row for 1-4 slots and two rows for 5-8 slots.
- Optionally hide locked slots and automatically compact the layout.
- Optionally show item tooltips when hovering a skill slot.
- Support line breaks in custom skill button names with `\n`.

### Right-click trigger switches

Independently enable or disable the following SkillSlots actions:

- Block right-click
- Entity right-click
- Item right-click

When a block or entity action is disabled, the normal item-use fallback remains available when item right-click is enabled.

### KubeJS client integration

When KubeJS is installed, the addon provides the `SkillSlotsEvents` client event group:

- `useGuiOpened`
- `useGuiClosed`
- `placeGuiOpened`
- `placeGuiClosed`

Client scripts can also use `SkillSlotsGui.open()`, `SkillSlotsGui.close()`, and `SkillSlotsGui.isOpen()` to control the skill wheel.

### GUI-only keybinds

The addon registers separate GUI-only keybinds for slots 1-8. They respond only while the SkillSlots wheel is open and do not replace SkillSlots' own global skill-use keybinds.

## Configuration

After the first launch, the following files are generated:

- `config/skillslots_addon-common.toml`
  - `maxSlots`: 1-8, default `8`
  - `beginnerSlots`: 0-8, default `0`
  - `enableBlockRightClick`: default `true`
  - `enableEntityRightClick`: default `true`
  - `enableItemRightClick`: default `true`
- `config/skillslots_addon-client.toml`
  - `layoutMode`: `AUTO`, `DIAMOND`, or `HORIZONTAL`
  - `hideLockedSlots`: default `false`
  - `showItemTooltips`: default `true`
  - `enableClosedWheelShortcuts`: default `false`

Changes to `maxSlots` and `beginnerSlots` require restarting the world. For multiplayer, keep the common configuration consistent with the server.

## Requirements

- Minecraft 1.20.1
- Forge 47.x
- [SkillSlots 2.1.1+forge](https://github.com/Snownee/SkillSlots)
- Kiwi, required by SkillSlots
- KubeJS 2001.6 or newer, optional for the KubeJS integration

## Installation

1. Install Forge 1.20.1.
2. Install SkillSlots and its required dependency Kiwi.
3. Put the SkillSlots Addon JAR into the `mods` folder.
4. Install KubeJS only if you need the optional client events or bindings.

SkillSlots is a required third-party dependency and is not bundled with this addon.

## Compatibility

This addon targets SkillSlots 2.1.1 for Forge. Its Mixins depend on SkillSlots' internal implementation, so a SkillSlots version change may require an addon update.

SkillSlots Addon is an independent, unofficial project by QLNPLUS and is not affiliated with or endorsed by Snownee.

## License

SkillSlots Addon is distributed under an **All Rights Reserved (ARR)** license. See the repository `LICENSE` file. SkillSlots is a separate project with its own license and distribution terms.
