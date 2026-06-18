package com.ashi.sslserverPinning.model;

public record PinValidationResponse(
        boolean matched,
        String providedPin,
        String expectedPin,
        String message
) {
}

