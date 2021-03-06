FROM maven

WORKDIR /app
RUN cd /app
EXPOSE 8090
COPY settings.xml .
COPY settings.xml /root/.m2/settings.xml
COPY /target/policyengine-0.0.1-SNAPSHOT.jar .
COPY /sample-kjar /app/sample-kjar
COPY /deploykjar.sh /app/deploykjar.sh

CMD ["java","-jar","-Dspring.profiles.active=policyProfile","policyengine-0.0.1-SNAPSHOT.jar"]
