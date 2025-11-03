package ru.netology.graduate.work.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.graduate.work.dto.request.AuthenticationRequest;
import ru.netology.graduate.work.dto.response.AuthenticationResponse;
import ru.netology.graduate.work.jwt.JwtTokenUtil;
import ru.netology.graduate.work.repository.AuthenticationRepo;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private AuthenticationRepo authenticationRepo;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private UserService userService;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        final String username = authenticationRequest.getLogin();
        final String password = authenticationRequest.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails userDetails = userService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        authenticationRepo.putTokenAndUsername(token, username);
        log.info("User {} authentication. JWT: {}", username, token);
        return new AuthenticationResponse(token);
    }

    public void logout(String authToken) {
        final String token = authToken.substring(7);
        final String username = authenticationRepo.getUsernameByToken(token);
        log.info("User {} logout. JWT is disabled.", username);
        authenticationRepo.removeTokenAndUsernameByToken(token);
    }
}
