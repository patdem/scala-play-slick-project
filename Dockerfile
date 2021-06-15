FROM ubuntu:20.10

RUN apt-get -yqq update \
    && apt-get -yqq install curl gnupg vim sudo openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

RUN curl -sLo scala.deb https://downloads.lightbend.com/scala/2.13.6/scala-2.13.6.deb \
    && dpkg -i scala.deb \
    && apt-get -yqq update \
    && apt-get -yqq install scala \
    && rm scala.deb
    
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list \
    && echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list \
    && curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add \
    && sudo apt-get -yqq update \
    && sudo apt-get -yqq install sbt=1.5.3
    
RUN useradd -ms /bin/bash -G sudo patdem \
   && passwd -d patdem

USER patdem

WORKDIR /home/patdem/app

COPY . .

CMD ["/bin/bash", "-c", "sbt run -Dconfig.resource=prod.conf"]