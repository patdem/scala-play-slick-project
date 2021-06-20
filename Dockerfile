FROM adoptopenjdk/openjdk11:ubuntu-slim

RUN apt-get -yqq update \
    && apt-get -yqq install curl unzip
    
RUN curl -sLo sbt.tgz https://github.com/sbt/sbt/releases/download/v1.5.3/sbt-1.5.3.tgz \
    && tar xzf sbt.tgz -C /usr/share/
    
RUN useradd -ms /bin/bash -G sudo patdem \
    && passwd -d patdem

USER patdem

RUN mkdir /home/patdem/app

WORKDIR /home/patdem/app

COPY target/universal/shop-1.1.zip .

RUN unzip -q shop-1.1 \
    && rm shop-1.1.zip

CMD ./shop-1.1/bin/shop -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=9000 -Dconfig.resource=prod.conf
EXPOSE 9000