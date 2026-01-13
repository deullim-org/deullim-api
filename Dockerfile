FROM eclipse-temurin:21-jre

RUN adduser --system --group user

USER user

WORKDIR /app

COPY --chown=user:user build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
