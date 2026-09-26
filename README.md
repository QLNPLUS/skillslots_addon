# SkillSlots Addon

Forge 1.20.1 的 [SkillSlots](https://github.com/Snownee/SkillSlots)（2.1.1+forge）附属 mod。

## 功能

### 1. 槽位数量与 GUI（配置文件）

- **maxSlots**：修改最大槽位数量，最高支持 **8**（原版上限 4）。
- **layoutMode**：修改技能轮盘槽位排列方式。
  - `AUTO`（默认）：1-4 槽使用原版上下左右（菱形）排列，5-8 槽自动切换为交错双行。
  - `DIAMOND`：始终使用菱形排列（槽位 ≥5 时仍强制使用行式排列）。
  - `HORIZONTAL`：1-4 槽单行显示，5-8 槽双行显示。
- 行式排列规则：≤4 槽为单行；≥5 槽为双行——奇数个（2n-1）时上排 n 个、
  下排 n-1 个且交错（下排槽正好位于上排两槽之间）；偶数个（2n）时上下对称对齐。
- **hideLockedSlots**：是否隐藏未解锁槽位。
  - `false`（默认）：保持原版行为，未解锁槽位显示为“锁定”。
  - `true`：技能轮盘只显示已解锁槽位，布局自动按可见槽位收缩。
- **showItemTooltips**：鼠标悬停在技能槽上时显示物品 Tooltip（默认开启；
  原版 2.1.1 发布版不显示）。
- 物品自定义名称中的 `\n` 仅在技能按钮名称中换行显示，
  例如 `{"text":"第一行\n第二行"}`。

### 2. 触发事件开关（配置文件）

- **enableBlockRightClick**：关闭技能的方块右键事件。
- **enableEntityRightClick**：关闭技能的实体右键事件。
- **enableItemRightClick**：关闭技能的物品右键事件。

关闭某项后，技能使用到该类型目标时不会触发对应动作；若物品右键仍开启，
方块/实体事件被禁用时会按原逻辑回落到物品右键（与原版 PASS 回退行为一致）。

### 3. KubeJS GUI 打开/关闭事件

安装 KubeJS 后自动启用，事件组 `SkillSlotsEvents`（客户端脚本）：

| 事件 | 触发时机 |
| --- | --- |
| `useGuiOpened` | 技能轮盘打开时 |
| `useGuiClosed` | 技能轮盘关闭时 |
| `placeGuiOpened` | 槽位放置界面打开时 |
| `placeGuiClosed` | 槽位放置界面关闭时 |

示例（`kubejs/client_scripts/`）：

```js
SkillSlotsEvents.useGuiOpened(event => {
  // event.player / event.getPlayer() 客户端玩家
  // event.guiId == 'use' | 'place'
  // event.isUseGui() / event.isPlaceGui()
  console.log('skill wheel opened by', event.player.username)
})

SkillSlotsEvents.placeGuiClosed(event => {
  event.player.tell('放置界面已关闭')
})
```

### 客户端主动打开技能轮盘

客户端脚本（`kubejs/client_scripts/`）可以通过 `SkillSlotsGui` 主动打开/关闭技能轮盘：

```js
SkillSlotsGui.open()      // 打开技能轮盘 screen，成功返回 true
SkillSlotsGui.close()     // 关闭轮盘（若开着），成功返回 true
SkillSlotsGui.isOpen()    // 当前轮盘是否打开
```

示例：绑定自定义按键打开轮盘：

```js
KeyBindEvents.on('your_custom_key', event => {
  SkillSlotsGui.open()
})
```

### GUI 专用快捷键 + 槽位按键提示

- addon 单独注册了默认数字键 **1-8** 的“技能槽附属（仅界面）”按键；它们仅在
  技能轮盘打开时响应，可在按键设置中改绑，提示文字会跟随绑定变化。
- SkillSlots 原 mod 的“使用技能 1-8”是另一套独立按键，负责轮盘关闭时的全局触发，
  默认均未绑定，并且仅在 `enableClosedWheelShortcuts=true` 时实际使用。若不需要全局
  快捷键，保持“未绑定”即可；addon 的 GUI 按键即使与背包等游戏按键相同，也不会在
  未打开技能轮盘时处理该按键。
- 从旧版 addon 升级时，旧绑定仍会保存在原 SkillSlots 的“使用技能 1-8”中。请先将
  这些全局按键重置为“未绑定”，再在“技能槽附属（仅界面）”分组中设置 GUI 按键。
- 槽位上方显示当前绑定键的常规名称（如 `1`、`A`、`Ctrl`、`Shift`），
  玩家一眼就能看到该按哪个键。
- 想单独修改某个键在槽位上的显示文字（不影响全局按键名），在资源包或
  KubeJS 的 `assets/skillslots_addon/lang/zh_cn.json`（或 en_us.json）中添加：
  ```json
  { "key.skillslots_addon.display.1": "一号技能" }
  ```
  键名使用 GLFW 键名（如 `1`、`a`、`left.control`、`left.shift`）。

## 配置文件

首次启动后生成：

- `config/skillslots_addon-common.toml`（服务端生效）
  - `maxSlots`：1-8，默认 **8**
  - `beginnerSlots`：0-8，默认 0
  - `enableBlockRightClick` / `enableEntityRightClick` / `enableItemRightClick`：默认 true
- `config/skillslots_addon-client.toml`（客户端生效）
  - `layoutMode`：AUTO / DIAMOND / HORIZONTAL，默认 AUTO
  - `hideLockedSlots`：默认 false
  - `showItemTooltips`：默认 true
  - `enableClosedWheelShortcuts`：是否允许原 SkillSlots 按键在轮盘关闭时触发，默认 false

> 注意：`maxSlots`/`beginnerSlots` 与原版 SkillSlots 配置一样需要重启世界生效；
> 本 mod 的配置会覆盖 SkillSlots 自身配置里的 `slots.maxSlots`（原版最高只允许 4）。
> SkillSlots 依赖的 Kiwi 会在加载/重载自身配置时把值覆盖回去（上限 4），
> 本 mod 在每次设置/读取槽位前会强制重新应用自身的配置值，因此
> 槽位数量、同步和 `/skillslots` 指令都按本 mod 配置生效。
> 联机时建议服务端与客户端配置保持一致。

## 安装

把 `build/libs/skillslots_addon-1.0.20.jar` 放入 `mods` 目录，需要：

- Minecraft 1.20.1 + Forge 47.x
- SkillSlots 2.1.1+forge（必备，mods.toml 已声明依赖）
- Kiwi（SkillSlots 自身的前置）
- KubeJS 2001.6+（可选，仅 GUI 事件需要）

## 构建

需要 JDK 17，本机离线构建（ForgeGradle 6 + Gradle 8.7）：

```powershell
$env:JAVA_HOME='C:/Program Files/Java/jdk-17'
.\gradlew.bat build --offline
```

产物位于 `build/libs/skillslots_addon-1.0.20.jar`。

## 实现说明

- 通过 Mixin 将 `SkillSlotsHandler` 内硬编码的 `MAX_SLOTS = 4` 常量全部提升为 8
  （数组、BitSet、循环、序列化等），同时扩容原 mod 使用按键数组与轮盘按钮数组；
  addon 另行注册只在轮盘界面响应的 8 个快捷键。
- 事件开关通过在 `SimpleSkill.finishUsing` 中重定向方块/实体/物品三条右键调用实现。
- 轮盘布局在 `UseScreen.render` 中按配置重算每个按钮的位置；
  `hideLockedSlots=true` 时跳过未解锁按钮并收缩布局。
- Mixin 采用 `remap=false` 并直接使用运行时 SRG 名称，因此构建不依赖 MixinGradle，
  兼容本机离线构建环境。

## 已知说明

- 槽位放置界面（PlaceScreen）固定显示 8 个槽位位置（含锁定槽），
  布局会整体左移以容纳关闭按钮；背景仍使用原版漏斗贴图，槽位与贴图洞位不完全对齐，
  但不影响功能。
- 本附属针对 SkillSlots 2.1.1（Forge）编写，升级 SkillSlots 主版本后需同步更新 Mixin。

## 许可证与第三方依赖

本附属是 QLNPLUS 编写的独立项目，采用 **ARR（All Rights Reserved，保留所有权利）**，详见仓库中的 `LICENSE` 文件。

SkillSlots 是 Snownee 编写的第三方 mod，采用其自身的许可证。本附属不包含、不重新分发 SkillSlots 的 JAR、源码或资源；安装本附属时必须另行安装 SkillSlots。当前项目不是官方附属，也未声明得到 Snownee 背书。
