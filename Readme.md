#Create docker image:
```bash
mvn clean package docker:build
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

#Demo
The Webservice is running on a micro AMI from Amazon EC2. You can test emulating an SNS message as as follows:

```bash
curl -v -H "x-amz-sns-message-type: Notification" -H "Content-type: text/plain" -X POST -d '{"email":"aaa@example.com","name":"Didac","message":"This is a message sent by Merkel","timestamp":"2016-11-21T00:07:40"}' "http://54.201.95.75/notification"
```

#DB
For this environment I am using a in memory H2 DB. Once the backend is running, the DB can be accessed through http://localhost:8082 and then set in the URL the following:
`jdbc:h2:mem:dataSource;Mode=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MV_STORE=FALSE;MVCC=FALSE;IGNORECASE=TRUE`

That will open a web interface where one can see the DB tables and perform queries on it.
