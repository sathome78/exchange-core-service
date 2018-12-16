FROM java:8
VOLUME /tmp
ARG APP_PATH=/exrates-core-service
ARG ENVIRONMENT

RUN mkdir -p exrates-core-service
COPY ./target/core-service.jar ${APP_PATH}/core-service.jar
COPY ./target/config/${ENVIRONMENT}/application.properties ${APP_PATH}/application.properties
ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.properties"

WORKDIR ${APP_PATH}

EXPOSE 8080
CMD java -jar core-service.jar $CONFIG_FILE_PATH
