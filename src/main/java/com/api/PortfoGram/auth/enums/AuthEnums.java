package com.api.PortfoGram.auth.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class AuthEnums {
        @Getter
        public enum ROLE {
                ROLE_USER("ROLE_USER"),
                ROLE_ADMIN("ROLE_ADMIN");

                private String role;

                ROLE(String role) {
                        this.role = role;
                }
        }
}
