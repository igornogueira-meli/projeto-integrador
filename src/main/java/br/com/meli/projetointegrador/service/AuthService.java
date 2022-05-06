package br.com.meli.projetointegrador.service;

import java.util.*;

import br.com.meli.projetointegrador.exception.InexistentWarehouseException;
import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.request.SignupRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import br.com.meli.projetointegrador.model.response.MessageResponse;
import br.com.meli.projetointegrador.repository.*;
import br.com.meli.projetointegrador.security.jwt.JwtUtils;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Classe de serviço responsável por processar  as autenticações.
 * @author Arthur Guedes de Souza
 */
@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    StockManagerRepository stockManagerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;



    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority item : userDetails.getAuthorities()) {
            String authority = item.getAuthority();
            roles.add(authority);
        }
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }


    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (userRepository.existsByCpf(signUpRequest.getCpf())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: CPF is already in use!"));
        }

        User user = new User(signUpRequest.getName(),
                signUpRequest.getCpf(),
                signUpRequest.getAddress(),
                signUpRequest.getEmail(),
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "manager":
                        Role stockManagerRole = roleRepository.findByName(ERole.ROLE_STOCK_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(stockManagerRole);
                        Warehouse warehouse = warehouseRepository.findById(signUpRequest.getWarehouse_id())
                                .orElseThrow(() -> new InexistentWarehouseException("The informed Warehouse does not exist!") );

                        StockManager stockManager = new StockManager(user, warehouse);
                        stockManagerRepository.save(stockManager);
                        break;

                    case "seller":
                        Role sellerRole = roleRepository.findByName(ERole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        Seller seller = new Seller(user);
                        sellerRepository.save(seller);
                        break;

                    default:
                        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(customerRole);
                        Customer customer = new Customer(user);
                        customerRepository.save(customer);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}