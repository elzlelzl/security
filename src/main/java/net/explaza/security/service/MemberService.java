package net.explaza.security.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.explaza.security.domain.entity.Manager;
import net.explaza.security.domain.entity.ManagerGroup;
import net.explaza.security.dto.ManagerPageDto;
import net.explaza.security.dto.MemberDto;
import net.explaza.security.dto.MemberUpdateDto;
import net.explaza.security.dto.PasswordDto;
import net.explaza.security.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private MemberRepository memberRepository;
    private  final  ModelMapper modelMapper = new ModelMapper();
    // 회원가입 시, 유효성 체크
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : errors.getFieldErrors()) {
            System.out.println("fidel:  "+error.getField());
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }

    @Transactional
    public Long joinUser(MemberDto memberDto, String email, HttpServletRequest request) {
        // 비밀번호 암호화
        memberDto.setPassword(passwordEn(memberDto.getPassword()));
        Long managerId = memberRepository.save(memberDto.toEntity()).getId();
        // 변경 LOG 저장
        return managerId;
    }


    //암호화
    public String passwordEn(String pwd){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(pwd);
    }
    //패스워드 비교
    public boolean passwordComparison(String checkPassword, String encodePassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(checkPassword, encodePassword);
    }

    //이메일로 회원정보 검색
    public MemberDto findMember(String useremail){
        ModelMapper modelMapper = new ModelMapper();
        Optional<Manager> userEntityWrapper = memberRepository.findByEmailAndDeletedAtIsNull(useremail);
        Manager userEntity = userEntityWrapper.get();
        MemberDto memberDto = modelMapper.map(userEntity, MemberDto.class);
        return memberDto;
    }

    //password 변경
    @Transactional
    public Long passwordUpdate(PasswordDto passwordDto, String email, HttpServletRequest request){
        MemberDto memberDto = findMember(passwordDto.getEmail());
        if(passwordComparison(passwordDto.getPassword(), memberDto.getPassword())) {
            memberDto.setPassword(passwordEn(passwordDto.getNewPassword()));
//            Long auditsId = auditsSave.auditsSave(email,"Updated",memberDto.getId(), "Manager.class", request);
            Long managerId = memberRepository.save(memberDto.toEntity()).getId();
//            auditsSave.auditsEdit(managerId, auditsId, "Manager.class");
            // 변경 LOG 저장
            return managerId;
        }else {
            return null;
        }
    }

    //회원정보 수정
    @Transactional
    public Long memberUpdate(MemberDto memberDto, String email, HttpServletRequest request){
//        memberDto.setPassword(passwordEn(memberDto.getPassword()));
//        memberDto.setPassword(findMember(memberDto.getEmail()).getPassword());
//        Long auditsId = auditsSave.auditsSave(email,"Updated",memberDto.getId(), "Manager.class", request);
        Long managerId = memberRepository.save(memberDto.toEntity()).getId();
//        auditsSave.auditsEdit(managerId, auditsId, "Manager.class");
        // 변경 LOG 저장
        return managerId;
    }

    //모든회원 찾기
    public List<MemberDto> findAllMember(){
        List<MemberDto> memberDto = memberRepository.findAll()
                .stream().map(p -> modelMapper.map(p, MemberDto.class)).collect(Collectors.toList());
        return memberDto;
    }

    @Transactional
    public Page<MemberDto> search(ManagerPageDto managerPageDto, Pageable pageable) {
        log.info(managerPageDto.toString());
        Page page = memberRepository.findAll(new Specification<Manager>() {

            @Override
            public Predicate toPredicate(Root<Manager> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (managerPageDto.getStatus()!=null && managerPageDto.getStatus().equals("D")){
                    predicates.add(criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("deletedAt"))));
                }else {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.isNull(root.get("deletedAt"))));
                }
                if (managerPageDto.getKeyword()!=null && !managerPageDto.getKeyword().equals("")) {
                    Predicate predicateForGrade
                            = criteriaBuilder.or(criteriaBuilder.like(root.get("email"), "%"+managerPageDto.getKeyword()+"%"),
                            criteriaBuilder.like(root.get("name"), "%"+managerPageDto.getKeyword()+"%"));
                    predicates.add(criteriaBuilder.and(predicateForGrade));
                }
                if (managerPageDto.getStatus()!=null && !managerPageDto.getStatus().equals("all")) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), managerPageDto.getStatus())));
                }
