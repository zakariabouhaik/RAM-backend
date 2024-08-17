package com.example.rambackend.chat;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/socket")
public class MyWebSocket {
/////////////////////useless

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        // Validate the token
        if (isValidToken(token)) {
            // Allow connection
        } else {
            // Close connection and handle exception
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"));
            } catch (IOException e) {
                // Handle the exception (log it, for example)
                e.printStackTrace();
            }
        }
    }

    private boolean isValidToken(String token) {
        // Implement token validation logic
        return true;
    }
}
