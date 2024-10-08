# Use OpenJDK 21 as a parent image
FROM openjdk:21-jdk-bullseye

# Update the package list and upgrade installed packages
RUN apt update && apt upgrade -y

# Install dependencies
RUN apt install -y wget unzip curl gnupg

# Download and install Google Chrome
RUN wget http://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_129.0.6668.89-1_amd64.deb
RUN apt install -y ./google-chrome-stable_129.0.6668.89-1_amd64.deb

# Get the installed Chrome version and set it as an argument
RUN CHROME_VERSION=$(google-chrome --version | cut -d ' ' -f 3)
RUN echo "Chrome version: ${CHROME_VERSION}"

# Download and install ChromeDriver using the specified version
RUN wget https://storage.googleapis.com/chrome-for-testing-public/129.0.6668.89/linux64/chromedriver-linux64.zip
RUN unzip chromedriver-linux64.zip
RUN mv chromedriver-linux64/chromedriver /usr/local/bin/chromedriver
RUN chmod +x /usr/local/bin/chromedriver

# Add the application's JAR file to the container
ARG JAR_FILE=build/libs/html-to-pdf-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]