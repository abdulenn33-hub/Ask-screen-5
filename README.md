# APK Builder (Docker)

This repo provides a small, repeatable **APK builder** that builds Android APKs **inside Docker** (no local Android SDK required).

## Requirements

- Docker

## Usage

From this repo:

```bash
chmod +x ./apk-builder.sh

# Build debug APK for the default module "app"
./apk-builder.sh --project /path/to/your/android-project --variant debug --output-dir ./dist

# Build release APK (your project must already have signingConfig set up)
./apk-builder.sh --project /path/to/your/android-project --module app --variant release --output-dir ./dist
```

## Options

Run:

```bash
./apk-builder.sh --help
```

## Output

Built APKs are copied to the folder you pass via `--output-dir` (default: `./dist`).