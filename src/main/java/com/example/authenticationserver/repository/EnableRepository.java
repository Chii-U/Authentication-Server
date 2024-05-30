package com.example.authenticationserver.repository;

import com.example.authenticationserver.entity.Enable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository //이거 레디스 접근하는 리포임.
public interface EnableRepository extends CrudRepository<Enable,String> {

    Optional<Enable> findByAuthNumber(String code);
}
