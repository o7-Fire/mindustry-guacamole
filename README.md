# Mindustry-Mods-Template

- V7 Edition

### How to get indexed by mods browser

![](https://cdn.discordapp.com/attachments/713346278003572777/821210982449807380/unknown.png)

### How to build and run

- `gradle runClient` install the mod and run it in just single command
- `gradle runServer` same thing as above but for server

### How to make jar for android

- install Android SDK ???
- add to `ANDROID_HOME` environment
- `gradle deploy`
- Dex = Dex only no class
- Example-Mods = class only no dex
- Example-Mods-Dexed = class and dex

### Feature

- Edit the gradle.properties instead of `mod.hjson`

- Editing Changelog.md will affect the release description

- Automatically create a new draft everytime you push

- Automatically use d8 and make dex-ed jar if Android SDK detected