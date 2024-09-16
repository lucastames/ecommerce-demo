package org.tames.ecommercecrud.modules.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginUserRequestDto(
    @NotEmpty @Size(max = 127) String username, @NotEmpty @Size(max = 127) String password) {}
