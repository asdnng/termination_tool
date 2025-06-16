FROM eclipse-temurin:17-jdk-jammy

# 1) 앱 복사 & 빌드
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# 2) 네이티브 Z3 라이브러리 위치 확인 (자동 추출)
ENV JAVA_TOOL_OPTIONS="-Djava.io.tmpdir=/tmp"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/build/libs/termination-tool-0.0.1-SNAPSHOT.jar"]
