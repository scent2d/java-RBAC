FROM openjdk:11 as intermediate
RUN apt-get update
RUN apt-get install -y maven
WORKDIR /casbin-rbac-java
COPY src /casbin-rbac-java/src
COPY pom.xml /casbin-rbac-java/pom.xml
COPY application.server.properties /casbin-rbac-java/application.properties
COPY model.conf /casbin-rbac-java/model.conf
COPY policy.csv /casbin-rbac-java/policy.csv
RUN mvn package
ENTRYPOINT ["java","-jar","target/casbin-rbac-java.jar"]