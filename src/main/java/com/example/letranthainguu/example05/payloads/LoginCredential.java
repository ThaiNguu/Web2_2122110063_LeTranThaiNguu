package com.example.letranthainguu.example05.payloads;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredential {
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
}
