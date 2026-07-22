package com.foodtable.express.auth.model.register.dto;

import lombok.Builder;

@Builder
public record ResponseUserDto(String id, String email) {
}
