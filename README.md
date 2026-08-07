# Monet Theme Service

基于壁纸的 Material 3 动态主题生成服务，支持 Android 9+。

## 项目结构

- **monet-api**: AIDL 接口与 ThemeColors 数据模型（共享模块）
- **monet-service**: 服务端应用（壁纸提取 + Monet 引擎 + AIDL Service）
- **monet-client**: 客户端 SDK（AIDL 连接管理 + Compose 主题集成）

## 技术栈

- AGP 9.2.1（内置 Kotlin 支持，无需单独声明 Kotlin 插件）
- Jetpack Compose + Material 3 Expressive (MD3E)
- AIDL 跨进程通信
- Palette API（Android 9-11 兼容）/ WallpaperColors（Android 12+）

## 权限模型

自定义权限 `com.monettheme.permission.GENERATE_THEME`，protectionLevel 为 `dangerous`，
首次调用时系统会弹窗询问用户授权。

## 第三方应用集成

1. AndroidManifest.xml 中声明权限：
   ```xml
   <uses-permission android:name="com.monettheme.permission.GENERATE_THEME" />
   ```

2. 依赖 monet-client 模块

3. 调用示例：
   ```kotlin
   val client = MonetColorClient(context)
   if (client.connect()) {
       val theme = client.generateThemeFromWallpaper(darkTheme = false)
       theme?.let { MonetTheme(colors = it) { YourAppContent() } }
       client.disconnect()
   }
   ```

## 构建

```bash
./gradlew :monet-service:assembleRelease
./gradlew :monet-client:assembleRelease
```
