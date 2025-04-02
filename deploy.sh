#!/bin/bash
# 프로젝트 경로 설정
PROJECT_PATH="/volume1/docker/Team11_CAPSROCK_BE"
DEPLOY_PATH="/volume1/docker/deploy"

# 기존 컨테이너 중지 및 삭제
CONTAINER_NAME="capsrock_app"
EXISTING_CONTAINER=$(docker ps -q -f name=$CONTAINER_NAME)

if [ ! -z "$EXISTING_CONTAINER" ]; then
  echo "기존 컨테이너 종료 중..."
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi

# Docker 이미지 빌드
echo "새로운 Docker 이미지 빌드 중..."
cd $PROJECT_PATH || exit
docker build -t capsrock-image .

# 새 컨테이너 실행
echo "새로운 컨테이너 실행 중..."
docker run -d \
  --name $CONTAINER_NAME \
  -p 8080:8080 \
  -v $DEPLOY_PATH:/app \
  -e SPRING_PROFILES_ACTIVE=prod \
  capsrock-image

# 실행된 컨테이너 확인
echo "배포 완료! 실행된 컨테이너 확인:"
docker ps -f name=$CONTAINER_NAME
