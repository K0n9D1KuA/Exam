#基础镜像
FROM openjdk:8
#作者
MAINTAINER Kn09D1KuA
#暴露端口
EXPOSE 11000
ADD ExamSystem-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
#配置文件选择prod
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]

