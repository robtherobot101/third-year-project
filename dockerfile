FROM airdock/oracle-jdk-maven

RUN ln -sf /usr/share/zoneinfo/Pacific/Auckland /etc/localtime
RUN rm -r /code/*
#RUN apt-get update
#RUN apt-get install -y maven


WORKDIR /code/server

ADD pom.xml /code/pom.xml
ADD server /code/server

RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify", "-DskipTests"]

RUN ["mvn", "package", "-DskipTests"]

EXPOSE 7015
CMD ["/srv/java/jvm/bin/java", "-jar", "target/server-1.0-SNAPSHOT.jar"]
