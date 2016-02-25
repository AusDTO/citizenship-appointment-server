package au.gov.dto.dibp.appointments.qflowintegration;

import au.gov.dto.dibp.appointments.util.ResponseWrapper;

public class ApiResponseNotSuccessfulException extends RuntimeException {
    private final ResponseWrapper response;

    public ApiResponseNotSuccessfulException(String message, ResponseWrapper responseRecived){
        super(message);
        this.response = responseRecived;
    }

    public ResponseWrapper getResponse(){
        return response;
    }
}
