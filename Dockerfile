FROM maven:3.9-eclipse-temurin-17

WORKDIR /framework

# Resolve dependencies first so this layer caches independently of source changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

# Override to target a tag, e.g.
#   docker run --rm api-tests mvn test -Dcucumber.filter.tags="@smoke"
CMD ["mvn", "-B", "test"]
