package au.gov.dto.dibp.appointments.service.api;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiCallsSenderServiceTest {

    @Mock
    private ApiUserLogInSignOutService apiUserService;

    @Mock
    private HttpClientHandler httpClient;

    @Mock
    private Mustache.Compiler mustache;

    @InjectMocks
    private ApiCallsSenderService service;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        service.resourceLoader = new DefaultResourceLoader();
        when(apiUserService.getApiSessionId()).thenReturn("someSessionId");

        Template mockTemplate = mock(Template.class);
        when(mockTemplate.execute(any())).thenReturn("Sample Message");
        when(mustache.compile(any(Reader.class))).thenReturn(mockTemplate);
    }

    @Test
    public void sendRequest_shouldRequestApiKey() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        verify(apiUserService, times(1)).getApiSessionId();
    }

    @Test
    public void sendRequest_shouldPutSessionApiOnTheTemplate() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Template mockTemplate = mock(Template.class);
        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(mockTemplate.execute(mapArgumentCaptor.capture())).thenReturn("Sample Message");
        when(mustache.compile(any(Reader.class))).thenReturn(mockTemplate);

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        assertThat(mapArgumentCaptor.getValue().get("apiSessionId"), is("someSessionId"));
    }

    @Test
    public void sendRequest_shouldPutServiceAddressOnTheTemplate() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Template mockTemplate = mock(Template.class);
        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        String serviceAddress = "some service address";

        when(mockTemplate.execute(mapArgumentCaptor.capture())).thenReturn("Sample Message");
        when(apiUserService.getApiSessionId()).thenReturn("someSessionId");
        when(mustache.compile(any(Reader.class))).thenReturn(mockTemplate);

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), serviceAddress);
        assertThat(mapArgumentCaptor.getValue().get("serviceAddress"), is(serviceAddress));
    }

    @Test
    public void sendRequest_shouldPutMessageUuidOnTheTemplate() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Template mockTemplate = mock(Template.class);
        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        when(mockTemplate.execute(mapArgumentCaptor.capture())).thenReturn("Sample Message");
        when(apiUserService.getApiSessionId()).thenReturn("someSessionId");
        when(mustache.compile(any(Reader.class))).thenReturn(mockTemplate);

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        assertThat(UUID.fromString((String)mapArgumentCaptor.getValue().get("messageUUID")).toString(), is(notNullValue()));
    }

    @Test
    public void sendRequest_shouldDelegateHttpCallToHttpClientHandler() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        verify(httpClient, times(1)).post(anyString(), anyString());
    }

    @Test
    public void sendRequest_shouldReleaseApiKeyWhenAllOk() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

        service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        verify(apiUserService, times(1)).releaseApiSessionId("someSessionId");
    }

    @Test
    public void sendRequest_shouldReleaseApiKeyWhenExceptionThrown() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Template mockTemplate = mock(Template.class);
        when(mockTemplate.execute(any())).thenThrow(new RuntimeException());
        when(mustache.compile(any(Reader.class))).thenReturn(mockTemplate);

        try {
            service.sendRequest(ClientService.REQUEST_TEMPLATE_PATH, new HashMap<String, String>(), "");
        }catch(RuntimeException e){}

        verify(apiUserService, times(1)).releaseApiSessionId("someSessionId");
        verify(httpClient, times(0)).post(anyString(), anyString());
    }
}
