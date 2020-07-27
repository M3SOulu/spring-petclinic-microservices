FROM jenkinsci/blueocean:latest

USER root

# Install docker-compose
RUN apk add py-pip python3-dev libffi-dev openssl-dev gcc libc-dev make
RUN pip3 install docker-compose