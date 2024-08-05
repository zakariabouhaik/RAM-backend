package com.example.rambackend.controllers;

import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.services.UserService;
import com.example.rambackend.servicesImpl.KeycloakServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private KeycloakServiceImpl keycloakService;

    @PostMapping
    public Mono<Utilisateur> addUser(@RequestBody Utilisateur user) {
        return keycloakService.createAndFetchUser(user.getEmail(), user.getMdp(), user.getFullname(), user.getRole());
    }
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Utilisateur>> getUser(@PathVariable String id) {
        return keycloakService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
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

}
