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

#### Wallet

These environment variables are used for the Apple Wallet implementation:

- `WALLET_PASS_TYPE_IDENTIFIER`: Use the same value supplied when setting up your Pass Type ID. It should start with `pass.`, e.g. `pass.com.apple.devpubs.example`
- `WALLET_TEAM_IDENTIFIER`: Your iOS developer account team identifier, e.g., `A93A5CM278`
- `WALLET_PRIVATE_KEY_P12_BASE64`: The Base64 encoded contents of a PKCS #12 file containing your Pass Type ID private key and certificate. See below for instructions on how to obtain this. 
- `WALLET_PRIVATE_KEY_PASSPHRASE`: The passphrase required to access the contents of the PKCS #12 file containing your Pass Type ID private key and certificate. 

To obtain the value for WALLET_PRIVATE_KEY_P12_BASE64, export your Pass Type ID certificate and corresponding private key as a `.p12` file.

The password you supply when you export should be set as the `WALLET_PRIVATE_KEY_PASSPHRASE` environment variable.
 
Run this command to extract the value to set as the `WALLET_PRIVATE_KEY_P12_BASE64` environment variable, where `export.p12` is the name of the `.p12` file you exported:

    base64 -i export.p12 -o -
    
On Mac OS you can optionally pipe the output of this command to `pbcopy` to add the result to your clipboard.

#### Other

- `SESSION_JWT_ENCRYPTION_KEY_BASE64`: 256-bit Base64 encoded encryption and signature key for storing the user's security context in a cookie
- `SECURITY_ADMIN_PASSWORD`: Password for the read-only monitoring endpoints
- `ANALYTICS_TRACKING_ID`: Analytics tracking ID, optional
- `PUBLIC_KEY_FINGERPRINT_BASE64_1` and `PUBLIC_KEY_FINGERPRINT_BASE64_2`: Base64 encoded SHA-256 fingerprints of the Subject Public Key Information (SPKI) in the public key certificate and the backup certificate signature request for the public host, used for [HTTP Public Key Pinning (HPKP)](https://developer.mozilla.org/en/docs/Web/Security/Public_Key_Pinning), optional, but both must be present to activate HPKP
- `HPKP_REPORT_URI_ENFORCED`: URI for web browsers to send reports on HPKP violations for enforced policies, optional 
- `HPKP_REPORT_URI_REPORT_ONLY`: URI for web browsers to send reports on HPKP violations for report-only policies, optional 
- `CSP_REPORT_URI`: URI for web browsers to send reports on [Content Security Policy (CSP)](http://content-security-policy.com/) violations, optional 

##### Commands

Get fingerprint of public key certificate as a Base64 encoded SHA-265 digest of the DER formatted Subject Public Key Info (SPKI): 

    openssl x509 -pubkey -inform PEM -in certificate.pem -outform DER | openssl dgst -sha256 -binary | base64

### Running the application

To simply run the application:

    ./go startApp
    
You can optionally supply the port number to use as an environment variable:

    PORT=8080 ./go run

The port defaults to 8083. Use `PORT=0` to select a random port.

### Development

To run or debug the application from your IDE, use the `Application` class as the entry point.

### Build

To run the full build:

    ./go fullBuild

### Testing

To run unit tests:

    ./go test

To run integration tests:

    ./go integrationTest

To run all tests:

    ./go allTests

### Security checks
 
Check dependencies against known publicly disclosed security vulnerabilities published by NIST in NVD Data Feeds:

    ./go dependencyCheck --info

### Other

To view other build tasks:

    ./go tasks
