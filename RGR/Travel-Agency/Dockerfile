# 1. Беремо основу
FROM eclipse-temurin:21-jdk

# 2. Створюємо робочу папку всередині контейнера
WORKDIR /app

# 3. Копіюємо наш скомпільований .jar файл всередину контейнера
COPY target/*.jar app.jar

# 4. Команда, яка запуститься при старті контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]
