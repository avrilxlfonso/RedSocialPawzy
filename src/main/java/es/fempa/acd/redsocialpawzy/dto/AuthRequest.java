package es.fempa.acd.redsocialpawzy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String username;
    private String email;
    private String password;
}
