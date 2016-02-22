package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import java.util.Base64;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SecurityCookieServiceTest {

    @Test
    public void createCookieFromClientAndExtractAuthentication() throws Exception {
        SecurityCookieService securityCookieService = new SecurityCookieService("key", new AuthenticationSerializer(new ObjectMapper()), new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier()));
        Client inClient = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        Cookie sessionCookie = securityCookieService.createSecurityCookie(inClient);
        Authentication outAuthentication = securityCookieService.getAuthenticationFrom(sessionCookie);
        Client outClient = (Client) outAuthentication.getPrincipal();

        assertThat(outClient.getClientId(), equalTo(inClient.getClientId()));
    }

    @Test
    public void extractAuthenticationFromCookieCreatedUsingAuthenticationSerializer() throws Exception {
        SecurityCookieService securityCookieService = new SecurityCookieService("key", new AuthenticationSerializer(new ObjectMapper()), new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier()));

        // sessionCookie payload created using Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true)
        Cookie sessionCookie = new Cookie("cookieName", "bkIKYEH7cs17f1WBOWXHB_pmYGv94dlISE5huYblm0hc988mRhK9v1El5wt86OGsbeuifdEMQ9nSuGFtLGpMvdQpPruhVOpkB_8iCsOHB-ZgHUl8I9-4SHpoSUxGX14HGSfvxNtk3_L6ccw77Xc4QDdsjiAX6joa6L_iFjTE1TZR1yQmdaL0Lr9v8DCqXB3ylQDz75PYaMKeIyILIfHSjngPsrMBZs2uP4ln-4V7nmwNMif7GopQyMZEBRW4RMbSeyJyQMs6ZrDst4mvGhc8nVFpXS0KtlpqqngZUTLZMRA6CcAiStsDS_K9TqWm6-D9il1OusDSOkEiAYKk6Wrf8DxQJhzwyTVnmBqFJ1vE5B7xybstRsXDTJMxyV0Yb0lSFdUVe-CBwnOkz8hYCwbxfJNhN-r83Qqf8-ym2Gn4kFA");
        Authentication outAuthentication = securityCookieService.getAuthenticationFrom(sessionCookie);
        Client outClient = (Client) outAuthentication.getPrincipal();

        assertThat(outClient.getClientId(), equalTo("clientId"));
    }

    @Test
    public void returnNullAuthenticationORThrowExceptionOnSignatureVerificationFailure() throws Exception {
        SecurityCookieService securityCookieService = new SecurityCookieService("key", new AuthenticationSerializer(new ObjectMapper()), new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier()));
        Cookie sessionCookie = new Cookie("cookieName", "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..UCRfAmvUzVIm5-KV.EsI59i_cQlp31TfLroXMFQrFBmwzTIXzfuCFYDzrDkt7A-bHvRxGOv4n_Mt-OtcDOUrgPoBrqsXdfo-4T813yqKI_ejYvyy-huN8ZeuefhBOHMWiB3VjyzuJkS75dw_3YHEX2gSLhKhxp1Fi4ZjTtU6itCp9D6cE1eGDhdBRNwv-ggoY5o6TS952rQdaza72mwCMq1mtC6Owy3zgncsO9y340d3jspwRxdMTtgK_8aES0ozuAHgHO68gsQUZoCxcqdFhe5g-P69ErCaajXLu4KlCWt-VfH0fU1x2aj9hiqlImTPZAh5oyPYNwRzDteeyewnHyRDbpTZbaYpn7AUrSLSViBQBit--qNDIYEZWw2Itd8JAJcE6dzrbMbepcw8DfluUm-MlLG7HLyQ--hZKP70wGXyp6h_1FxxPNGkoR6B2a3TLbC3N1L2nIKynwA259g_MRz-SMr-pLjbhhmtCiusKm_Iv_6ym_6iYSNF2zicEwzlJNtJ9UacaR7SW89bPDiKfiawf1hnD9ICdK6AXs-JgIh-f9bLR9l578nZsfMhTcfa6Ysp578yOzQcsLM41Q71ArAvsiUXVRYEKAin0yQDEjFlopOVLn94Jf_KPEN4NQZ7iCL2xE6XXW-XnsfTyBa1GJwuaRcqGz-M1SZD0BDpkl3XBB0kcplFrF0JgPKUoupv8QCnCI1gXposW3UCauB55VbC7wM0j3Po0hAf7KH8hpnw8VxPHt7rxHa89hD5rpgiAmvQaD6sZe3AVwH-b.sX_uMKN34PkGIajUst7LZQ");

        Authentication outAuthentication = securityCookieService.getAuthenticationFrom(sessionCookie);
        assertThat(outAuthentication, nullValue());
    }

    @Test
    public void returnNullAuthenticationOnExpiredJwt() throws Exception {
        SecurityCookieService securityCookieService = new SecurityCookieService("key", new AuthenticationSerializer(new ObjectMapper()), new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier()));
        Cookie sessionCookie = new Cookie("cookieName", "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg");

        Authentication outAuthentication = securityCookieService.getAuthenticationFrom(sessionCookie);
        assertThat(outAuthentication, nullValue());
    }


}
