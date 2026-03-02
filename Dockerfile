FROM eclipse-temurin:25-jdk

# 컨테이너 내 작업 디렉토리 생성
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 환경 변수 기본값 설정
ENV DB_DDL_AUTO=none

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 3000