# ---------- Etapa 1: Build con Maven ----------
FROM eclipse-temurin:17-jdk-alpine AS build

# Instala utilidades (opcional: para "curl" en healthcheck local)
RUN apk add --no-cache bash curl

WORKDIR /app


COPY target/barajasclub.war app.war


EXPOSE 8080

CMD ["java","-jar","app.war"]


