#!/bin/sh

../gradlew :server:clean :server:build
widdershins --code true build/generated/openapi.json -o openapi.md
exit 0