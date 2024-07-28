package net.explaza.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ManagerPageDto {
    int newPage;
    String keyword;
    String status;

    String group;
}
