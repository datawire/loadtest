FROM alpine:3.7

ENV LOCUST_VERSION=0.8.1

WORKDIR /locust
EXPOSE  8089 5557 5558

RUN apk add --no-cache \
        --virtual=.build-dep build-base \
    && apk --no-cache add libzmq python3 python3-dev \
    && python3 -m ensurepip \
	&& rm -r /usr/lib/python*/ensurepip \
	&& pip3 install --no-cache-dir --upgrade pip setuptools \
	&& pip3 install --no-cache-dir locustio==${LOCUST_VERSION} \
	&& if [[ ! -e /usr/bin/pip ]]; then ln -s pip3 /usr/bin/pip; fi \
	&& if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi \
	&& rm -r /root/.cache \
	&& apk del .build-dep

COPY . .
ENTRYPOINT ["./entrypoint.sh"]
