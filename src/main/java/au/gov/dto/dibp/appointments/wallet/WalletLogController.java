package au.gov.dto.dibp.appointments.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class WalletLogController {
    private static final Logger LOG = LoggerFactory.getLogger(WalletLogController.class);

    /**
     * Reference: https://developer.apple.com/library/ios/documentation/PassKit/Reference/PassKit_WebService/WebService.html#//apple_ref/doc/uid/TP40011988-CH0-SW7
     */
    @RequestMapping(value = "/wallet/v1/log", method = RequestMethod.POST)
    public void logWalletErrors(@RequestBody String message) {
        LOG.error(message);
    }
}
