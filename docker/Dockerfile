FROM ubuntu:22.04

ARG DEBIAN_FRONTEND=noninteractive
ARG ANDROID_SDK_ROOT=/opt/android-sdk

ENV ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT}"
ENV ANDROID_HOME="${ANDROID_SDK_ROOT}"
ENV PATH="${PATH}:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/tools/bin"

# Android commandline-tools download URL (Linux)
ARG ANDROID_CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

RUN apt-get update \
  && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    git \
    unzip \
    wget \
    zip \
    openjdk-17-jdk \
  && rm -rf /var/lib/apt/lists/*

RUN mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools" \
  && cd /tmp \
  && wget -qO cmdline-tools.zip "${ANDROID_CMDLINE_TOOLS_URL}" \
  && unzip -q cmdline-tools.zip -d "${ANDROID_SDK_ROOT}/cmdline-tools" \
  && mv "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest" \
  && rm -f /tmp/cmdline-tools.zip

# Accept licenses and install common SDK components.
# Note: We install a couple of recent platforms/build-tools so most projects build out of the box.
RUN yes | sdkmanager --licenses >/dev/null \
  && sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "platforms;android-33" \
    "build-tools;34.0.0" \
    "build-tools;33.0.2"

WORKDIR /work

