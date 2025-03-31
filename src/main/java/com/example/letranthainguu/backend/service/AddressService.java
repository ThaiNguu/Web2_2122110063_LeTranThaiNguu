package com.example.letranthainguu.backend.service;

import java.util.List;

import com.example.letranthainguu.backend.domain.Address;
import com.example.letranthainguu.backend.dto.AddressDTO;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAddresses();

    AddressDTO getAddress(Long addressId);

    AddressDTO updateAddress(Long addressId, Address address);

    String deleteAddress(Long addressId);
}
