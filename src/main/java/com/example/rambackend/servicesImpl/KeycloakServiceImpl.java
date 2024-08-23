package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.enums.UserRole;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeycloakServiceImpl {


    private final WebClient webClient;

    @Autowired
    private AuditRepository  auditRepository;

    @Autowired
    private UserRepository userRepository;

@Autowired
public KeycloakServiceImpl(WebClient.Builder webClientBuilder){
    this.webClient=webClientBuilder.baseUrl("https://16.171.20.170:8082").build();
}

    public Mono<String> getAdminToken(){
        return webClient.post()
                .uri("https://16.171.20.170:8082/realms/RAM/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", "RAM")
                        .with("client_secret", "W298zel3u864jv7CgPniC8pBbdTimykm"))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response->{
                    System.out.println("response data: "+response);
                    return (String) response.get("access_token");
                })
                .onErrorMap(error -> {
                    System.out.println("Error : " + error.getMessage());
                    return error;
                });
    }


    public Mono<Boolean>enableUser(String userId){
        return getAdminToken()
                .flatMap(token -> {
                    Map<String,Object>userUpdate = new HashMap<>();
                    userUpdate.put("enabled",true);


                    return webClient.put()
                            .uri("/admin/realms/RAM/users/{id}", userId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userUpdate)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .thenReturn(true)
                            .onErrorResume(WebClientResponseException.class, e -> {
                                System.err.println("Error enabling user: " + e.getResponseBodyAsString());
                                return Mono.just(false);
                            });
                })
                .onErrorResume(error -> {
                    System.err.println("Error enabling user in Keycloak: " + error.getMessage());
                    error.printStackTrace();
                    return Mono.just(false);
                });
    }

    public Mono<Boolean>setPassword(String email,String newPassword){
    return getAdminToken()
            .flatMap(token -> getUserIdByEmail(email,token)
                    .flatMap(userId->{
                        Map<String,Object>passwordSet  = new HashMap<>();
                        passwordSet.put("type","password");
                        passwordSet.put("value",newPassword);
                        passwordSet.put("temporary",false);

                        return webClient.put()
                                .uri("/admin/realms/RAM/users/{id}/reset-password", userId)
                                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(passwordSet)
                                .retrieve()
                                .bodyToMono(Void.class)
                                .thenReturn(true);
                    })

            )
            .onErrorResume(e->{
                System.err.println("Error setting password: "+e.getMessage());
                return Mono.just(false);
            });
    }


    public Mono<Utilisateur> createAudite(String email, String fullname, String idAudit ,String IdMongo) {
        return getAdminToken()
                .flatMap(token -> {
                    System.out.println("Creating user with email: " + email);
                    Map<String, Object> userRepresentation = new HashMap<>();
                    userRepresentation.put("username", email);
                    userRepresentation.put("email", email);
                    userRepresentation.put("enabled", false);

                    Map<String, List<String>> attributes = new HashMap<>();
                    attributes.put("Fullname", fullname != null ? List.of(fullname) : List.of());
                    attributes.put("role", List.of(UserRole.AUDITE.toString()));
                    attributes.put("IdMongo", List.of(IdMongo));
                    userRepresentation.put("attributes", attributes);

                    return webClient.post()
                            .uri("/admin/realms/RAM/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userRepresentation)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .then(getUserIdByEmail(email, token))
                            .flatMap(userId -> fetchUserDetails(userId))
                            .flatMap(user -> Mono.fromCallable(() -> {
                                        return auditRepository.findById(idAudit)
                                                .map(audit -> {
                                                    audit.setAudite(user);
                                                    return auditRepository.save(audit);
                                                })
                                                .orElseThrow(() -> new EntityNotFoundException("Audit not found with ID: " + idAudit));
                                    })
                                    .onErrorMap(EntityNotFoundException.class, e -> new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()))
                                    .thenReturn(user))
                            .onErrorResume(WebClientResponseException.class, e -> {
                                System.err.println("Error response body: " + e.getResponseBodyAsString());
                                return Mono.error(e);
                            });
                })
                .doOnNext(user -> System.out.println("Created user with ID: " + user.getId()))
                .onErrorResume(error -> {
                    System.err.println("Error creating user in Keycloak or associating with audit: " + error.getMessage());
                    error.printStackTrace();
                    return Mono.error(error);
                });
    }
    public Mono<String> createUser(String email, String fullname,String IdMongo) {
        return getAdminToken()
                .flatMap(token -> {
                    System.out.println("Creating user with email: " + email);
                    Map<String, Object> userRepresentation = new HashMap<>();
                    userRepresentation.put("username", email);
                    userRepresentation.put("email", email);
                    userRepresentation.put("enabled", true);

                    Map<String, List<String>> attributes = new HashMap<>();
                    attributes.put("Fullname", fullname != null ? List.of(fullname) : List.of());
                    attributes.put("role", List.of(UserRole.AUDITEUR.toString()));
                    attributes.put("IdMongo", List.of(IdMongo));

                    userRepresentation.put("attributes", attributes);



                    return webClient.post()
                            .uri("/admin/realms/RAM/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userRepresentation)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .then(getUserIdByEmail(email, token))
                            .onErrorResume(WebClientResponseException.class, e -> {
                                System.err.println("Error response body: " + e.getResponseBodyAsString());
                                return Mono.error(e);
                            });
                })
                .doOnNext(userId -> System.out.println("Created user with ID: " + userId))
                .onErrorResume(error -> {
                    System.err.println("Error creating user in Keycloak: " + error.getMessage());
                    error.printStackTrace();
                    return Mono.error(error);
                });
    }

    public Mono<Utilisateur> getUserById(String id) {
        return getAdminToken()
                .flatMap(token -> webClient.get()
                        .uri("/admin/realms/RAM/users/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(this::mapToUtilisateur)
                )
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                });
    }



    public Flux<Utilisateur> getAllUsers() {
        return getAdminToken()
                .flatMapMany(token -> webClient.get()
                        .uri("/admin/realms/RAM/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .map(this::mapToUtilisateur)
                )
                .onErrorResume(e -> {
                    System.err.println("Error fetching users from Keycloak: " + e.getMessage());
                    return Flux.empty();
                });
    }


    public Flux<Utilisateur> getAllAudite() {
        return getAdminToken()
                .flatMapMany(token -> webClient.get()
                        .uri("/admin/realms/RAM/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .map(this::mapToUtilisateur)
                        .filter(user->UserRole.AUDITE.equals(user.getRole()))
                )
                .onErrorResume(e -> {
                    System.err.println("Error fetching users from Keycloak: " + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<Map<String, Object>> login(String username, String password) {
        System.out.println("Attempting login for user: " + username);
        return webClient.post()
                .uri("/realms/RAM/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "RAM")
                        .with("client_secret", "W298zel3u864jv7CgPniC8pBbdTimykm")
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnNext(tokenInfo -> System.out.println("Login successful for user: " + username))
                .onErrorResume(WebClientResponseException.class, e -> {
                    System.err.println("Keycloak error response: " + e.getResponseBodyAsString());
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                    return Mono.error(e);
                })
                .onErrorMap(error -> {
                    System.err.println("Login error for user " + username + ": " + error.getMessage());
                    return error;
                });
    }

    public Mono<String> getIdMongoByUserId(String userId) {
        return getAdminToken()
                .flatMap(token -> webClient.get()
                        .uri("/admin/realms/RAM/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(userDetails -> {
                            Map<String, List<String>> attributes = (Map<String, List<String>>) userDetails.get("attributes");
                            if (attributes != null && attributes.containsKey("IdMongo")) {
                                List<String> idMongoList = attributes.get("IdMongo");
                                if (!idMongoList.isEmpty()) {
                                    return idMongoList.get(0);
                                }
                            }
                            return null;
                        })
                )
                .onErrorResume(e -> {
                    System.err.println("Error fetching IdMongo for user " + userId + ": " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Void> addNotificationToUser(String userId, String fromUserId, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("from", fromUserId);
        notificationData.put("desciption", message);

        return webClient.post()
                .uri("http://localhost:8080/User/addNotification?userId={userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notificationData)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    System.err.println("Error adding notification to user: " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Void> addNotificationToAuditeur(String auditeurId, String fromUserId) {
        return getIdMongoByUserId(auditeurId)
                .flatMap(idMongo -> {
                    if (idMongo == null) {
                        return Mono.error(new RuntimeException("IdMongo not found for auditeur"));
                    }

                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("from", fromUserId);
                    notificationData.put("desciption", "vous avez été choisi pour faire une audite");

                    return webClient.post()
                            .uri("http://localhost:8080/User/addNotification?userId={idMongo}", idMongo)
                            .bodyValue(notificationData)
                            .retrieve()
                            .bodyToMono(Void.class);
                });
    }



    public Mono<List<Map<String, Object>>> fetchNotifications(String idMongo) {
        return Mono.fromCallable(() -> userRepository.findById(idMongo))
                .flatMap(optionalUser -> {
                    if (optionalUser.isPresent()) {
                        Utilisateur user = optionalUser.get();
                        return Mono.just(user.getNotifications().stream()
                                .map(notification -> {
                                    Map<String, Object> notificationMap = new HashMap<>();
                                    notificationMap.put("dateTime", notification.getDateTime());
                                    notificationMap.put("description", notification.getDesciption());
                                    return notificationMap;
                                })
                                .collect(Collectors.toList()));
                    } else {
                        return Mono.just(Collections.emptyList());
                    }
                });
    }
    public Mono<Utilisateur> createAndFetchUser(String email, String fullname,String IdMongo) {
        return createUser(email, fullname, IdMongo)
                .flatMap(userId -> fetchUserDetails(userId));
    }

    private Mono<Utilisateur> fetchUserDetails(String userId) {
        return getAdminToken()
                .flatMap(token -> webClient.get()
                        .uri("/admin/realms/RAM/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(this::mapToUtilisateur)
                );
    }

    public Mono<Map<String, Boolean>> checkAccountStatus(String email) {
        return getAdminToken()
                .flatMap(token -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/admin/realms/RAM/users")
                                .queryParam("email", email)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                )
                .flatMap(users -> {
                    if (users.isEmpty()) {
                        return Mono.just(Map.of("exists", false, "enabled", false, "hasPassword", false));
                    }
                    Map<String, Object> user = users.get(0);
                    String userId = (String) user.get("id");
                    boolean isEnabled = (Boolean) user.get("enabled");

                    return getAdminToken()
                            .flatMap(token -> webClient.get()
                                    .uri("/admin/realms/RAM/users/{id}/credentials", userId)
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                                    .map(credentials -> Map.of(
                                            "exists", true,
                                            "enabled", isEnabled,
                                            "hasPassword", !credentials.isEmpty()
                                    ))
                            );
                })
                .onErrorResume(e -> {
                    System.err.println("Error checking account status: " + e.getMessage());
                    return Mono.just(Map.of("exists", false, "enabled", false, "hasPassword", false));
                });
    }
    private Utilisateur mapToUtilisateur(Map<String, Object> userDetails) {
        Utilisateur user = new Utilisateur();
        user.setId((String) userDetails.get("id"));
        user.setFullname(getAttributeValue(userDetails, "Fullname"));
        user.setEmail((String) userDetails.get("email"));
        user.setEnabled((Boolean) userDetails.get("enabled"));
        user.setRole(UserRole.valueOf(getAttributeValue(userDetails, "role")));
        user.setIdMongo(getAttributeValue(userDetails, "IdMongo"));


        return user;

    }


    public Mono<Utilisateur> updateUser(String id, Map<String, Object> updates) {
        return getAdminToken()
                .flatMap(token -> getUserById(id)
                        .flatMap(existingUser -> {
                            Map<String, Object> userRepresentation = new HashMap<>();
                            Map<String, List<String>> attributes = new HashMap<>();

                            // Conserver tous les champs existants
                            userRepresentation.put("email", updates.getOrDefault("email", existingUser.getEmail()));
                            userRepresentation.put("username", updates.getOrDefault("email", existingUser.getEmail()));
                            userRepresentation.put("enabled", true);
                            userRepresentation.put("firstName", existingUser.getFullname().split(" ")[0]);
                            userRepresentation.put("lastName", existingUser.getFullname().split(" ").length > 1 ? existingUser.getFullname().split(" ")[1] : "");

                            // Mise à jour des attributs
                            attributes.put("Fullname", List.of((String) updates.getOrDefault("fullname", existingUser.getFullname())));
                            attributes.put("role", List.of(updates.containsKey("role") ? updates.get("role").toString() : existingUser.getRole().toString()));
                            userRepresentation.put("attributes", attributes);

                            System.out.println("Sending update to Keycloak: " + userRepresentation);

                            return webClient.put()
                                    .uri("/admin/realms/RAM/users/{id}", id)
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userRepresentation)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .then(getUserById(id));
                        })
                )
                .onErrorResume(e -> {
                    System.err.println("Error updating user in Keycloak: " + e.getMessage());
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException wce = (WebClientResponseException) e;
                        System.err.println("Response body: " + wce.getResponseBodyAsString());
                    }
                    return Mono.error(e);
                });
    }
    public Mono<Void> deleteUser(String id) {
        return getAdminToken()
                .flatMap(token -> webClient.delete()
                        .uri("/admin/realms/RAM/users/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Void.class)
                )
                .onErrorResume(e -> {
                    System.err.println("Error deleting user from Keycloak: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    private String getAttributeValue(Map<String, Object> userDetails, String attributeName) {
        Map<String, List<String>> attributes = (Map<String, List<String>>) userDetails.get("attributes");
        List<String> values = attributes.get(attributeName);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }










    private Mono<String> getUserIdByEmail(String email, String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/RAM/users")
                        .queryParam("email", email)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(users -> {
                    if (users.isEmpty()) {
                        throw new RuntimeException("User not found");
                    }
                    return (String) users.get(0).get("id");
                });
    }

    public Mono<String> getCurrentUserId(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/admin/realms/RAM/users/me").build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .map(user -> (String) user.get("id"))
                .onErrorResume(e -> {
                    System.err.println("Error fetching current user ID: " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<Utilisateur> getAdminUsers() {
        return getAdminToken()
                .flatMapMany(token -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/admin/realms/RAM/users")
                                .queryParam("role", "admin") // Adjust query parameters as needed
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .map(this::mapToUtilisateur)
                        .filter(user -> UserRole.ADMIN.equals(user.getRole())) // Adjust as needed
                )
                .onErrorResume(e -> {
                    System.err.println("Error fetching admin users from Keycloak: " + e.getMessage());
                    return Flux.empty();
                });
    }

}
