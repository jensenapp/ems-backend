package com.jensen.ems.dto;

public record LoginResponseDto(String message,
                               AccountResponseDto user,
                               String jwtToken) {
}