//                if (!managerPageDto.getLevel().equals(null) && !managerPageDto.getLevel().equals("all")) {
//                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("level"), managerPageDto.getLevel())));
//                }
                if (managerPageDto.getGroup()!= null && !"".equals(managerPageDto.getGroup())){
                    Subquery<ManagerGroup> subquery = query.subquery(ManagerGroup.class);
                    Root<ManagerGroup> subRoot = subquery.from(ManagerGroup.class);
                    Predicate managerPredicate = criteriaBuilder.equal(root.get("id"),subRoot.get("manager"));
                    Predicate groupPredicate = criteriaBuilder.equal(subRoot.get("group").get("name"), managerPageDto.getGroup());
                    subquery.select(subRoot).where(managerPredicate, groupPredicate);
                    predicates.add(criteriaBuilder.exists(subquery));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
        Page<MemberDto> memberList = new MemberDto().toDtoList(page);

        for (MemberDto member : memberList.toList()){
//            switch (member.getLevel()){
//                case 100: member.setLevelFormat("마스터");
//                    break;
//                case 70: member.setLevelFormat("검수");
//                    break;
//                case 20: member.setLevelFormat("가공");
//                    break;
//                case 0: member.setLevelFormat("수집기");
//                    break;
//            }
            if (member.getGroupList().size()>0)
                member.setGroupName(member.getGroupList().get(0).getGroup().getName());
            else
                member.setGroupName("");
            switch (member.getStatus()){
                case "Y": member.setStatus("사용중");
                    break;
                case "D": member.setStatus("삭제");
                    break;
                case "P": member.setStatus("일시중지");
                    break;
            }
        }
        return memberList;


    }

    //ID 회원 찾기
    public MemberDto findOne(Long userId){
        Optional<Manager> memberEntity = memberRepository.findByIdAndDeletedAtIsNull(userId);
        if(memberEntity.isPresent()) {
            return modelMapper.map(memberEntity, MemberDto.class);
        }else {
            return null;
        }
    }

    //중폭체크
    public boolean overlap(MemberUpdateDto memberUpdateDto){
        Optional<Manager> userEntity = memberRepository.findByEmailAndDeletedAtIsNull(memberUpdateDto.getEmail());
        if(userEntity.isPresent()){
            return(memberUpdateDto.getId().equals(userEntity.get().getId()));
        }else return true;
    }
    public boolean overlapName(MemberUpdateDto memberUpdateDto){
        Optional<Manager> userEntity = memberRepository.findByNameAndDeletedAtIsNull(memberUpdateDto.getName());
        if(userEntity.isPresent()){
            return(memberUpdateDto.getId().equals(userEntity.get().getId()));
        }else return true;
    }


    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<Manager> userEntityWrapper = memberRepository.findByEmailAndDeletedAtIsNull(userEmail);
        Manager userEntity = userEntityWrapper.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (ManagerGroup managerGroup : userEntity.getGroupList()){
            authorities.add(new SimpleGrantedAuthority(managerGroup.getGroup().getRoles()));
        }
        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);
    }

    @Transactional
    public void deletedAt(Long memberId) {
        Optional<Manager> manager = memberRepository.findById(memberId);
        MemberDto memberDto = modelMapper.map(manager, MemberDto.class);
        memberDto.setDeletedAt(LocalDateTime.now());
        memberDto.setStatus("D");
        memberRepository.save(memberDto.toEntity());
    }


    public void checkSessionExpiration(HttpSession session) {
        long maxInactiveInterval = session.getMaxInactiveInterval() * 1000; // 세션의 최대 유효 시간 (밀리초 단위)
        long creationTime = session.getCreationTime(); // 세션의 생성 시간 (밀리초 단위)
        long currentTime = System.currentTimeMillis(); // 현재 시간 (밀리초 단위)
        long remainingTime = maxInactiveInterval - (currentTime - creationTime); // 만료까지 남은 시간 (밀리초 단위)

        long remainingSeconds = remainingTime / 1000; // 초 단위로 변환
        long hours = remainingSeconds / 3600; // 시간
        long minutes = (remainingSeconds % 3600) / 60; // 분
        long seconds = remainingSeconds % 60; // 초
        String remainingTimeFormatted;

        if (hours > 0) {
            remainingTimeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds); // HH:mm:ss
        } else if (minutes > 0) {
            remainingTimeFormatted = String.format("%02d 분", minutes); // mm 분
        } else {
            remainingTimeFormatted = String.format("%02d 초", seconds); // ss 초
        }

        System.out.println("Remaining time: " + remainingTimeFormatted);
    }

}