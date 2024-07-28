package net.explaza.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
//    @NotEmpty(message = "회원 email은 필수 입니다")
    private String email;
//    @NotEmpty(message = "회원 패스워드는 필수 입니다")
    private String pwd;

}
