package org.tames.ecommercecrud.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDto(
    @NotEmpty @Size(max = 127) String username,
    @NotEmpty @Size(max = 255) @Email String email,
    @NotEmpty @Size(max = 127) String password,
    @NotEmpty String phoneNumber) {}
