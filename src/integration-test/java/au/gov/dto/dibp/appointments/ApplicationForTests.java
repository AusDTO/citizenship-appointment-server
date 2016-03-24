package au.gov.dto.dibp.appointments;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

class ApplicationForTests {
    private ConfigurableApplicationContext context;

    void runTestApplication(){
        context =  SpringApplication.run(Application.class);
    }

    void stopApplication(){
        SpringApplication.exit(context);
    }
}
