package net.explaza.security.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
public class MemberUpdateDto {      //관리자가 회원들 정보 변경 null파라메타 검증

    private Long id;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

//    @NotBlank(message = "Level 선택은  필수입니다.")
//    private String level;

    @NotBlank(message = "상태 선택은 필수입니다.")
    private String status;
}
