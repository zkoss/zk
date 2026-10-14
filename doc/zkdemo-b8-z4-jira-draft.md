# zkdemo B8-Z4 Jira 草稿：`Fileupload.get` 的 `params` 傳 `null` 會拋 NPE

狀態：**已送出**，[ZK-6198](https://zkoss.atlassian.net/browse/ZK-6198)（2026-10-14，Affects Version 11.0.0）。

- 來源：zkdemo-30 回報 B8-Z4；本 session 在 8085 Marble 預覽站重現（2026-10-14）。
- 判定：ZK 核心 bug，與主題無關，所以不放進 ZK-6112。`master`、`10.4`、`marble` 三條線的 `Fileupload.java:360` 內容相同；只在 11.0.0（marble）實際跑過，所以 Affects Version 只填 `11.0.0`。
- 查重：JQL `project = ZK AND (summary ~ "Fileupload.get" OR text ~ "\"params\" is null")` 沒有相同的 ticket。
- 非視覺問題，不需要截圖。

---

## 送出內容

```text
projectKey:        ZK
issueTypeName:     Bug
summary:           fileupload: Fileupload.get(Map params, ...) throws NullPointerException when params is null
contentFormat:     markdown
additional_fields: { "versions": [{ "name": "11.0.0" }] }
```

### description

````markdown
# Steps to Reproduce

```xml
<zk>
    <button label="Upload" onClick='org.zkoss.zul.Fileupload.get(null, "Select a file", "Upload", 1, 50, false, null)'/>
</zk>
```

1. click the Upload button

# Current Result

an error box shows: Cannot invoke "java.util.Map.put(Object, Object)" because "params" is null

# Expected Result

the upload dialog opens, the same as when an empty map is passed

# Debug Info

`Fileupload.get(Map, String, String, String, int, int, boolean, EventListener)` calls `params.put(...)` without a null check — https://github.com/zkoss/zk/blob/master/zul/src/main/java/org/zkoss/zul/Fileupload.java#L360
The overloads without a `params` argument pass `new HashMap<String, Object>(8)`, so only callers of the public `Map` overloads hit this. The javadoc does not say `params` must be non-null.

# Workaround

pass an empty map: `Fileupload.get(new HashMap<>(), message, title, max, maxsize, alwaysNative, listener)`
````
