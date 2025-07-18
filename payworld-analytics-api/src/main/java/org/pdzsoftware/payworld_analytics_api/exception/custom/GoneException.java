package org.pdzsoftware.payworld_analytics_api.exception.custom;

import org.springframework.http.HttpStatus;

public class GoneException extends ApiException {
    public GoneException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.GONE;
    }
}
