FROM hseeberger/scala-sbt:8u242_1.3.8_2.13.1 as build
ARG VERSION
COPY . .
RUN sbt "clean; compile; dist"
RUN unzip /root/target/universal/diditweetthat-${VERSION}.zip \
    && chmod +x  diditweetthat-${VERSION}/bin/diditweetthat

FROM openjdk:8-jre-alpine as prod
ARG VERSION
WORKDIR /root
COPY --from=build /root/diditweetthat-${VERSION} /root/diditweetthat-${VERSION}/
RUN apk update && apk upgrade && apk add bash
CMD /root/diditweetthat-${VERSION}/bin/diditweetthat
