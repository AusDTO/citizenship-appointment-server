# citizenship-appointment-server [![Travis CI Build Status](https://travis-ci.org/AusDTO/citizenship-appointment-server.svg?branch=master)](https://travis-ci.org/AusDTO/citizenship-appointment-server)

This is the server code for the citizenship appointment service.  

The client code can be found in this repository: [AusDTO/citizenship-appointment-client](https://github.com/AusDTO/citizenship-appointment-client)

## Instructions

### Prerequisites

- Java 8

### Environment variables

A number of environment variables must be set to run the application

#### Service endpoints

- `SERVICE_ADDRESS_APPOINTMENT_TYPE`: HTTP(S) endpoint for the `svcAppointmentType` SOAP service
- `SERVICE_ADDRESS_CALENDAR`: HTTP(S) endpoint for the `svcCalendar` SOAP service
- `SERVICE_ADDRESS_CUSTOMER`: HTTP(S) endpoint for the `svcCustomer` SOAP service
- `SERVICE_ADDRESS_PROCESS`: HTTP(S) endpoint for the `svcProcess` SOAP service
- `SERVICE_ADDRESS_SERVICE`: HTTP(S) endpoint for the `svcService` SOAP service
- `SERVICE_ADDRESS_UNIT`: HTTP(S) endpoint for the `svcUnit` SOAP service
- `SERVICE_ADDRESS_USER`: HTTP(S) endpoint for the `svcAppUser` SOAP service

#### API users

At least one set of API user credentials must be supplied. `x` is a sequential number starting from `1`.

- `USER_USERNAME_x`: Username of API user `x` 
- `USER_PASSWORD_x`: Password of API user `x`
- `USER_ID_x`: ID of API user `x`

#### Other

- `SESSION_JWT_ENCRYPTION_KEY_BASE64`: 256-bit Base64 encoded encryption and signature key for storing the user's security context in a cookie
- `SECURITY_ADMIN_PASSWORD`: Password for the read-only monitoring endpoints
- `ANALYTICS_TRACKING_ID`: Analytics tracking ID, optional
- `PUBLIC_KEY_FINGERPRINT_BASE64_1` and `PUBLIC_KEY_FINGERPRINT_BASE64_2`: Base64 encoded SHA-256 fingerprints of the Subject Public Key Information (SPKI) in the public key certificate and the backup certificate signature request for the public host, used for [HTTP Public Key Pinning (HPKP)](https://developer.mozilla.org/en/docs/Web/Security/Public_Key_Pinning), optional, but both must be present to activate HPKP
- `HPKP_REPORT_URI`: URI for web browsers to send reports on HPKP violations, optional 
- `CSP_REPORT_URI`: URI for web browsers to send reports on [Content Security Policy (CSP)](http://content-security-policy.com/) violations, optional 

##### Commands

Get fingerprint of public key certificate as a Base64 encoded SHA-265 digest of the DER formatted Subject Public Key Info (SPKI): 

    openssl x509 -pubkey -inform PEM -in certificate.pem -outform DER | openssl dgst -sha256 -binary | base64

### Running the application

To simply run the application:

    ./gradlew run
    
You can optionally supply the port number to use as an environment variable:

    PORT=8080 ./gradlew run

The port defaults to 8083. Use `PORT=0` to select a random port.

### Development

To run or debug the application from your IDE, use the `Application` class as the entry point.

### Build

To run the full build:

    ./gradlew clean build

### Testing

To run unit tests:

    ./gradlew test

To run integration tests:

    ./gradlew integrationTest

To run all tests:

    ./gradlew check

### Other

To view other build tasks:

    ./gradlew tasks
