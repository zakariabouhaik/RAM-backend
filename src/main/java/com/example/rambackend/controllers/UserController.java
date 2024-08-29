package com.example.rambackend.controllers;

import com.auth0.jwt.JWT;
import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Notification;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.UserRepository;
import com.example.rambackend.services.UserService;
import com.example.rambackend.servicesImpl.KeycloakServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuditRepository auditRepository;

    @PostMapping
    public Mono<ResponseEntity<Utilisateur>> addUser(@RequestBody Utilisateur user) {
        return Mono.fromCallable(() -> {
                    Notification notif = new Notification();
                    notif.setDesciption("Bienvenue à votre portail d'audit");
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setNotifications(Collections.singletonList(notif));
                    return userRepository.save(utilisateur);
                })
                .flatMap(savedUtilisateur ->
                        keycloakService.createAndFetchUser(user.getEmail(), user.getFullname(), savedUtilisateur.getId())

                )
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Error creating user: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
    @PostMapping("/addadmin")
    public Mono<ResponseEntity<Utilisateur>> addAdmin(@RequestBody Utilisateur user) {
        return Mono.fromCallable(() -> {
                    Notification notif = new Notification();
                    notif.setDesciption("Bienvenue à votre portail d'audit");
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setNotifications(Collections.singletonList(notif));
                    return userRepository.save(utilisateur);
                })
                .flatMap(savedUtilisateur ->
                        keycloakService.createAndFetchAdmin(user.getEmail(), user.getFullname(), savedUtilisateur.getId())

                )
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    System.err.println("Error creating user: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/unreadNotificationCount")
    public Mono<ResponseEntity<Long>> getUnreadNotificationCount(@RequestParam String idMongo) {
        return keycloakService.getUnreadNotificationCount(idMongo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/markNotificationsAsRead")
    public Mono<ResponseEntity<Void>> markNotificationsAsRead(@RequestParam String idMongo) {
        return keycloakService.markNotificationsAsRead(idMongo)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
    @PostMapping("/addAudit")
    public Mono<ResponseEntity<Utilisateur>> addAudit(@RequestBody Utilisateur user, @RequestParam String auditId) {
        return  Mono.fromCallable(() -> {
            Notification notif = new Notification();
            notif.setDesciption("Bienvenue à votre portail d'audit");
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNotifications(Collections.singletonList(notif));
            return userRepository.save(utilisateur);
        }).flatMap(savedUtilisateur ->
                        keycloakService.createAudite(user.getEmail(), user.getFullname(), auditId , savedUtilisateur.getId())

        ).flatMap(createdUser -> {
                    Audit audit = auditRepository.findById(auditId).orElse(null);

                    String adminId = "66c24a34556fca12b8653199"; // ID de l'admin
                    String message = "Le auditeur " +audit.getAuditeur().getFullname() + " a créé l'audité " + user.getFullname();
                    return keycloakService.addNotificationToUser(adminId, createdUser.getId(), message)
                            .thenReturn(createdUser);
                })

                .map(ResponseEntity::ok)
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(null))
                );
    }

    @GetMapping("/notification")
    public Mono<ResponseEntity<List<Map<String, Object>>>> fetchNotifications(@RequestParam String idMongo) {
        return keycloakService.fetchNotifications(idMongo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/addNotification")
    public Mono<ResponseEntity<Utilisateur>> addNotification(@RequestParam String userId, @RequestBody Map<String, Object> notificationData) {
        return Mono.fromCallable(() -> userRepository.findById(userId))
                .flatMap(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        Utilisateur user = optionalUser.get();

                        String fromKeycloakId = (String) notificationData.get("from");
                        return keycloakService.getUserById(fromKeycloakId)
                                .flatMap(fromUser -> {
                                    Notification notification = new Notification();
                                    notification.setFrom(fromUser);
                                    notification.setDesciption((String) notificationData.get("desciption"));
                                    notification.setDateTime(LocalDateTime.now());

                                    if (user.getNotifications() == null) {
                                        user.setNotifications(new ArrayList<>());
                                    }
                                    user.getNotifications().add(notification);

                                    return Mono.just(userRepository.save(user));
                                })
                                .onErrorResume(e -> Mono.error(new RuntimeException("From user not found in Keycloak")));
                    } else {
                        return Mono.error(new RuntimeException("User not found in MongoDB"));
                    }
                })
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e.getMessage().equals("From user not found in Keycloak") || e.getMessage().equals("User not found in MongoDB")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                    }
                });
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
                .flatMap(loginData -> {
                    String accessToken = (String) loginData.get("access_token");
                    // Decode the access token to get the user ID
                    String userId = JWT.decode(accessToken).getSubject();

                    return keycloakService.getIdMongoByUserId(userId)
                            .map(idMongo -> {
                                loginData.put("idMongo", idMongo);
                                return ResponseEntity.ok(loginData);
                            })
                            .defaultIfEmpty(ResponseEntity.ok(loginData));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))));
    }


    @GetMapping("/{userId}/idMongo")
    public Mono<ResponseEntity<?>> getIdMongoByUserId(@PathVariable String userId) {
        return keycloakService.getIdMongoByUserId(userId)
                .map(idMongo -> {
                    if (idMongo != null) {
                        return ResponseEntity.ok(Map.of("idMongo", idMongo));
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> {
                    System.err.println("Error fetching IdMongo for user " + userId + ": " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An internal error occurred")));
                });
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


    @GetMapping("/admins")
    public Mono<ResponseEntity<List<Utilisateur>>> getAdminUsers() {
        return keycloakService.getAdminUsers()
                .collectList() // Collect Flux to List
                .map(users -> ResponseEntity.ok(users))
                .onErrorResume(e -> {
                    System.err.println("Error fetching admin users: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of()));
                });
    }
}
