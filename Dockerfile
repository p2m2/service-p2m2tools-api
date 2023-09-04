FROM inraep2m2/scala-sbt:1.0.2

LABEL author="Olivier Filangi"
LABEL mail="olivier.filangi@inrae.fr"
ENV MILL_VERSION="0.11.2"

EXPOSE 8080

COPY . /service-p2m2tools-api/
WORKDIR /service-p2m2tools-api/
RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/${MILL_VERSION}/${MILL_VERSION} > mill &&\
    chmod +x mill &&\
    ./mill app.test &&\
    ./mill app.assembly

CMD ["java","-jar","./out/app/assembly.dest/out.jar"]