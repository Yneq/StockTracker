package com.service.impl;

import com.dao.UserDao;
import com.dao.impl.UserDaoImpl;
import com.model.User;
import com.service.AuthService;
import com.util.JwtUtil;
import com.util.PasswordUtil;

public class AuthServiceImpl implements AuthService{
	
	
	
	UserDao userDao=new UserDaoImpl();
	@Override
	public void register(String username, String password, String email) {
	    // 檢查 username 是否已存在
	    if (userDao.findByUsername(username) != null) {
	        System.out.println("Username already exists: " + username);
	        throw new RuntimeException("使用者名稱已存在");
	    }

	    // 檢查 email 是否已存在
	    if (userDao.findByEmail(email) != null) {
	        System.out.println("Email already exists: " + email);
	        throw new RuntimeException("Email 已存在");
	    }

	    User user = new User();
	    user.setUsername(username);
	    user.setPassword(PasswordUtil.encode(password));
	    user.setEmail(email);
	    
	    userDao.insertUser(user);
	    System.out.println("✅ 註冊成功: " + username + " / " + email);
	}
	
	@Override
	public String login(String emailOrUsername, String password) {
		
		User user = userDao.findByEmail(emailOrUsername);
	    
	    if (user == null) {
	        // 如果用 email 找不到，再試 username（相容性）
	        user = userDao.findByUsername(emailOrUsername);
	    }

	    if (user == null) {
	        throw new RuntimeException("帳號不存在");
	    }

	    if (!PasswordUtil.matches(password, user.getPassword())) {
	        throw new RuntimeException("密碼錯誤");
	    }

	    return JwtUtil.generateToken(user.getUsername());
	}
	
	
	}
	
	
	
	
	

