# Project 2: Video Game Library (Android Client)

A Kotlin Android app, built with Jetpack Compose, for keeping track of the video games you own or want to play. You sign in, add games to your library, and browse, search, filter, and remove them. All data comes from our team's Spring Boot REST API.

This repo is the **front end**. The API lives in its own repo: [favGameBackend](https://github.com/jesscococ09/favGameBackend)

- **API base URL:** `https://favgame-backend.onrender.com`

## Team

| Name | GitHub                                      |
| --- |---------------------------------------------|
| Athian Camberos | [@acamberos1](https://github.com/acamberos1) |
| Jessika Torrealba  | [@jesscococ09](https://github.com/jesscococ09) |
| Leonardo Lopez | [@Leobus](https://github.com/leodebuss)     |

## Live API and other links


- **Swagger docs:** `https://favgame-backend.onrender.com/swagger-ui.html`
- **render:** `https://favgame-backend.onrender.com`
- **api documentation:** `https://favgame-backend.onrender.com/v3/api-docs`

## Tech stack

| Area | Tools |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose |
| Networking | Retrofit, OkHttp |
| Auth | OAuth2 Authorization Code with PKCE  |
| Architecture | MVVM (ViewModel and StateFlow) |
| Testing | JUnit, coroutines-test, MockWebServer, Compose UI tests |
| Build | Gradle (Kotlin DSL) |
| Backend this app talks to | Spring Boot, PostgreSQL, Docker |

## Screens

| Screen | What it does |
| --- | --- |
| Login | Sign in through OAuth2 with PKCE. On success, goes to the game list. |
| Game list | Your games, loaded from the live API with pagination. Supports search, filters (genre, platform, rating), and A–Z sorting. |
| Game detail | View, edit, or add a single game, all on one screen. |
| Admin | Shown only to admins. Lists users, toggles the admin role, and deletes users. |

The app also shows a message for 401, 403, 404, and 500 errors instead of a blank screen.

## Local setup

### 1. Requirements

- Android Studio [Android Studio Panda 4 | 2025.3.4 Patch 1]
- JDK 17
- An Android emulator or a physical device
- Docker Desktop, only if you want to run the API locally

### 2. Clone the repo

```bash
git clone https://github.com/jesscococ09/Project2_CST338_GROUP6.git
cd Project2_CST338_GROUP6
```

### 3. Add your secrets to `local.properties`

`local.properties` is in `.gitignore` and must never be committed. Ask a teammate for the values privately, not in the group chat.

```properties
sdk.dir=/path/to/your/Android/sdk
OAUTH_CLIENT_ID=your-client-id

```


### 4. (Optional) Run the API locally with Docker

Do this from the **API repo**, not this one:

```bash
git clone https://github.com/jesscococ09/favGameBackend
cd favGameBackend
docker compose up --build
```

This starts the API and a local PostgreSQL database. To stop them:

```bash
docker compose down
```

Then point the app at your local API in `local.properties`:

```properties
API_BASE_URL=http://10.0.2.2:8080/api/v1/
```

The emulator reaches your computer at `10.0.2.2`, not `localhost`. Plain HTTP is only allowed for local development, through the network security config. The live API uses HTTPS.

### 5. Run the app

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Pick an emulator or device and click **Run**.

## Running tests

```bash
./gradlew test                  # unit tests: ViewModels and API client (MockWebServer)
./gradlew connectedAndroidTest  # Compose UI tests (needs a running emulator or device)
```

## Generating KDoc

```bash
./gradlew dokkaHtml
```



