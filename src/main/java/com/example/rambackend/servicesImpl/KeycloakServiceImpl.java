package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakServiceImpl {


    private final WebClient webClient;

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

    public Mono<String> createUser(String email, String password, String fullname, UserRole role) {
        return getAdminToken()
                .flatMap(token -> {
                    System.out.println("Creating user with email: " + email);
                    Map<String, Object> userRepresentation = new HashMap<>();
                    userRepresentation.put("username", email);
                    userRepresentation.put("email", email);
                    userRepresentation.put("enabled", true);

                    Map<String, List<String>> attributes = new HashMap<>();
                    attributes.put("Fullname", fullname != null ? List.of(fullname) : List.of());
                     attributes.put("role", role != null ? List.of(role.toString()) : List.of());
                    userRepresentation.put("attributes", attributes);

                    List<Map<String, Object>> credentials = new ArrayList<>();
                    if (password != null) {
                        credentials.add(Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        ));
                    }
                    userRepresentation.put("credentials", credentials);

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

    public Mono<Utilisateur> createAndFetchUser(String email, String password, String fullname, UserRole role) {
        return createUser(email, password, fullname, role)
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

    private Utilisateur mapToUtilisateur(Map<String, Object> userDetails) {
        Utilisateur user = new Utilisateur();
        user.setId((String) userDetails.get("id"));
        user.setFullname(getAttributeValue(userDetails, "Fullname"));
        user.setEmail((String) userDetails.get("email"));
        user.setRole(UserRole.valueOf(getAttributeValue(userDetails, "role")));
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


}
