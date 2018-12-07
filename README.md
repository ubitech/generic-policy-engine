#### Overview  

Current project includes a Proof of concept for a generic scalable policy engine.
Policy engine is based on drools with rule kjars discovery in a remote maven repository. 
Discovery of remote kjars is done via the drools KieScanner. KieScanner is a scanner of the maven repositories (both local and remote) used to automatically discover if there are new releases for a given KieModule and its dependencies and eventually deploy them in the KieRepository.

#### Policy engine Arquitecture:

<img src="/images/policyArchitecture.png" width="500">

The delivery of the different policy action messages are delivered at the policy engine workers as follows:  

<img src="/images/distributedpolicymanager.png" width="1000">

#### Prerequisites:
1. Rabbitmq pub/sub framework  
You can access to http://localhost:15672 with username/password guest/guest.  
You should configure rabbitmq by ip at the application properties of policyengine  
```
docker-compose up -d broker
docker exec broker rabbitmq-plugins enable rabbitmq_management
```
2. A Nexus maven repository  
```
docker-compose up -d my-nexus //like this there is a connectivity problem between policy manager container and nexus
docker run -d -p 8081:8081 -p 8082:8082 -p 8083:8083 --name my-nexus sonatype/nexus3:3.0.0
```
You should create a new repository named  maven-group that includes the following sub repositories: central, releases & snapshots.  
Extra information can be found http://codeheaven.io/using-nexus-3-as-your-repository-part-1-maven-artifacts/  
You can access Nexus repository at http://localhost:8081  

You should update the ip of the remote nexus maven repository at the following files:  

* settings.xml
* /src/main/resources/application.properties


#### Local mode execution it in standalone mode:
```
mvn clean instal 
java -jar target/policy-engine-0.0.1-SNAPSHOT.jar 
```

#### Containerized mode:
In containerized  mode there is an extra prerequisites. This consist in enabling a load balancer in front of the workers. Traefik load balancer is selected for that. You can access traefik at http://localhost:8080/dashboard/    

##### Start Traefik load balancer
```
sudo service apache2 stop //Stop apache because traefik uses the port 80
docker-compose up -d reverse-proxy 
```

##### Create policyengine container(s)
```
docker  build -t policyengine . // build policy engine image
docker-compose up -d policyengine // Create only one worker
docker-compose up -d --scale policyengine=2  //Create a cluster of policy engine containers
```

##### Some usefull commmands for testing are:  
```docker images //fetch all docker images  
docker image prune -a // remove all images which are not used by existing containers  
docker rm $(docker stop $(docker ps -a -q --filter ancestor=policyengine --format="{{.ID}}")) // kill all policy engine workers
docker logs policyengine_policyengine_1 --follow //read logs from a worker
docker exec -it  policyengine_policyengine_1  bash // get inside the container

```  

#### License

This component is published under Apache 2.0 license. Please see the LICENSE file for more details.

#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

- Eleni Fotopoulou ([@elfo](https://github.com/efotopoulou))
- Anastasios Zafeiropoulos ([@tzafeir ](https://github.com/azafeiropoulos))
