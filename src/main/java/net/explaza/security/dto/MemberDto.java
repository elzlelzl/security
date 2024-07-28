package net.explaza.security.dto;

import lombok.*;
import net.explaza.security.domain.entity.Manager;
import net.explaza.security.domain.entity.ManagerGroup;
import org.springframework.data.domain.Page;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"groupList","excelUploadList"})
@NoArgsConstructor
public class MemberDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

//    @NotNull(message = "등급 선택은  필수입니다.")
//    private Integer level;

//    private String levelFormat;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    private LocalDateTime email_verified_at;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문, 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password;


    @NotBlank(message = "상태 선택은 필수입니다.")
    private String status;

    private LocalDateTime last_singin_at;

    private List<ManagerGroup> groupList;
    private String groupName;

    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    public Manager toEntity(){
        return Manager.builder()
                .id(id)
                .name(name)
                .email(email)
                .email_verified_at(email_verified_at)
                .password(password)
                .status(status)
                .last_singin_at(last_singin_at)
                .groupList(groupList)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder
    public MemberDto(Long id, String name,
//                     Integer level,
                     String email, LocalDateTime email_verified_at,
                     String password, String status, LocalDateTime last_singin_at,
                     LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                     List<ManagerGroup> groupList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.email_verified_at = email_verified_at;
        this.password = password;
        this.status = status;
        this.last_singin_at = last_singin_at;
        this.groupList = groupList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Page<MemberDto> toDtoList(Page<Manager> boardList){
        Page<MemberDto> memberDtoList = boardList.map(m -> MemberDto.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .email_verified_at(m.getEmail_verified_at())
                .password(m.getPassword())
                .status(m.getStatus())
                .last_singin_at(m.getLast_singin_at())
                .groupList(m.getGroupList())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .deletedAt(m.getDeletedAt())
                .build());
        return memberDtoList;
    }
}