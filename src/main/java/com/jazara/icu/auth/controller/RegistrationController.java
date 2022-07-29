package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.User;
import com.jazara.icu.auth.payload.LoginRequest;
import com.jazara.icu.auth.service.CustomResponse;
import com.jazara.icu.auth.service.JwtTokenUtil;
import com.jazara.icu.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class RegistrationController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomResponse customResponse;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    public RegistrationController() {
        super();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) throws Exception {

        Map<String, Object> tokenMap = new HashMap<String, Object>();
        try {
            userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());

            User appUser = userService.findUserByUsername(loginRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, Object> resultTokenMap = new HashMap<String, Object>();

            if (token != null) {
                resultTokenMap.put("token", token);
                resultTokenMap.put("user", appUser);

                return customResponse.HandleResponse(true, null, resultTokenMap, HttpStatus.OK);
            } else {
                return customResponse.HandleResponse(false, "Invalid Token", null, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/checkjwttoken")
    public ResponseEntity<Map<String, Object>> checktoken(@RequestParam String token) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        try {
            if (jwtTokenUtil.isTokenExpired(token))
                return customResponse.HandleResponse(false, "Expired Token", null, HttpStatus.UNAUTHORIZED);
            else {
                return customResponse.HandleResponse(true, null, null, HttpStatus.OK);
            }
        } catch (ExpiredJwtException e) {
            return customResponse.HandleResponse(false, "Expired Token", null, HttpStatus.UNAUTHORIZED);
        }

    }

    //get logged in user
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        return customResponse.HandleResponse(true, null, userService.findUserByUsername(loggedUsername), HttpStatus.OK);
    }

    //get logged in user id
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getLoggedUserID() {
        return customResponse.HandleResponse(true, null, userService.getLoggedUserId(), HttpStatus.OK);
    }

    // Registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUserAccount(@RequestBody User accountDto) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.save(accountDto);
        if (registered == null) {
            return customResponse.HandleResponse(false, "couldn't register account", null, HttpStatus.OK);
        }
        LOGGER.info("registered account : " + registered.getEmail());
        return customResponse.HandleResponse(true, null, registered, HttpStatus.OK);
    }

    // activation
    @PostMapping(value = "/activate")
    public ResponseEntity<Map<String, Object>> activateUserAccount(@RequestBody String email) {
        User u = userService.ActivateUser(email);
        if (u == null)
            return customResponse.HandleResponse(false, null, null, HttpStatus.OK);
        return customResponse.HandleResponse(true, null, null, HttpStatus.OK);
    }

    @PostMapping(value = "/changePassword")
    public ResponseEntity<?> ChangeUserPass(@RequestBody String newPass) throws Exception {
        try {
            userService.changeUserPassword(newPass);
            return customResponse.HandleResponse(true, null, null, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }

    }

    //check if username or email is used or no
    @PostMapping("/checkusername")
    public ResponseEntity<?> checkUsernameOrEmail(@RequestParam String username) {
        if (userService.loadUserByUsername(username) == null)
            return customResponse.HandleResponse(true, "not used", null, HttpStatus.OK);
        else if (userService.loadUserByUsername(username) != null) ;
        return customResponse.HandleResponse(false, "used email or username", null, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllUsers() throws Exception {
        try {
            userService.deleteAllUsers();
            return customResponse.HandleResponse(true, "deleted all users", null, HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> GetAllUsers() throws Exception {
        try {
            return customResponse.HandleResponse(true, null, userService.getAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.getMessage(), null, HttpStatus.OK);
        }
    }

}
