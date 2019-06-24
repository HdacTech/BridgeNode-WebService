Bridge Node Web Server Service
======================

### License

Hdac private platform is licensed under the [MIT License](http://opensource.org/licenses/MIT).

Copyright (c) 2018-2019 Hdac Technology AG


### Execution environment 

About source code
>- JavaSE 1.8 optimization
>- Using Eclipse Oxygen.1a Release (4.7.1a)
>- Use HdacJavaLib.jar, contractLib-0.0.1.jar
>- Spring 5.0.6

About docker image
>- Centos
>- Apache web server
>- Tomcat v8.5


### Related docker image 

You can download docker image in [Bridgenode docker hub](https://hub.docker.com/r/hdac/bridgenode).
>1. webserver_v09 (docker-compose: [webserver-docker-compose](docker/docker-compose/webserver))
>2. nfsserver_v09 (docker-compose: [nfsserver-docker-compose](docker/docker-compose/nfsserver))


### How to build source code

>1. Download the source code and add the project through Eclipse.
>2. File > Import > Existing Projects into Workspace
>3. Choose the folder where the source is located, check pom.xml, and complete the import.
>4. After import project, check build.xml.
>5. Choose build.xml > Run As > Ant Build.
>6. Check result of build in webapps > WEB-INF > classes.

### How to update docker image

Webserver docker image has 2 of HDAC core cli and loadbalanced tomcat.
If source code update occurs, the class file must be overridden in the existing docker image.

>1. Operate webserver docker container
>2. Update file to inside of container tomcat1 directory (tomcat2 also)  
>- classes : /opt/tomcat1/myapp/WEB-INF
>- js,css : /opt/tomcat1/myapp/explorer
>- jsp : /opt/tomcat1/myapp/jsp
>- $ docker cp classes webserver:/opt/tomcat1/myapp/WEB-INF  
>3. After update, restart tomcat service
>- $ systemctl restart tomcat-1 (tomcat-2 also)
>4. If library changeing occurs, All library files are managed by nfsserver, copy the jar file to the nfsserver docker container.  
>- $ docker cp Anchoring-0.0.1.jar nfsserver:/opt/shareUtil/lib (After running nfsserver container)

### How to operate service

>1. Operate nfsserver docker container
>2. Operate webserver docker container
>3. Checking share library inside webserver container  
>- $ docker exec -it webserver bash  
>- $ cd /opt/shareLib  
>4. configure database config file (/opt/tomcat1/myapp/WEB-INF/c;asses/config)
>5. After configuration, restart tomcat service
>- $ systemctl restart tomcat-1 (tomcat-2 also)


_A detailed description of the docker setting and operation can be found here [Bridgenode docker hub](https://hub.docker.com/r/hdac/bridgenode)._






