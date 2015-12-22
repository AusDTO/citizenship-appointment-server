package au.gov.dto.dibp.appointments.service.api;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;

import java.util.Map;

public interface ApiCallsSenderService {
    ResponseWrapper sendRequest(String requestTemplatePath, Map<String, String> messageParams, String serviceAddress);
}
