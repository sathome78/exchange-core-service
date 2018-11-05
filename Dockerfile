FROM openjdk:8-jdk
RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*
VOLUME /tmp
ARG APP_PATH=/exrates-core-service
ARG ENVIRONMENT

RUN mkdir -p exrates-core-service
COPY ./target/core-api.jar ${APP_PATH}/core-api.jar
COPY ./target/config/${ENVIRONMENT}/application.properties ${APP_PATH}/application.properties
ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.properties"

WORKDIR ${APP_PATH}

EXPOSE 8080
CMD java -jar core-api.jar $CONFIG_FILE_PATH
