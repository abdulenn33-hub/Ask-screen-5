#!/usr/bin/env bash
set -euo pipefail

IMAGE_NAME="${IMAGE_NAME:-apk-builder:latest}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
  cat <<'EOF'
apk-builder.sh — build Android APKs in Docker

Usage:
  ./apk-builder.sh [options]

Options:
  --project PATH        Android Gradle project root (default: .)
  --module NAME         Gradle module to build (default: app)
  --variant NAME        Build variant: debug|release|<custom> (default: debug)
  --output-dir PATH     Where to copy APK(s) (default: ./dist)
  --rebuild-image       Rebuild the Docker image before building
  --gradle-args "ARGS"  Extra args passed to Gradle (default: none)
  --task TASK           Override Gradle task (default: :<module>:assemble<Variant>)
  -h, --help            Show this help

Examples:
  ./apk-builder.sh --project ../MyApp --variant debug
  ./apk-builder.sh --project ../MyApp --module app --variant release --output-dir ./out

Notes:
  - This expects a Gradle wrapper at <project>/gradlew.
  - For release signing, your Android project must be configured accordingly (signingConfig).
EOF
}

PROJECT="."
MODULE="app"
VARIANT="debug"
OUTPUT_DIR="./dist"
REBUILD_IMAGE="false"
GRADLE_ARGS=""
TASK=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project) PROJECT="$2"; shift 2 ;;
    --module) MODULE="$2"; shift 2 ;;
    --variant) VARIANT="$2"; shift 2 ;;
    --output-dir) OUTPUT_DIR="$2"; shift 2 ;;
    --rebuild-image) REBUILD_IMAGE="true"; shift ;;
    --gradle-args) GRADLE_ARGS="$2"; shift 2 ;;
    --task) TASK="$2"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; echo >&2; usage; exit 2 ;;
  esac
done

if ! command -v docker >/dev/null 2>&1; then
  echo "docker not found. Please install Docker and try again." >&2
  exit 1
fi

if [[ ! -d "${PROJECT}" ]]; then
  echo "--project is not a directory: ${PROJECT}" >&2
  exit 1
fi

PROJECT_ABS="$(cd "${PROJECT}" && pwd)"

mkdir -p "${OUTPUT_DIR}"
OUTPUT_ABS="$(cd "${OUTPUT_DIR}" && pwd)"

if [[ ! -f "${PROJECT_ABS}/gradlew" ]]; then
  echo "No Gradle wrapper found at: ${PROJECT_ABS}/gradlew" >&2
  echo "Point --project at an Android Gradle project root." >&2
  exit 1
fi

if [[ "${REBUILD_IMAGE}" == "true" ]]; then
  echo "Rebuilding image ${IMAGE_NAME}..."
  docker build -t "${IMAGE_NAME}" -f "${SCRIPT_DIR}/docker/Dockerfile" "${SCRIPT_DIR}" >/dev/null
else
  if ! docker image inspect "${IMAGE_NAME}" >/dev/null 2>&1; then
    echo "Building image ${IMAGE_NAME} (first run)..."
    docker build -t "${IMAGE_NAME}" -f "${SCRIPT_DIR}/docker/Dockerfile" "${SCRIPT_DIR}" >/dev/null
  fi
fi

variant_capitalized="${VARIANT^}"

if [[ -z "${TASK}" ]]; then
  TASK=":${MODULE}:assemble${variant_capitalized}"
fi

echo "Building APK(s)"
echo "  project:    ${PROJECT_ABS}"
echo "  module:     ${MODULE}"
echo "  variant:    ${VARIANT}"
echo "  task:       ${TASK}"
echo "  output-dir: ${OUTPUT_ABS}"

docker run --rm \
  --user "$(id -u):$(id -g)" \
  -v "${PROJECT_ABS}:/work:rw" \
  -v "${OUTPUT_ABS}:/out:rw" \
  -e GRADLE_USER_HOME=/work/.gradle-docker-cache \
  "${IMAGE_NAME}" \
  bash -lc "
    set -euo pipefail
    cd /work
    chmod +x ./gradlew
    ./gradlew ${TASK} ${GRADLE_ARGS}

    shopt -s globstar nullglob
    APK_PATHS=( \
      /work/${MODULE}/build/outputs/apk/**/${VARIANT}/*.apk \
      /work/${MODULE}/build/outputs/apk/${VARIANT}/*.apk \
    )

    if (( \${#APK_PATHS[@]} == 0 )); then
      echo 'No APKs found under build/outputs/apk. Build may have produced AAB or used a different output path.' >&2
      exit 3
    fi

    mkdir -p /out
    for apk in \"\${APK_PATHS[@]}\"; do
      echo \"Copying: \${apk}\"
      cp -f \"\${apk}\" /out/
    done

    echo 'Done. APKs in /out:'
    ls -1 /out/*.apk
  "

