package com.dao;

import com.model.User;

public interface UserDao {
	
	//create
	User findByUsername(String usrename);
	User findByEmail(String email);
	void insertUser(User user);
	
	
	//read
	
	//update
	
	//delete

}
