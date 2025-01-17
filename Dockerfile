FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.4_11

ENV LC_ALL=C

ARG GIT_BRANCH=local
ARG GIT_REVISION=local

ENV MYSQL_VERSION=8.0.17
ENV MARIADB_VERSION=1.1.10
ENV POSTGRESQL_VERSION=42.1.1
ENV JTDS_VERSION=1.3.1

LABEL MYSQL_VERSION=$MYSQL_VERSION
LABEL MARIADB_VERSION=$MARIADB_VERSION
LABEL POSTGRESQL_VERSION=$POSTGRESQL_VERSION
LABEL JTDS_VERSION=$JTDS_VERSION

LABEL GIT_BRANCH=$GIT_BRANCH
LABEL GIT_REVISION=$GIT_REVISION

ADD docker/open-sans.tar.gz /usr/share/fonts/

RUN adduser -S java -h / -D -G root && \
    set -x && \
    apk add --no-cache curl unzip graphviz fontconfig && \
    fc-cache -fv && \
    mkdir /drivers_inc && \
    cd /drivers_inc && \
    curl -JLO http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/$MYSQL_VERSION/mysql-connector-java-$MYSQL_VERSION.jar && \
    curl -JLO http://search.maven.org/remotecontent?filepath=org/mariadb/jdbc/mariadb-java-client/$MARIADB_VERSION/mariadb-java-client-$MARIADB_VERSION.jar && \
    curl -JLO http://search.maven.org/remotecontent?filepath=org/postgresql/postgresql/$POSTGRESQL_VERSION.jre7/postgresql-$POSTGRESQL_VERSION.jre7.jar && \
    curl -JLO http://search.maven.org/remotecontent?filepath=net/sourceforge/jtds/jtds/$JTDS_VERSION/jtds-$JTDS_VERSION.jar && \
    mkdir /drivers && \
    mkdir /output && \
    chown -R java /drivers_inc && \
    chown -R java /drivers && \
    chown -R java /output && \
    apk del curl


ADD target/schema*.jar /
ADD docker/entrypoint.sh /

USER java
WORKDIR /

VOLUME /drivers /output

ENTRYPOINT ["/entrypoint.sh"]
