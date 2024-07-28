package net.explaza.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    MASTER("ROLE_MASTER"),
    EXAMINE("ROLE_EXAMINE"),
    PROCESS("ROLE_PROCESS"),
//    GROUP_MASTER("ROLE_GROUP_MASTER"),

    COLLECTOR("ROLE_COLLECTOR"),
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_MEMBER");




    private String value;
}