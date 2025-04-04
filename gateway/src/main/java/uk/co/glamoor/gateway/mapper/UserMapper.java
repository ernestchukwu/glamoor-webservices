package uk.co.glamoor.gateway.mapper;

import uk.co.glamoor.gateway.dto.request.UserRequest;
import uk.co.glamoor.gateway.model.entity.User;

public class UserMapper {

    public static User toUser(UserRequest userRequest) {
        return new User(userRequest.getId(), userRequest.getUid());
    }
}
