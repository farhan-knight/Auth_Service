package com.dev.authservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;


@Setter
@Getter
@Entity
public class Session extends BaseModel{

    private String token;

    private Date expiringAt;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

}
