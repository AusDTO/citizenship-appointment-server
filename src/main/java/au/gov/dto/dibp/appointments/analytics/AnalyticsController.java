package au.gov.dto.dibp.appointments.analytics;

import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.util.TemplateLoader;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AnalyticsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsController.class);
    private final String trackingId;
    private final TemplateLoader templateLoader;

    @Autowired
    public AnalyticsController(TemplateLoader templateLoader,
                               @Value("${analytics.tracking.id:}") String trackingId){
        this.templateLoader = templateLoader;
        this.trackingId = trackingId;
    }

    @RequestMapping(value = "/analytics_basic.js", method = RequestMethod.GET, produces = "application/javascript")
    public @ResponseBody
    ResponseEntity<String> getAnalyticsCodeForNotAuthUser() {

        Map<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        Template template = templateLoader.loadTemplate("analytics.mustache");
        String requestBody = template.execute(model);

        return new ResponseEntity<>(requestBody,  new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/analytics_auth.js", method = RequestMethod.GET, produces = "application/javascript")
    public @ResponseBody
    ResponseEntity<String> getAnalyticsCodeForAuthUser(@AuthenticationPrincipal Client client) {

        Map<String, Object> model = new HashMap<>();
        model.put("trackingId", trackingId);
        if(client != null) {
            model.put("clientId", client.getClientId());
            model.put("unitId", client.getUnitId());
        }

        Template template = templateLoader.loadTemplate("analytics.mustache");
        String requestBody = template.execute(model);

        return new ResponseEntity<>(requestBody,  new HttpHeaders(), HttpStatus.OK);
    }
}
