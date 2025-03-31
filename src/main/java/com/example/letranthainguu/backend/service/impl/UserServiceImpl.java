package com.example.letranthainguu.backend.service.impl;

import java.util.List;
import java.util.HashSet;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.example.letranthainguu.backend.config.AppConstants;
import com.example.letranthainguu.backend.domain.Address;
import com.example.letranthainguu.backend.domain.Cart;
import com.example.letranthainguu.backend.domain.Role;
import com.example.letranthainguu.backend.domain.User;
import com.example.letranthainguu.backend.exceptions.APIException;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.dto.AddressDTO;
import com.example.letranthainguu.backend.dto.CartDTO;
import com.example.letranthainguu.backend.dto.ProductDTO;
import com.example.letranthainguu.backend.dto.UserDTO;
import com.example.letranthainguu.backend.dto.UserReponse;
import com.example.letranthainguu.backend.repository.AddressRepo;
import com.example.letranthainguu.backend.repository.RoleRepo;
import com.example.letranthainguu.backend.repository.UserRepo;
import com.example.letranthainguu.backend.service.CartService;
import com.example.letranthainguu.backend.service.UserService;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        // Check if user exists with email
        if (userRepo.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new APIException("User already exists with emailId: " + userDTO.getEmail());
        }

        try {
            User user = modelMapper.map(userDTO, User.class);
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);

            // Create new HashSet for roles to avoid session issues
            user.setRoles(new HashSet<>());
            
            // Get role by ID and add to user's roles
            Role role = roleRepo.getReferenceById(AppConstants.USER_ID);
            user.getRoles().add(role);

            String country = userDTO.getAddress().getCountry();
            String state = userDTO.getAddress().getState();
            String city = userDTO.getAddress().getCity();
            String pincode = userDTO.getAddress().getPincode();
            String street = userDTO.getAddress().getStreet();
            String buildingName = userDTO.getAddress().getBuildingName();

            Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                country, state, city, pincode, street, buildingName);

            if (address == null) {
                address = new Address(country, state, city, pincode, street, buildingName);
                address = addressRepo.save(address);
            }

            user.setAddresses(List.of(address));
            User registeredUser = userRepo.save(user);

            userDTO = modelMapper.map(registeredUser, UserDTO.class);
            userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
            return userDTO;

        } catch (DataIntegrityViolationException e) {
            throw new APIException("Error registering user: " + e.getMessage());
        } catch (Exception e) {
            throw new APIException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public UserReponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<User> pageUsers = userRepo.findAll(pageDetails);

        List<User> users = pageUsers.getContent();

        if (users.size() == 0) {

            throw new APIException("No User exists !!!");
        }

        List<UserDTO> userDTOS = users.stream().map(user -> {

            UserDTO dto = modelMapper.map(user, UserDTO.class);

            if (user.getAddresses().size() != 0) {

                dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
            }

            // CartDTO cart modelMapper.map(user.getCart(), CartDTO.class);

            // List<ProductDTO> products user.getCart().getCartItems().stream()

            // .map(item> modelMapper.map(item.getProduct(),

            // ProductDTO.class)).collect(Collectors.toList());

            // dto.setCart(cart);

            // dto.getCart().setProducts (products);

            return dto;
        }).collect(Collectors.toList());

        UserReponse userResponse = new UserReponse();

        userResponse.setContent(userDTOS);

        userResponse.setPageNumber(pageUsers.getNumber());

        userResponse.setPageSize(pageUsers.getSize());

        userResponse.setTotalElements(pageUsers.getTotalElements());

        userResponse.setTotalPages(pageUsers.getTotalPages());

        userResponse.setLastPage(pageUsers.isLast());

        return userResponse;
    }

    @Override
    public UserDTO getUserByEmail(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
        }

        if (user.getCart() != null) {
            CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);

            List<ProductDTO> products = user.getCart().getCartItems().stream()
                    .map(item -> modelMapper.map(item.getProduct(),
                            ProductDTO.class)).collect(Collectors.toList());

            cart.setProducts(products);
            userDTO.setCart(cart);
        }

        return userDTO;
    }

    @Override
    public UserDTO getUserById(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

        // CartDTO cart modelMapper.map(user.getCart(), CartDTO.class);

        // List<ProductDTO> products user.getCart().getCartItems().stream()

        // .map(item-> modelMapper.map(item.getProduct(),

        // ProductDTO.class)).collect(Collectors.toList());

        // userDTO.setCart(cart);

        // userDTO.getCart().setProducts (products);

        return userDTO;
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Kiểm tra mật khẩu cũ nếu đang thay đổi mật khẩu
        if (userDTO.getOldPassword() != null && userDTO.getPassword() != null) {
            if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
                throw new APIException("Mật khẩu hiện tại không đúng!");
            }
            // Mã hóa mật khẩu mới
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setFirstName(userDTO.getFirstName());

        user.setLastName(userDTO.getLastName());

        user.setMobileNumber(userDTO.getMobileNumber());

        user.setEmail(userDTO.getEmail());

        if (userDTO.getAddress() != null) {
            String country = userDTO.getAddress().getCountry();

            String state = userDTO.getAddress().getState();

            String city = userDTO.getAddress().getCity();

            String pincode = userDTO.getAddress().getPincode();

            String street = userDTO.getAddress().getStreet();

            String buildingName = userDTO.getAddress().getBuildingName();

            Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
                    city, pincode, street, buildingName);

            if (address == null) {
                address = new Address(country, state, city, pincode, street, buildingName);

                address = addressRepo.save(address);

                user.setAddresses(List.of(address));
            }
        }

        userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

        // CartDTO cart modelMapper.map(user.getCart(), CartDTO.class);

        // List<ProductDTO> products user.getCart().getCartItems().stream()

        // .map(item-> modelMapper.map(item.getProduct(), ProductDTO.class)).collect

        // (Collectors.toList());

        // userDTO.setCart(cart);

        // userDTO.getCart().setProducts (products);

        return userDTO;
    }

    @Override
    public String deleteUser(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // List<CartItem> cartItems user.getCart().getCartItems();

        // Long cartId user.getCart().getCartId();

        // cartItems.forEach(item -> {

        // Long productId item.getProduct().getProductId();

        // cartService.deleteProductFromCart(cartId, productId);

        // });

        userRepo.delete(user);

        return "User with userId " + userId + " deleted successfully!!!";
    }
}
