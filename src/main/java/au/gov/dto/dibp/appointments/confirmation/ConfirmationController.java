package au.gov.dto.dibp.appointments.confirmation;


import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetails;
import au.gov.dto.dibp.appointments.appointmentdetails.AppointmentDetailsService;
import au.gov.dto.dibp.appointments.client.Client;
import au.gov.dto.dibp.appointments.wallet.WalletSupportService;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestController
public class ConfirmationController {

    private static final DateTimeFormatter APPOINTMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");
    private static final DateTimeFormatter APPOINTMENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    private final AppointmentDetailsService appointmentDetailsService;
    private final WalletSupportService walletSupportService;

    @Autowired
    public ConfirmationController(AppointmentDetailsService appointmentDetailsService,
                                  WalletSupportService walletSupportService){
        this.appointmentDetailsService = appointmentDetailsService;
        this.walletSupportService = walletSupportService;
    }

    @RequestMapping(value = "/confirmation", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView getConfirmationPage(@AuthenticationPrincipal Client client,
                                            HttpServletRequest request) throws UnsupportedEncodingException {
        AppointmentDetails appointmentDetails = appointmentDetailsService.getExpectedAppointmentForClientForNextYear(client);

        if(appointmentDetails == null){
            return new ModelAndView("redirect:/calendar?error", new HashMap<>());
        }

        HashMap<String, Object> model = new HashMap<>();

        String unitAddress = appointmentDetails.getUnitAddress();
        model.put("location", unitAddress);
        model.put("locationURL", URLEncoder.encode("Visa and Citizenship Office, " + unitAddress, "UTF-8"));
        model.put("clientId", client.getClientId());
        model.put("customerId", client.getCustomerId());
        model.put("hasEmail", client.isEmail());
        model.put("hasMobile", client.isMobile());

        model.put("appointment_date", appointmentDetails.getAppointmentDate().format(APPOINTMENT_DATE_FORMATTER));
        model.put("appointment_time", appointmentDetails.getAppointmentDate().format(APPOINTMENT_TIME_FORMATTER));

        String userAgentHeaderValue = request.getHeader("user-agent");
        model.put("supportsWallet", supportsWallet(userAgentHeaderValue));
        model.put("useWalletModal", useWalletModal(userAgentHeaderValue));

        return new ModelAndView("confirmation_page", model);
    }

    private boolean supportsWallet(String userAgentHeaderValue) {
        return walletSupportService.supportsWallet(userAgentHeaderValue);
    }

    private boolean useWalletModal(String userAgentHeaderValue) {
        return !supportsWallet(userAgentHeaderValue) && !isMobileOrUnknownDevice(userAgentHeaderValue);
    }

    private boolean isMobileOrUnknownDevice(String userAgentHeaderValue) {
        if (StringUtils.isBlank(userAgentHeaderValue)) {
            return false;
        }
        UserAgent userAgent = new UserAgent(userAgentHeaderValue);
        return userAgent.getOperatingSystem().getDeviceType() == DeviceType.MOBILE;
    }

}
