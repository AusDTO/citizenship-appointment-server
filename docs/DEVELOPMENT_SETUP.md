#Devlopment Setup

How to start as a developer. Brief description of what you would need to have installed and what dependencies you should have configured to successfully run and contribute to the project.

##Prerequisites

You will need to:

* have git to checkout this repo [how to install Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
* have Java installed [how to install Java?](https://java.com/en/download/help/download_options.xml)

An essential part of the system is integration with a booking service that stores all the data about users, appointments etc. In order to be able to run this application without an integration to a real system, we created a dummy version of it, so this server application can be run without being dependent on the licence we have. The address of the stub is:
        
    The App:  http://citizenship-appointment-stub.herokuapp.com/
    The Code: https://github.com/AusDTO/citizenship-appointment-backend-stub
    
##Essential environment variables

###The values to set

You will need a few environment variables set in order to run the application (the values are just sample values you can set it to):

    #Monitoring tests
    export MONITOR_BASE_URL="http://localhost:8083"
    export MONITOR_CLIENT_ID="11111111111" 
    export MONITOR_FAMILY_NAME="Potter"
    
    # STUB API ENDPOINTS
    export SERVICE_ADDRESS_CUSTOMER="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_USER="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_CALENDAR="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_PROCESS="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_SERVICE="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_UNIT="http://citizenship-appointment-stub.herokuapp.com/"
    export SERVICE_ADDRESS_APPOINTMENT_TYPE="http://citizenship-appointment-stub.herokuapp.com/"
    
    # API_USERS
    export USER_USERNAME_1=“some_user”
    export USER_PASSWORD_1=“some_password”
    export USER_ID_1=“1”
    
    #SYSTEM_ESSENTIAL
    export SECURITY_ADMIN_PASSWORD="whatever"
    export SESSION_ENCRYPTION_KEY="super secret key"
    export ANALYTICS_TRACKING_ID=""
    export SECURITY_ANONYMOUS_TOKEN_KEY="anonymousTokenKey"
    export SESSION_JWT_ENCRYPTION_KEY_BASE64="AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    
###The why
    
The above environment variables are essential to run the application. 

MONITOR_BASE_URL, MONITOR_CLIENT_ID, MONITOR_FAMILY_NAME are storing the details of a sample user that can log in to the system, they are used to run an e2e test checking the ability to book while connected to the booking system.

SERVICE_ADDRESS_CUSTOMER, SERVICE_ADDRESS_USER, SERVICE_ADDRESS_CALENDAR, SERVICE_ADDRESS_PROCESS, SERVICE_ADDRESS_UNIT,SERVICE_ADDRESS_APPOINTMENT_TYPE and SERVICE_ADDRESS_SERVICE are the endpoints of the booking system we are integrating with to store the data about the appointments. 
    
USER_USERNAME_*x*, USER_PASSWORD_*x*, USER_ID_*x*, (where x should be a number) is for managing the users that the system uses to obtain authentication with the API of the booking system.

SECURITY_ADMIN_PASSWORD is for the monitoring endpoint.

SESSION_ENCRYPTION_KEY, SESSION_JWT_ENCRYPTION_KEY_BASE64 and SECURITY_ANONYMOUS_TOKEN_KEY are needed for various security mechanisms we implemented.

ANALYTICS_TRACKING_ID is for the Google Analytics account we integrated with the system.


##Running the application

Once all the above steps are done in the main folder of the project execute

    ./go fullBuild

This will download any required dependencies and run all the tests if everything is successful.

##Next steps

All the commands you can run can be found in the go script located in the root folder of this project. 

    ./go startApp

will start the application. Happy coding!
