# Auth Server


= = = = = 

:warning: 
at the moment this only returns a short-lived (5 minutes) access token. Needs to be extended to use proper refresh/access token mechanism.

= = = = = 

##### Assumptions/Context

The Registration functionality sends an email with a link to verify the user's email address. 
The Login functionality can be configured from the YML file to either accept all registered users or only those who verified their email. 

The PasswordReset functionality has 2 endpoints: one to request the pw reset and the other to actually change the pw.
The first also sends an email with a token, the link should - in a real scenario - be handled by the Frontend where 
the user sets the new password. Then the link+token could be used to call the endpoint to actually change the password. 
One of the tests shows this workflow.

Emails to verify account and send pw reset link are sent through Google SMTP so 
the account needs to be setup in the `application.yml` file (just email and pw of the GMail account). 
*Notice:* if emails are not received, then you need to *“allow less safe apps”* in that google account settings

Tests are “integration” tests, therefore I wanted to also check I was not screwing the actual email sending functionality 
while developing. So real emails get actually sent.

Upon login, any (old) pw reset request is deleted, as it means the user "recovered" the pw. 
Otherwise we'd store stale request indefinitely (unless removing with some separate cron job).


##### How to start
It is enough to launch the main class. It will run an embedded tomcat; 
A simple instance of MongoDB should be running (just install it and run `mongod`), the default MongoDB port `27017` will be fine. 
Tests will use the embedded MongoDB, so no need to actually run MongoDB when building/testing.

Swagger will be available at `http://localhost:8080/swagger-ui.html`.

To run on HTTPS, recommended in real world scenario, just run 
with `-Dspring.profiles.active=secure` the `application-secure.yml` config file contains configuration to use 
a certificate. In order to call endpoints with cURL the `-k` option needs to be used, otherwise it will 
complain that the certificate is self-signed.

To request a token
```
curl -X POST -H "Content-Type: application/json" -d '{"email": "gabrielefalace@gmail.com", "password": "type_pw_here"}' http://127.0.0.1:8080/login
```
 

##### Further improvement
* Improving names of Endpoints and HTTP methods used
* Unit tests (where things such as the Mail server would be mocked out).
    * Although I think that a few unit test would have been very appropriate here, I preferred to dedicate time to the "integration" ones, 
    to first check the "end-to-end" workflow in a similar fashion to what I was doing manually (aka call endpoint and expect results).
