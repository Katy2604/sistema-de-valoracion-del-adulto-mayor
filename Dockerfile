
# Etapa 1: Construir la aplicación Java con Maven
FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY prototipo/mavenproject1/pom.xml .
COPY prototipo/mavenproject1/src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar la aplicación en Tomcat 9
FROM tomcat:9.0-jdk8-temurin

# Eliminar aplicaciones de ejemplo de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Configurar Tomcat para escuchar en el puerto 10000 de Render
RUN sed -i 's/port="8080"/port="10000"/' /usr/local/tomcat/conf/server.xml

# Instalar nuestra aplicación como página principal
COPY --from=build /app/target/mavenproject1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 10000

CMD ["catalina.sh", "run"]
