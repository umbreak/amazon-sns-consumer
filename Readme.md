#Create and run docker image:
Change in the pom file the variable docker.image.prefix for your dockerHub username
```bash
mvn clean package docker:build
docker run -e "SPRING_PROFILES_ACTIVE=prod" -p 80:8080 -t {your_docker_user_name}/komoot-notifcation
```


#Tests
```bash
mvn clean test
````

#Run
##Backend
```bash
mvn spring-boot:run
```


#DB
For this environment I am using a in memory H2 DB. Once the backend is running, the DB can be accessed through http://localhost:8082 and then set in the URL the following:
`jdbc:h2:mem:dataSource;Mode=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MV_STORE=FALSE;MVCC=FALSE;IGNORECASE=TRUE`

That will open a web interface where one can see the DB tables and perform queries on it.
