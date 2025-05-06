#!/bin/bash
# 프로젝트 경로
PROJECT_PATH=/opt/Team11_CAPSROCK_BE
DEPLOY_PATH=/opt/deploy/
LOG_FILE=/opt/deploy/deploy.log

# 최신 코드 가져오기
cd $PROJECT_PATH || exit
git fetch origin
git pull origin main

# Gradle build
./gradlew bootJar

# 빌드된 JAR 찾기
BUILD_PATH=$(ls $PROJECT_PATH/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_PATH)

# 현재 실행 중인 프로세스 종료
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
  sleep 1
else
  kill -15 "$CURRENT_PID"
  sleep 5
fi

# 배포 디렉토리 생성 및 JAR 복사
mkdir -p $DEPLOY_PATH
cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

# 새로운 JAR 실행
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR > $LOG_FILE 2>&1 &