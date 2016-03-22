# Design

Brief overview of design choices for the citizenship-appointment-server project.

## MVC framework
We are using an MVC framework for implementations of common web conventions to speed up web development. We chose Spring Boot as it is Open Source with wide community support, and can be readily integrated with an existing auth/security framework.

## Authentication
Our choice of a well established MVC framework affords the availability of a mature and widely-used auth/security framework, Spring Security. The ubiquity of this framework and the amount of support it receives allows us faster development with greater confidence that we've created fewer vulnerabilities than if we were to attempt to write our own.

## Zero downtime deployments and no user interruption
In an effort to achieve deployments with zero downtime, sessions are stored in cookies rather than storing them using the HttpSession object. Our session cookies are encrypted, signed, and have an expiry set, and do not contain any personal information.

This allows us to perform blue/green deployments by simply removing an instance and having traffic routed to the other live instance(s), as we do not need to resolve the management of HttpSession objects that would have otherwise been tied to the removed instance.

## Security
We've aimed to follow best practices in addressing security threats such as content injection attacks, eg. cross-site scripting (XSS). We perform input validation, output escaping, and set HTTP headers to address Content Security Policy (CSP), Cross-Site Request Forgery (CSRF), Public Key Pinning (HPKP), and Clickjacking.

## Embedded servlet container
Running standalone applications using embedded servlet containers simplifies the deployment process as there is only ever the need to deploy new versions of the application, even in the case of updating servlet-container versions.

## Client (front-end) dependency
Our client project is being included as a package dependency with client assets copied to our application resources. Decoupling the presentation from the back-end means that we can develop our front-end using front-end tools that are independent of the back-end tech stack.

To enforce this clean separation we've split our front-end and back-end into two separate repositories. 

## Stubbing external APIs
We've stubbed our external API dependencies to allow development to continue in the case of these dependencies experiencing an outage. It also allows us greater control when testing failure scenarios, and means our code can be run against our unit tests without requiring credentials.

## Secrets and environment configuration
Secrets and environment variables for application configuration are stored externally to the repository and application artifacts for t3h s3cur1ty!!!!!11

## Security exploit analysis
We run an exploit analysis tool to alert us of security vulnerabilities in our dependencies, using data from the National Vulnerability Database (NVD) hosted by NIST: https://nvd.nist.gov
