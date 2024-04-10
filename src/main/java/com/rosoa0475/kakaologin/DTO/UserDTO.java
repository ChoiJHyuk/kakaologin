package com.rosoa0475.kakaologin.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rosoa0475.kakaologin.domain.UserEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    @JsonProperty("nickname")
    private String 	profile_nickname;

    public UserEntity getUserEntity() {
        return UserEntity.builder()
                .profileNickname(profile_nickname)
                .build();
    }
}
