# ---------- Etapa 1: Build con Maven ----------
FROM eclipse-temurin:17-jdk-alpine AS build

# Instala utilidades (opcional: para "curl" en healthcheck local)
RUN apk add --no-cache bash curl

WORKDIR /workspace

# Copiamos sólo archivos de build primero para aprovechar cache
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Descarga dependencias (sin compilar fuente aún)
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

# Ahora sí copia el código
COPY src src

# Empaqueta (genera target/*.jar o *.war)
RUN ./mvnw -q -DskipTests clean package

# Busca el artefacto final (jar o war ejecutable de Spring Boot)
RUN ARTIFACT="$(ls target | grep -E '\.(jar|war)$' | head -n 1)" && \
    echo "Empaquetado: $ARTIFACT" && \
    cp "target/$ARTIFACT" /workspace/barajasclub.war

# ---------- Etapa 2: Runtime ultra-ligero ----------
FROM eclipse-temurin:17-jre-alpine

# Zona horaria (opcional)
ENV TZ=America/Mexico_City
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Usuario no root por seguridad
RUN addgroup -S app && adduser -S app -G app
USER app

WORKDIR /app
COPY --from=build /workspace/app.jar /app/barajasclub.war

# Ajustes de JVM pensados para contenedor
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport"
# Perfil por defecto (ajústalo si usas 'prod' o similar)
ENV SPRING_PROFILES_ACTIVE=default

# Render expone $PORT; Spring no lo toma solo, se lo pasamos a la JVM
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://127.0.0.1:${PORT:-8080}/actuator/health || exit 1

# Importante: -Dserver.port=$PORT para Render
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
