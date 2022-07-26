package com.jazara.icu.auth.controller;

import com.jazara.icu.auth.domain.User;
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
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) throws Exception {

        Map<String, Object> tokenMap = new HashMap<String, Object>();
        try {
            userService.authenticate(username, password);
        } catch (DisabledException e) {
            return customResponse.HandleResponse(false, "Please Activate Your Account", "", HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            return customResponse.HandleResponse(false, "Incorrect Email or Password", "", HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = userService.loadUserByUsername(username);
        User appUser = userService.findUserByUsername(username);
        if (appUser == null) {
            return customResponse.HandleResponse(false, "There is no account with given username or email", "", HttpStatus.UNAUTHORIZED);
        }
        if (!appUser.isEnabled()) {
            return customResponse.HandleResponse(false, "Please Activate Your Account", "", HttpStatus.UNAUTHORIZED);
        } else {
            final String token = jwtTokenUtil.generateToken(userDetails);

            Map<String, Object> resultTokenMap = new HashMap<String, Object>();

            if (token != null) {
                resultTokenMap.put("token", token);
                resultTokenMap.put("user", appUser);

                return customResponse.HandleResponse(true, "", resultTokenMap, HttpStatus.OK);
            } else {
                return customResponse.HandleResponse(false, "Invalid Token", "", HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @GetMapping(value = "/checkjwttoken")
    public ResponseEntity<Map<String, Object>> checktoken(@RequestParam String token) {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        try {
            if (jwtTokenUtil.isTokenExpired(token))
                return customResponse.HandleResponse(false, "Expired Token", "", HttpStatus.UNAUTHORIZED);
            else {
                return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
            }
        } catch (ExpiredJwtException e) {
            return customResponse.HandleResponse(false, "Expired Token", "", HttpStatus.UNAUTHORIZED);
        }

    }

    //get logged in user
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        return customResponse.HandleResponse(true, "", userService.findUserByUsername(loggedUsername), HttpStatus.OK);
    }

    //get logged in user id
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getLoggedUserID() {
        return customResponse.HandleResponse(true, "", userService.getLoggedUserId(), HttpStatus.OK);
    }

    // Registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUserAccount(@RequestBody User accountDto) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.save(accountDto);
        if (registered == null) {
            return customResponse.HandleResponse(false, "couldn't register account", "", HttpStatus.OK);
        }
        LOGGER.info("registered account : " + registered.getEmail());
        return customResponse.HandleResponse(true, "", registered.getEmail(), HttpStatus.OK);
    }

    // activation
    @PostMapping(value = "/activate")
    public ResponseEntity<Map<String, Object>> activateUserAccount(@RequestBody String email) {
        User u = userService.ActivateUser(email);
        if (u == null)
            return customResponse.HandleResponse(false, "", "", HttpStatus.OK);
        return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
    }

    @PostMapping(value = "/changePassword")
    public ResponseEntity<?> ChangeUserPass(@RequestBody String newPass) throws Exception {
        try {
            userService.changeUserPassword(newPass);
            return customResponse.HandleResponse(true, "", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }

    }

    //check if username or email is used or no
    @PostMapping("/checkusername")
    public ResponseEntity<?> checkUsernameOrEmail(@RequestParam String username) {
        if (userService.loadUserByUsername(username) == null)
            return customResponse.HandleResponse(true, "not used", "", HttpStatus.OK);
        else if (userService.loadUserByUsername(username) != null) ;
        return customResponse.HandleResponse(false, "used email or username", "", HttpStatus.OK);
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> DeleteAllUsers() throws Exception {
        try {
            userService.deleteAllUsers();
            return customResponse.HandleResponse(true, "deleted all users", "", HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/getAll")
    public ResponseEntity<?> GetAllUsers() throws Exception {
        try {
            return customResponse.HandleResponse(true, "", userService.getAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return customResponse.HandleResponse(false, e.toString(), "", HttpStatus.OK);
        }
    }

}
