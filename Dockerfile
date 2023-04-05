FROM tomcat:9.0.44-jdk16-openjdk-buster

LABEL maintainer="beachth@cf.ac.uk"

RUN rm -rf /usr/local/webapps/*
ADD target/ServiceLookup.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir /opt/servicelist
ENV DCOMServiceConfig /opt/servicelist/services.json
ENV DCOMCertificates /usr/local/tomcat/webapps/certificates/
ADD services.json /opt/servicelist/services.json
RUN mkdir /usr/local/tomcat/webapps/certificates
COPY certificates/ /usr/local/tomcat/webapps/certificates/
ADD ServiceLookup.pem /opt/servicelist/ServiceLookup.pem
ENV DCOMCertificatePath /opt/servicelist/ServiceLookup.pem
ENV DCOMCertificatePassword a5b50932


EXPOSE 8080
CMD ["catalina.sh", "run"]
