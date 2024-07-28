package net.explaza.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class PasswordDto {  //내정보 패스워드 변경시 null 파라메타 검증
    private  String  email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;



    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String newPassword2;


}
