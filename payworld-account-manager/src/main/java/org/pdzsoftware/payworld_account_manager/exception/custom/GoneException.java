package org.pdzsoftware.payworld_account_manager.exception.custom;

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
