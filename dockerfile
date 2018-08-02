FROM myimage

RUN apt-get update
RUN apt-get install -y maven

WORKDIR /code

ADD pom.xml /code/pom.xml
ADD desktopClient /code/desktopClient
ADD server /code/server

RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify", "-DskipTests"]

RUN ["mvn", "package", "-DskipTests"]

EXPOSE 7015
CMD ["/srv/java/jvm/bin/java", "-jar", "server/target/server-1.0-SNAPSHOT.jar"]
