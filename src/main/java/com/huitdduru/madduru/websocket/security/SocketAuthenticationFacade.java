package com.huitdduru.madduru.websocket.security;

import com.corundumstudio.socketio.SocketIOClient;
import com.huitdduru.madduru.user.entity.User;

public interface SocketAuthenticationFacade {
    User getCurrentUser(SocketIOClient client);
}
