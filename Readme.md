# DCOM Service Lookup


## To build an image

Please run the build script


## To install

Please run via docker


## To add a new service

To add a new service what you must do is add their public key to the service lookup service registry. Once this is done they can then register themselves.

Ask the service to generate an RSA keypair and then send you the public key.

```
openssl genrsa -aes128 -out mykey.pem 1024
openssl rsa -in mykey.pem -pubout > mykey.pub
```

They should send you the mykey.pub file - they should keep the mykey.pem file private for their service to run on

You then need to add it to the running service lookup. First get the service lookup container id by running

```
docker ps
```

Then run

```
docker cp <keyfile> <docker_container_name>:/usr/local/tomcat/webapps/certificates/<hostname of service>
```

The service lookup will then automatically process this.


## To add a new "legacy" service

First get the service lookup container id by running

```
docker ps
```

Then run

```
docker cp <docker_container_name>:/opt/servicelist/services.json ./services.json
```

Edit the file to add the service details. Then run 

```
docker cp ./services.json <docker_container_name>:/opt/servicelist/services.json 
docker restart <docker_container_name>
```
