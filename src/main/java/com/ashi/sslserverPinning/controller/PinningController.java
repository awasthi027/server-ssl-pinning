package com.ashi.sslserverPinning.controller;

import com.ashi.sslserverPinning.model.PinValidationRequest;
import com.ashi.sslserverPinning.model.PinValidationResponse;
import com.ashi.sslserverPinning.service.ServerPinningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pinning")
public class PinningController {

    private final ServerPinningService serverPinningService;

    public PinningController(ServerPinningService serverPinningService) {
        this.serverPinningService = serverPinningService;
    }

    @GetMapping("/server-pin")
    public ResponseEntity<Map<String, String>> getServerPin() {
        return ResponseEntity.ok(Map.of(
                "pin", serverPinningService.getExpectedSha256Pin(),
                "sha256Hex", serverPinningService.getCertificateSha256Hex(),
                "note", "Use pin value in /api/pinning/validate to simulate client pin check"
        ));
    }

    @PostMapping("/validate")
    public ResponseEntity<PinValidationResponse> validatePin(@RequestBody PinValidationRequest request) {
        boolean matched = serverPinningService.isPinValid(request.pin());

        PinValidationResponse response = new PinValidationResponse(
                matched,
                request.pin(),
                serverPinningService.getExpectedSha256Pin(),
                matched ? "Pin matches server certificate" : "Pin does not match server certificate"
        );

        return ResponseEntity.ok(response);
    }
}

