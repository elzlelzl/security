package net.explaza.security.repository;

import net.explaza.security.domain.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByIdAndDeletedAtIsNull(Long id);

    Page findAll(Specification<Group> employeeName, Pageable pageable);

    List findAll(Specification<Group> employeeName);



}
