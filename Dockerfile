# Define the build stage for Maven
FROM openjdk:17-jdk as build

# Set Maven version and URL for the Maven binary
ARG MAVEN_VERSION=3.8.8
ARG MAVEN_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Maven
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_URL} \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Set up the environment variables
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Copy the project files into the container
COPY src /home/app/src
COPY pom.xml /home/app

# Set the working directory
WORKDIR /home/app

# Run Maven build
RUN mvn -f /home/app/pom.xml clean package -X

# Define the final image
FROM openjdk:17-jdk

# Copy the built artifact from the build stage
COPY target/fcsb.jar /app.jar

# Expose the port the app runs on
EXPOSE 7000

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
