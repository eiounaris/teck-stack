package com.eiou.mall.common.web;

import com.eiou.mall.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Health")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Operation(summary = "Health check")
    @GetMapping
    public ApiResponse<HealthResponse> health() {
        return ApiResponse.ok(new HealthResponse("UP", "mall-lite-order", LocalDateTime.now()));
    }

    public record HealthResponse(String status, String application, LocalDateTime checkedAt) {
    }
}
