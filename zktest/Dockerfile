## Tomcat
#FROM tomcat:9-jdk11-openjdk
#ENV DEPLOY_DIR=/usr/local/tomcat/webapps

## WildFly
FROM jboss/wildfly
ENV DEPLOY_DIR=/opt/jboss/wildfly/standalone/deployments

## Glassfish
#FROM glassfish
#ENV DEPLOY_DIR=$GLASSFISH_HOME/glassfish/domains/domain1/autodeploy

ADD debug/zktest*.war ${DEPLOY_DIR}/zktest.war

ENV JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 "
EXPOSE 5005