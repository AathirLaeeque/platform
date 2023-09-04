package com.leucine.streem.repository;

import com.leucine.streem.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface IUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
