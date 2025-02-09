# 使用 Maven 3.5 + JDK 8 的 Alpine 作为构建基础镜像
FROM maven:3.5-jdk-8-alpine as builder

# 设置工作目录
WORKDIR /app

# 复制项目的 Maven 配置文件和源码
COPY pom.xml .
COPY src ./src

# 编译打包，跳过测试
RUN mvn package -DskipTests

# 运行容器时启动 Spring Boot 应用
CMD ["java", "-jar", "/app/target/yupao-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
