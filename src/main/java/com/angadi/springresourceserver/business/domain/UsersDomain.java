package com.angadi.springresourceserver.business.domain;

import lombok.*;

@Getter @Setter @NoArgsConstructor  @AllArgsConstructor
public class UsersDomain {
    @NonNull
    private String userName;
    @NonNull
    private Long userID;



}
