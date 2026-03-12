package com.jensen.ems.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
