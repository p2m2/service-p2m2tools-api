FROM inraep2m2/scala-sbt:1.0.2

LABEL author="Olivier Filangi"
LABEL mail="olivier.filangi@inrae.fr"
ENV MILL_VERSION="0.11.2"

COPY . /service-p2m2tools-api/
WORKDIR /service-p2m2tools-api/
RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/${MILL_VERSION}/${MILL_VERSION} > mill &&\
    chmod +x mill &&\
    ./mill app.test # first time download and build every thing !

CMD ["./mill","-w","app.runBackground"]