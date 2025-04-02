#!/bin/bash

# 최신 코드 가져오기
echo "🔄 Git Pull..."
git pull origin main || { echo "❌ Git Pull 실패"; exit 1; }

# 애플리케이션 빌드
echo "⚙️ Gradle Build..."
./gradlew build || { echo "❌ Gradle Build 실패"; exit 1; }

# 기존 컨테이너 중지 및 삭제 (있을 경우만)
if [ "$(docker ps -aq -f name=capsrock_app)" ]; then
    echo "🛑 기존 컨테이너 중지 및 삭제..."
    docker rm -f capsrock_app
fi

# 새 이미지 빌드
echo "🐳 Building new Docker image..."
docker build -t capsrock-be . || { echo "❌ Docker Build 실패"; exit 1; }

# 사용되지 않는 이미지 정리
docker rmi $(docker images -f "dangling=true" -q)

# 새 컨테이너 실행
echo "🚀 Running new container..."
docker run -d --name capsrock_app -p 8080:8080 -v /volume1/docker/deploy:/app capsrock-be

echo "✅ 배포 완료!"
