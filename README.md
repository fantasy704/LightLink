# LightLink · 轻链

一个前后端分离的**轻量级多人在线语音平台**——左侧是 Discord 式语音频道的频道列表，进入频道即可随时开麦，无需预约、无需排队，像走进一间开着灯的屋子那样自然。基于 WebRTC 实现多人实时语音通话与文字聊天，适合开黑、远程会议、亲友连麦等场景。界面简洁清爽，支持中英文双语切换与明暗主题。

**「轻量」是本项目的核心理念**：架构轻——音频以 WebRTC P2P 直连，服务器只做信令与状态转发，不承载媒体流；部署轻——一个 Spring Boot 后端充当 HTTP/WS 信令服务，一个 Vue 前端渲染全部界面，MySQL + Redis 即可支撑起整个平台；上手轻——无需独立注册页，申请账号、审核通过、进频道开麦，三步就走完整个链路。

> 📌 这是作者**第一个使用 WebRTC** 的项目，也是学习 Java + 前端之后**第一个自主设计并完整实现的全栈项目**，从数据库设计、后端接口到前端交互全部独立完成。

## 功能特性

### 🎙️ 实时语音
- 基于 WebRTC + STUN 搭建多人点对点语音，房间内成员自由开麦
- 获取/切换本地麦克风设备，静音开关，实时音量可视化
- 房间内成员在线状态实时同步，说话状态标识

### 💬 文字聊天
- 房间内实时文字消息，进出房间系统消息提示

### 🏠 频道（服务器）管理
- 创建频道（单个用户最多 5 个），自定义频道名、简介与人数上限（2~50 人）
- 通过 20 位随机邀请码一键加入，频道满员拦截
- 频道主可管理成员（移除成员）、调整人数上限、删除频道
- 频道成员列表实时加载，离线成员自动置灰、在线优先排序

### 👤 账号体系
- 邮箱注册**申请 + 管理员审核**机制，审核通过后方可登录
- 登录 / 自动登录 / 退出，Redis 会话令牌（24h 有效），单端在线互斥
- 密码前端 MD5 加密后传输

### 📱 客户端设置
- 中文 / 英文双语（i18n）
- 高级网络模式：自定义 HTTP / WebSocket 服务器地址并可测试连通性

### 🛠️ 管理后台（`light-link_admin`）
- 独立的管理端前端 + 后端服务
- 查看注册申请列表，一键通过 / 拒绝（需要管理凭证 `adminKey`）

## 技术栈

| 端 | 技术 |
| --- | --- |
| 前端（用户端） | Vue 3 · Vite 6 · Element Plus · **Sober** · Pinia · Vue Router · Axios · WebRTC |
| 前端（管理端） | Vue 3 · Vite |
| 后端 | Java 17+ · Spring Boot 3.4 · Maven 多模块 · MyBatis · WebSocket (Jakarta) · FastJSON |
| 数据库 | MySQL 8.0 · Redis |
| 构建 | Maven · npm |

Sober 官方文档：<https://soberjs.com/>（UI 组件库，项目中以 `s-` 前缀组件使用，如 `s-button`、`s-popup`、`s-text-field`，与 Element Plus 并存）

## 系统架构

```
                        ┌─────────────────────────────────────────┐
                        │               前端 (Vite)                │
                        │  light-link_front (端口 5173)            │
                        │  Vue3 + Element Plus + Sober + Pinia     │
                        │  WebRTC RTCPeerConnection (P2P 语音)     │
                        └───────┬───────────────────┬─────────────┘
                                │ HTTP /api         │ WS /api/websocket
                                │ (axios 代理)      │ (信令: offer/answer/ice)
                        ┌───────┴──────────┐  ┌────┴──────────────┐
                        │  lightlink_web   │  │   各客户端 P2P     │
                        │  (端口 8080)      │  │   WebRTC 音视频    │
                        │  账号/频道接口    │  │   (STUN 打洞)      │
                        └───────┬──────────┘  └───────────────────┘
                                │
                        ┌───────┴──────────┐
                        │ lightlink_admin  │  ← light-link_admin 管理端前端 (代理 /admin)
                        │  (端口 8081)      │
                        └───────┬──────────┘
                                │
                       MySQL (5 张表) + Redis (会话/在线状态)
```

### 核心通信流程

1. **信令层**：前端连接 WebSocket（`/api/websocket/{server_id}`），与后端交换 `offer` / `answer` / `ice_candidate` 信令，同时维护房间成员在线状态与心跳。
2. **媒体层**：握手完成后，各客户端建立点对点 `RTCPeerConnection` 直接传输音频，默认使用 Google 公共 STUN 服务器（`stun.l.google.com:19302`）。
3. **心跳与重连**：客户端每 5s 发送 `ping`，后端回 `pong`；断线后 3s 自动重连。

