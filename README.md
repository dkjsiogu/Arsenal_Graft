## Arsenal_Graft — 开发者入门（面向继续开发者）

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

我可以代劳的事项
- 帮你把本地 `main` 推送到你已创建的 GitHub 仓库（如果需要我可以重试网络推送）。
- 帮你写 CI（GitHub Actions）以在每次 PR/run 时构建并运行测试。 

要我现在做什么？
- A：把现在的 README 提交到本地仓库（我可以立即提交）。
- B：我先不提交，你人工检查并指示我何时提交。
- C：替你创建一个最小 GitHub Actions CI 工作流用于构建（我也可同时提交）。

