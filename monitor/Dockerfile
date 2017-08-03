FROM java:8
MAINTAINER Ryze

VOLUME /tmp
ADD ./target/monitor.jar /app.jar

RUN bash -c 'touch /app.jar'
EXPOSE 8040 8041
ENTRYPOINT ["java","-jar","/app.jar"," --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

