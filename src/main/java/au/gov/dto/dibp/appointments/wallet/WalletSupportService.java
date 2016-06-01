package au.gov.dto.dibp.appointments.wallet;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class WalletSupportService {
    public boolean supportsWallet(String userAgentHeaderValue) {
        if (StringUtils.isBlank(userAgentHeaderValue)) {
            return false;
        }
        UserAgent userAgent = new UserAgent(userAgentHeaderValue);
        if (userAgent.getOperatingSystem().getDeviceType() == DeviceType.MOBILE
                && userAgent.getOperatingSystem().getGroup() == OperatingSystem.IOS
                && userAgent.getOperatingSystem().getId() >= OperatingSystem.iOS6_IPHONE.getId()) {
            return true;
        }
        Version oldestSafariVersionWithGuaranteedSupport = new Version("6.2", "6", "2"); // Mac OS X 10.8.2 or later required to install this Safari version
        if (userAgent.getOperatingSystem() == OperatingSystem.MAC_OS_X
                && userAgent.getBrowser().getGroup() == Browser.SAFARI
                && userAgent.getBrowserVersion().compareTo(oldestSafariVersionWithGuaranteedSupport) >= 0) {
            return true;
        }
        return false;
    }

}
