# Arsenal_Graft 

简短状态
- 这是一个进行中的 Minecraft mod 框架，目标是通过“改造（modification）”系统动态扩展玩家能力与额外物品槽。当前已实现基于 JSON 的模板注册、手部（hand）动态物品槽、服务端权威的同步与基础 GUI。仍在完善中。

仓库主要目录（快速导览）
- src/main/java/io/github/dkjsiogu/arsenalgraft/
  - api/v3/ — 对外 API（ArsenalGraftAPI、ModificationManager、组件接口）
  - api/v3/loader/ — JSON 模板加载器（data/arsenalgraft/modifications）
  - client/ — 客户端 UI、Screen、Menu、渲染逻辑
  - network/ — 同步包（菜单同步、模板同步）
  - command/ — 开发用与测试命令（/arsena）
  - menu/、inventory/ — 容器与服务端权威数据实现
- src/main/resources/data/arsenalgraft/modifications/ — JSON 模板目录（示例：normal_hand.json）
- docs/ —（待补充）JSON schema 与使用说明

已实现的核心功能（摘要）
- JSON 驱动的改造模板加载：data/arsenalgraft/modifications/*.json
- 普通手（normal_hand）模板示例：每个实例增加 1 格手部物品槽，可叠加安装
- HandInventory：标准 Container/Menu 实现，支持 shift-left 快速转移、左/右键交互
- 服务端权威存储（HandInventoryData） + 客户端只读缓存，解决同步冲突
- /arsena 命令集（list/check/grant/remove/debug），并为改造 id 提供补全
- 清理：移除旧的 legacy ExtraHand/NormalHand Java 实现，统一为 JSON 注册

如何添加改造（两种方式）
- 推荐（数据驱动） — JSON
  - 在 `data/arsenalgraft/modifications/` 放入 JSON 文件（参考已有 `normal_hand.json`）。
  - 使用 F3+T 或 `/reload` 触发重载，或查看服务端日志确认加载。
- 可选（脚本） — KubeJS
  - 使用 KubeJS 在运行时调用提供的 KubeJS API 动态注册模板或触发事件。

常用开发/测试命令
- ./gradlew clean build
- F3+T 或 /reload （数据重载）
- /arsena list
- /arsena debug templates
- /arsena grant <mod> [player]
- /arsena remove <mod> [player]

短期计划（接下来要实现）
- 增量同步（减少全量包）与冲突抑制
- 完善 GUI（背景纹理、布局自适应、多实例展示）
- 文档：详细 JSON schema（docs/REGISTER_JSON.md）与示例
- CI：加入构建与数据加载验证（建议 GitHub Actions）

更新日志（简洁）
- Unreleased:
  - refactor: 统一改造模板注册为 JSON 驱动
  - feat(gui): 基于标准 Container 的 HandInventory + Screen（原生交互）
  - feat(inventory): 服务端权威 HandInventoryData 与客户端缓存机制
  - cli: 新增 `/arsena` 命令集与改造 id 补全
  - cleanup: 删除 legacy ExtraHand Java 实现，精简框架

贡献与联系
- 推荐分支策略：feature branch → PR → CI 验证 → 合并到 main
- 遇到同步或 UI 异常，请先在日志开启 DEBUG 并提供服务端与客户端相关片段，便## Arsenal_Graft — 开发者入门（面向继续开发者）

当前状态（重要）
- 本 mod 尚未完成：仓库保留了核心组件框架、API 与数据加载器，但仍有功能、文档与示例需要补全和验证。请把本仓库视为“进行中”项目。
- 你之前提到 `archive/` 用于本地参考；当前仓库里不包含第三方完整源码（archive 目录未上传），因此不要依赖 archive 中的文件来做编译或发布决策。

目标读者
- 希望继续实现/完善改造（modification）系统的开发者。本文聚焦如何在本地构建、运行和在代码层面继续开发。

必须项（环境）
- JDK 17+（或按项目 `gradle.properties`）。
- Gradle（项目自带 gradle wrapper）。
- 推荐 IDE：IntelliJ IDEA（支持 Gradle/Maven），或 VS Code + Java 插件。

快速构建与本地校验（PowerShell）
```powershell
# 在项目根目录执行：
./gradlew clean build      # 整体构建检查
./gradlew prepareRunClientCompile     # 准备用于客户端运行的 classpath
./gradlew prepareRunDataCompile       # 准备用于数据生成的 classpath
./gradlew prepareRunGameTestServerCompile
./gradlew prepareRunServerCompile
```

关键代码位置（先看这些文件）
- `src/main/java/io/github/dkjsiogu/arsenalgraft/api/v3/ArsenalGraftAPI.java` — 公共 API 门面（修改模型的增删查与同步事件）。
- `src/main/java/io/github/dkjsiogu/arsenalgraft/api/v3/modification/ModificationManager.java` — 注册/查找/管理 modification 的核心。 
- `src/main/java/io/github/dkjsiogu/arsenalgraft/api/v3/loader/JsonModificationLoader.java` — 数据驱动（JSON）loader。数据文件位置：`data/arsenalgraft/modifications/`。
- `src/main/java/io/github/dkjsiogu/arsenalgraft/api/v3/kubejs/ArsenalGraftKubeJSAPI.java` — KubeJS 集成点（若使用 KubeJS，按其文档书写脚本并调用此 API）。
- `src/main/java/.../client/` 与 `.../network/` — 客户端展示、GUI 与网络同步相关代码。

如何注册改造（两条路径）
- JSON（推荐做为数据驱动首选）：
	- 把 JSON 文件放入 `data/arsenalgraft/modifications/`，每个文件描述一个或多个 modification 模板。
	- `JsonModificationLoader` 在数据加载阶段读取并把模板注册到 `ModificationManager`。参考 `docs/REGISTER_JSON.md` 中的字段与示例。
- KubeJS（面向服务端脚本/数据工程师）：
	- 使用 `ArsenalGraftKubeJSAPI` 中的构建器 API 在脚本里注册模板或调用运行时 API。KubeJS 脚本会在 mod 加载阶段执行（取决于 KubeJS 的加载时机）。

未完成 / 优先任务（建议的起点）
1. 编写或补全关键单元 / 集成测试（覆盖 ModificationManager、JsonModificationLoader、同步包）。
2. 补全文档：完善 `docs/REGISTER_JSON.md` 的字段说明与更多示例。
3. 验证并完善客户端 GUI（`client/gui/ModificationScreen`）与菜单适配器。确保从服务器同步数据后客户端状态正确显示。
4. 提供示例修改（从 `archive_examples/` 恢复或重写）并在 CI 中运行数据加载来验证。 

开发注意事项
- 仓库当前不依赖 `archive/`，不要修改或期待其存在于远程（本地可保存参考）。
- 若要使示例可选，项目使用了反射保护（例如对 `examples` 的加载），恢复示例需要把示例源码移回 `src/` 并移除反射调用。
- 在修改公共 API（如 `ArsenalGraftAPI`）前，先在本地 run `./gradlew clean build` 验证没有连锁破坏。

代码风格与测试
- 请遵循项目当前包命名与类分层（`api/v3/*` 为外部 API，`modification/*` 为实现细节）。
- 建议添加 JUnit 测试并把其作为 CI 验证的一部分（我可以帮你添加 GitHub Actions 配置）。

提交与协作
- 本地默认分支名：`main`。
- 推荐工作流程：feature 分支 → PR → CI（编译 + 测试）→ 合并到 `main`。
