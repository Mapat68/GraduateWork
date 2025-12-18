package ru.netology.graduate.work.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.graduate.work.dto.request.AuthenticationRequest;
import ru.netology.graduate.work.dto.response.AuthenticationResponse;
import ru.netology.graduate.work.jwt.JwtTokenUtil;
import ru.netology.graduate.work.repository.AuthenticationRepo;
import ru.netology.graduate.work.service.UserService;


@RestController
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

    private AuthenticationRepo authenticationRepo;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private UserService userService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRQ) {
        final String username = authenticationRQ.getLogin();
        final String password = authenticationRQ.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails userDetails = userService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        authenticationRepo.putTokenAndUsername(token, username);
        log.info("User {} authentication. JWT: {}", username, token);
        return new AuthenticationResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        final String token = authToken.substring(7);
        final String username = authenticationRepo.getUsernameByToken(token);
        log.info("User {} logout. JWT is disabled.", username);
        authenticationRepo.removeTokenAndUsernameByToken(token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
