#!/usr/bin/env bash
# run.sh: YootGame 실행 스크립트

# 스크립트 위치에 따라 경로 조정 필요 시 수정하세요
JAR_PATH="target/YootGame-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_PATH" ]; then
  echo "오류: ${JAR_PATH} 파일을 찾을 수 없습니다."
  exit 1
fi

echo "YootGame을 실행합니다..."
java -jar "$JAR_PATH"