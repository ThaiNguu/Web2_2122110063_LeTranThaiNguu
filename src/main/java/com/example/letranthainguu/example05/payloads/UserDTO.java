package com.example.letranthainguu.example05.payloads;

import java.util.HashSet;
import java.util.Set;

import com.example.letranthainguu.example05.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String password;
    private String oldPassword;
    private Set<Role> roles = new HashSet<>();
    private AddressDTO address;
    private CartDTO cart;

    public void setCart(CartDTO cart) {
        this.cart = cart;
    }
}
