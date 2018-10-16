FROM java:8
VOLUME /tmp
ARG APP_PATH=/exrates-core-service
ARG ENVIRONMENT

RUN mkdir -p exrates-core-service
COPY ./target/core-api.jar ${APP_PATH}/core-api.jar
COPY "./target/config/${ENVIRONMENT}/application.yml" ${APP_PATH}/application.yml
ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.yml"

WORKDIR ${APP_PATH}

EXPOSE 8080
CMD java -jar core-api.jar $CONFIG_FILE_PATH
