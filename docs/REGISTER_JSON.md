# REGISTER_JSON

目标：说明如何通过 JSON 在运行时注册改造模板（modification template）。

1) 最小 JSON schema（示例）

```json
{
  "id": "simple_hand",
  "displayName": "Simple Extra Hand",
  "slotType": "hand_extra",
  "components": [
    {
      "type": "inventory",
      "size": 9
    }
  ],
  "maxStack": 1
}
```

字段说明：
- `id`: 模板的唯一标识（字符串）
- `displayName`: 显示名称（字符串）
- `slotType`: 插槽类型（需与 `ArsenalSlotTypes` 中的类型一致）
- `components`: 组件数组，每项描述该改造包含的组件（例如 `inventory`、`skill` 等）
- `maxStack`: 该改造在玩家身上允许的最大数量（整数）

2) 放置位置

将上面的 JSON 文件放置在资源路径：
`resources/data/arsenalgraft/modifications/<id>.json`

框架会通过 `JsonModificationLoader` 在资源加载时自动读取并注册模板。

3) 注意事项

- 如果想通过数据包进行分发，把 JSON 放到对应数据包的 `data/arsenalgraft/modifications/` 下。
- 若需要更复杂的组件，请参考 `api/v3/modification` 中的模板接口并扩展 schema。

4) KubeJS

- 若使用 KubeJS，可以调用 `ArsenalGraftKubeJSAPI` 提供的方法来在脚本中注册模板（示例已在归档的 `api_v3_examples` 中）。
