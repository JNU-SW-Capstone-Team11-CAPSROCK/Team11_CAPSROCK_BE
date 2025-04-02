#!/bin/bash

# 최신 코드 가져오기
echo "🔄 Git Pull..."
git pull origin main || { echo "❌ Git Pull 실패"; exit 1; }

# 애플리케이션 빌드
echo "⚙️ Gradle Build..."
./gradlew build || { echo "❌ Gradle Build 실패"; exit 1; }

# 기존 컨테이너 중지 및 삭제
echo "🛑 Stopping old container..."
docker stop capsrock-app
docker rm capsrock-app

# 새 이미지 빌드
echo "🐳 Building new Docker image..."
docker build -t capsrock-be . || { echo "❌ Docker Build 실패"; exit 1; }

# 사용되지 않는 이미지 정리
docker rmi $(docker images -f "dangling=true" -q)

# 새 컨테이너 실행
echo "🚀 Running new container..."
docker run -d --name capsrock-app -p 8080:8080 capsrock-be

echo "✅ 배포 완료!"
