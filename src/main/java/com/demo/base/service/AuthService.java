package com.demo.base.service;

import com.demo.base.domain.User;
import com.demo.base.domain.response.Result;
import com.demo.base.repository.UserRepository;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final HttpServletRequest Current;

    @Autowired
    public AuthService(UserRepository userRepository, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.Current = request;
    }

    public Result<Void> login(@Nonnull @Valid User user) {
        User one = userRepository.getOne(QueryWrapper.create().eq(User::getUsername, user.getUsername()));
        if (one != null && one.getPassword().equals(user.getPassword())) {
            Current.getSession(true).setAttribute("user", one);
            //Current.getSession(true);
            return Result.success(null);
        } else
            return Result.fail("账号或密码不正确");
    }

}
