

Application is configured to work with H2 inmemory database - no configuration is needed.

To run application in Tomcat Embeded mode:
mvn spring-boot:run

To package application as war file deployable to App server/container:
mvn package

On first run database is populated with following credentials:
    admin:12345  - role ADMIN
    user1:12345  - role USER
    user2:12345  - role USER


#sample json files to use in requests

(all files located in root folder) 
accoun1.json
register1.json
register2.json


#CREATE ACCOUNT (no authorization)
curl  -H  "Content-Type: application/json" -X POST  http://localhost:8081/account  -d @account1.json


#REGISTER URL (authorization with admin )
curl  -u admin:12345 -H  "Content-Type: application/json" -X POST  http://localhost:8081/register  -d @register2.json



#TRY REGISTER NOT VALID URL (authorization with admin )
curl  -u admin:12345 -H  "Content-Type: application/json" -X POST  http://localhost:8081/register  -d @register1.json



#GET STATISTICS FOR admin (authorization with admin )
curl  -u admin:12345 -H  "Content-Type: application/json" -H "AccountId: admin"  http://localhost:8081/statistic


#TO CHECK REDIRECTS
curl  -I -u admin:12345 -H  "Content-Type: application/json"  http://localhost:8081/GENERATED_URL_FOR_SPECIFIED_USER

in browser  just top put URL and to authorzie in browser:
http://localhost:8081/GENERATED_URL_FOR_SPECIFIED_USER
