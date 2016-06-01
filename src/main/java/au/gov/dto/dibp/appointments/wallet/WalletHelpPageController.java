package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@RestController
class WalletHelpPageController {
    @RequestMapping(value = "/wallet/pass/barcode", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView walletBarcodePage(@AuthenticationPrincipal Client client) {
        return new ModelAndView("wallet_barcode_page", new HashMap<String, Object>() {{
            put("clientId", client.getClientId());
            put("customerId", client.getCustomerId());
        }});
    }
}
