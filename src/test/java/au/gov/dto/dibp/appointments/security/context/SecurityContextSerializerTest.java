package au.gov.dto.dibp.appointments.security.context;

import au.gov.dto.dibp.appointments.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.http.Cookie;
import java.security.SecureRandom;
import java.util.Base64;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SecurityContextSerializerTest {
    @Test
    public void createSecurityContextWithAuthenticationFromSessionCookieOnRequest() throws Exception {
        JwtClientSerializer jwtClientSerializer = createJwtClientSerializer();
        SecurityContextSerializer securityContextSerializer = new SecurityContextSerializer(jwtClientSerializer);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Client inClient = new Client("clientId", "familyName", "customerId", true, true, "unitId", "serviceId", "appointmentTypeId", true);
        String jwtToken = jwtClientSerializer.serialize(inClient);
        Cookie sessionCookie = new Cookie(SecurityContextSerializer.COOKIE_NAME, jwtToken);
        request.setCookies(sessionCookie);

        SecurityContext securityContext = securityContextSerializer.deserialize(request, response);

        Authentication authentication = securityContext.getAuthentication();
        Client outClient = (Client) authentication.getPrincipal();
        assertThat(outClient.getClientId(), equalTo(inClient.getClientId()));
    }

    @Test
    public void returnNullAuthenticationORThrowExceptionOnSignatureVerificationFailure() throws Exception {
        SecurityContextSerializer securityContextSerializer = new SecurityContextSerializer(createJwtClientSerializer());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String jwtTokenWithInvalidSignature = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..UCRfAmvUzVIm5-KV.EsI59i_cQlp31TfLroXMFQrFBmwzTIXzfuCFYDzrDkt7A-bHvRxGOv4n_Mt-OtcDOUrgPoBrqsXdfo-4T813yqKI_ejYvyy-huN8ZeuefhBOHMWiB3VjyzuJkS75dw_3YHEX2gSLhKhxp1Fi4ZjTtU6itCp9D6cE1eGDhdBRNwv-ggoY5o6TS952rQdaza72mwCMq1mtC6Owy3zgncsO9y340d3jspwRxdMTtgK_8aES0ozuAHgHO68gsQUZoCxcqdFhe5g-P69ErCaajXLu4KlCWt-VfH0fU1x2aj9hiqlImTPZAh5oyPYNwRzDteeyewnHyRDbpTZbaYpn7AUrSLSViBQBit--qNDIYEZWw2Itd8JAJcE6dzrbMbepcw8DfluUm-MlLG7HLyQ--hZKP70wGXyp6h_1FxxPNGkoR6B2a3TLbC3N1L2nIKynwA259g_MRz-SMr-pLjbhhmtCiusKm_Iv_6ym_6iYSNF2zicEwzlJNtJ9UacaR7SW89bPDiKfiawf1hnD9ICdK6AXs-JgIh-f9bLR9l578nZsfMhTcfa6Ysp578yOzQcsLM41Q71ArAvsiUXVRYEKAin0yQDEjFlopOVLn94Jf_KPEN4NQZ7iCL2xE6XXW-XnsfTyBa1GJwuaRcqGz-M1SZD0BDpkl3XBB0kcplFrF0JgPKUoupv8QCnCI1gXposW3UCauB55VbC7wM0j3Po0hAf7KH8hpnw8VxPHt7rxHa89hD5rpgiAmvQaD6sZe3AVwH-b.sX_uMKN34PkGIajUst7LZQ";
        Cookie sessionCookie = new Cookie(SecurityContextSerializer.COOKIE_NAME, jwtTokenWithInvalidSignature);
        request.setCookies(sessionCookie);

        SecurityContext securityContext = securityContextSerializer.deserialize(request, response);

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    @Test
    public void returnNullAuthenticationOnExpiredJwt() throws Exception {
        SecurityContextSerializer securityContextSerializer = new SecurityContextSerializer(createJwtClientSerializer());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String expiredJwtToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..v3OyQykgTQI5U7gP.dKsmMKX1MHGoMx2rXrCCWOCbyax-J8JS6gu63OBXEDm7Ab926OwlwlZcvoOZGW5nO7ZR95h2pe8pQs8s8cqWJUO4L4dGI9jTj4jK_Lsy9cPWDY4BMzs2bVBuasn88OQYjC-3zuZyvPKfQHrSVS9OjTaMLeMBwMfKP-k3IysOUfUtWUNcRb86v7VCnOd0ATljXUN8DekK8iZ0wD5AtBJVaOQLbaNWiXGY2pnA2eOW9cI_vPbCqqn4ZW-r7sEy6UzHgXYgRAr4bKb7abVtRvO1Xg3CcpquE597Om0bKJIk-VVCz7fVzpz5rkp16vzN-RKBJBs2MK-UsXKD9Lkgedh5w--Q4muiWrAqA5_Tx36mvkESlzR5pbsKu84ZweE5dfen47q_BWaZguVb8jFJB1pofpEgNiZ1C1K8aKIO03CIR-cOOfvoPrsdte-0M4F5bq4KwLna8fYm9D3OeJN3sai3Ba2KKPtLsfz-F5jJlCOV44JE-F9Pqa1xfdpD_S5UenWFi9IUsM912BoCTX4ouEMP6ZUVHwKgTeFjInJXe6iJVqvhPfrWUeVUBmBURy_8XGrzW12GqN_Qp_-275gQ_jlQfyMsdtkLdMp9YxpIbPb4Whq0ey5eKvy924Z4aWKQcw6SrVPAhFjXbvtwGVJYv2lzQ2vQIDE9g1dxqPpRvAG_qb_4M3Xfhtjo2W1Md-U1Oo5cfDsrbqeeegeYDH_AA5t5tJxLDB7TtR8xtjFb52WNItxcKeMnb6jegAwWlEjAkAqY.1d7Z0BNKOegXeUI_fY8yQg";
        Cookie sessionCookie = new Cookie(SecurityContextSerializer.COOKIE_NAME, expiredJwtToken);
        request.setCookies(sessionCookie);

        SecurityContext securityContext = securityContextSerializer.deserialize(request, response);

        assertThat(securityContext.getAuthentication(), nullValue());
    }

    private JwtClientSerializer createJwtClientSerializer() {
        return new JwtClientSerializer(Base64.getEncoder().encodeToString(new byte[32]), new ObjectMapper(), new ExpirationJwtClaimsVerifier());
    }

    //    @Test
    public void createSecureKey() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println("export SESSION_JWT_ENCRYPTION_KEY_BASE64=\"" + encodedKey + "\"");
    }
}
