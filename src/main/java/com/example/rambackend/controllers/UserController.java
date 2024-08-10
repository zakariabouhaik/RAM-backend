package com.example.rambackend.controllers;

import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.services.UserService;
import com.example.rambackend.servicesImpl.KeycloakServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/User")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private KeycloakServiceImpl keycloakService;

    @PostMapping
    public Mono<Utilisateur> addUser(@RequestBody Utilisateur user) {
        return keycloakService.createAndFetchUser(user.getEmail(), user.getFullname());
    }

    @PostMapping("/addAudit")
    public Mono<Utilisateur>addAudit(@RequestBody Utilisateur user){
        return keycloakService.createAudite(user.getEmail(),user.getFullname());
    }

    @PostMapping("/addpassword")
    public Mono<ResponseEntity<Boolean>> setPassword(@RequestBody Utilisateur user){

        return keycloakService.setPassword(user.getEmail(),user.getMdp()).
                map(success -> ResponseEntity.ok(success))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false));

    }

    @PostMapping("/checkPassword")
    public Mono<ResponseEntity<Map<String, Boolean>>> checkPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return keycloakService.checkAccountStatus(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/enable/{userId}")
    public Mono<ResponseEntity<Boolean>> enableUser(@PathVariable String userId) {
        return keycloakService.enableUser(userId)
                .map(success -> {
                    if (success) {
                        return ResponseEntity.ok(true);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Utilisateur>> getUser(@PathVariable String id) {
        return keycloakService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        return keycloakService.login(username, password)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))));
    }


    @GetMapping()
    public Mono<ResponseEntity<List<Utilisateur>>> getAllUsers() {
        return keycloakService.getAllUsers()
                .collectList()
                .map(users -> {
                    if (users.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(users);
                    }
                });
    }

    @GetMapping("/Audite")
    public Mono<ResponseEntity<List<Utilisateur>>> getAllAudite() {
        return keycloakService.getAllAudite()
                .collectList()
                .map(users -> {
                    if (users.isEmpty()) {
                        return ResponseEntity.noContent().build();
                    } else {
                        return ResponseEntity.ok(users);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Utilisateur>> editUser(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return keycloakService.updateUser(id, updates)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.class, e -> {
                    System.err.println("Error response from Keycloak: " + e.getResponseBodyAsString());
                    return Mono.just(ResponseEntity.status(e.getStatusCode()).body(null));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return keycloakService.deleteUser(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
    @GetMapping("/current-user-id")
    public Mono<ResponseEntity<Map<String, String>>> getCurrentUserId(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return keycloakService.getCurrentUserId(accessToken)
                .map(id -> ResponseEntity.ok(Map.of("userId", id)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}
