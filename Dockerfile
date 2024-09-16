FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

ENV SERVER_ADDRESS=0.0.0.0:8080
ENV POSTGRES_CONN=postgres://cnrprod1725773447-team-79619:cnrprod1725773447-team-79619@rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net:5432/cnrprod1725773447-team-79619
ENV POSTGRES_JDBC_URL=jdbc:postgresql://rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net:6432/cnrprod1725773447-team-79619
ENV POSTGRES_USERNAME=cnrprod1725773447-team-79619
ENV POSTGRES_PASSWORD=cnrprod1725773447-team-79619
ENV POSTGRES_HOST=rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net
ENV POSTGRES_PORT=5432
ENV POSTGRES_DATABASE=cnrprod1725773447-team-79619

RUN gradle build --no-daemon

FROM openjdk:8-jre-slim

ENV SERVER_ADDRESS=0.0.0.0:8080
ENV POSTGRES_CONN="postgres://cnrprod1725773447-team-79619:cnrprod1725773447-team-79619@rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net:5432/cnrprod1725773447-team-79619"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net:6432/cnrprod1725773447-team-79619"
ENV POSTGRES_USERNAME="cnrprod1725773447-team-79619"
ENV POSTGRES_PASSWORD="cnrprod1725773447-team-79619"
ENV POSTGRES_HOST="rc1b-5xmqy6bq501kls4m.mdb.yandexcloud.net"
ENV POSTGRES_PORT=5432
ENV POSTGRES_DATABASE="cnrprod1725773447-team-79619"

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]