### WebSocket 消息类型

| 消息类型 | 方向 | 说明 |
| --- | --- | --- |
| `ping` / `pong` | 双向 | 心跳保活 |
| `user_joined` / `user_left` | 服务端→客户端 | 成员进出房间，携带在线用户列表 |
| `text_message` | 双向 | 房间文字聊天 |
| `offer` / `answer` / `ice_candidate` | 双向 | WebRTC SDP / ICE 信令（转发给指定成员） |
| `mute_audio` / `speaking` | 双向 | 静音 / 说话状态广播 |
| `join_room` / `leave_room` / `reconnect` | 预留 | 房间控制消息 |

## 数据库设计

数据库名 `light_link`（由 `light-link.sql` 初始化），共 5 张表：

| 表 | 说明 | 关键字段 |
| --- | --- | --- |
| `user_info` | 用户信息 | `uid`（U+8位随机）、`email`、`name`、`password`（MD5） |
| `apply_info` | 注册申请表 | 申请时写入，审核通过后转存 `user_info` 并删除 |
| `server` | 频道信息 | `server_id`（S+8位随机）、`server_name`（唯一）、`server_master`、`max_number`（2~50） |
| `server_member` | 频道成员 | `server_id` + `server_user` 联合 |
| `invite_link` | 邀请码 | `link_key`（20位随机）、`link_server` |

登录会话不落库，由 Redis（db 2）保存：
- `PERSONALMEETING:LOGIN_USER:{token}` → 登录用户信息
- `PERSONALMEETING:UID_TOKEN:{uid}` → 用户当前令牌（用于单端在线互斥）

## 项目结构

```
light-link
├── light-link.sql                # MySQL 初始化脚本
├── README.md
├── light-link_backend            # 后端（Maven 多模块）
│   ├── pom.xml
│   ├── lightlink_web             # 用户端服务（端口 8080）
│   │   └── src/main/java/top/lightlink/web
│   │       ├── controller/       # AccountController / ServerController / WebTestController
│   │       ├── websocket/        # WebSocket 信令服务 + Configurator（从 Cookie 鉴权）
│   │       └── config/           # CORS / WebSocket 配置
│   ├── lightlink_admin           # 管理端服务（端口 8081）
│   │   └── src/main/java/top/lightlink/admin
│   │       └── controller/       # UserController（审核注册申请）
│   └── lightlink_common          # 公共模块（实体/服务/Mapper/Redis/异常/工具）
│       └── src/main/java/top/lightlink
│           ├── entity/           # PO / DTO / VO / Query / 枚举（ResponseCodeEnum 等）
│           ├── service/          # AccountService / ServerService 实现
│           ├── mapper/           # MyBatis Mapper（UserInfo/ApplyInfo/Server/...）
│           ├── components/       # RedisComponent（会话令牌管理）
│           ├── signal/           # WSMessageType / SignalMessage 信令消息结构
│           ├── exception/        # BusinessException + 全局异常处理器
│           └── utils/            # RedisUtil / StringUtil / CopyUtil
├── light-link_front              # 用户端前端（Vue 3 + Vite，端口 5173）
│   └── src
│       ├── views/                # server(频道) / online(语音房) / person(个人) / invitation(邀请) / error
│       ├── store/                # OnlineStore(信令+RTC) / LoginStore / SettingStore / ThemeStore / TabStore / LocalDeviceStore
│       ├── device/               # useDevice.js 麦克风设备管理
│       ├── i18n/                 # zh-CN / en-US
│       └── utils/                # Request(axios) / Message / Confirm / I18NUtil
└── light-link_admin              # 管理端前端（Vue 3 + Vite，代理 /admin → 8081）
    └── src
        ├── App.vue               # 申请审核页面（管理凭证 + 通过/拒绝）
        └── utils/Request.js
```

## 开源协议

本项目使用 [MIT](LICENSE) 协议开源，欢迎自由使用、修改与传播。

## 意见与改进

作为作者第一版自主设计的全栈作品，本项目一定还有许多不完善之处。**欢迎提出建议或反馈**（架构、体验、代码风格等任何方向都可以），也欢迎大家基于本项目**复刻或改造**，做出属于自己的语音平台。任何 Issue、PR 或想法都欢迎交流。

此外，作者**有空时会持续完善本项目**，后续会不断优化体验、补充功能，敬请期待。