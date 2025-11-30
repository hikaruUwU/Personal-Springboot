package com.demo.base.repository;

import com.demo.base.domain.User;
import com.demo.base.mapper.UserMapper;
import com.demo.base.util.annotation.ResourceFinder;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class UserRepository extends ServiceImpl<UserMapper, User> implements ResourceFinder<User> {
    @Override
    public User getEntity(Serializable id) {
        return super.getById(id);
    }
}