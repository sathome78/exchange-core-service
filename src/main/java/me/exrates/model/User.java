package me.exrates.model;

import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.main.UserFile;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class User {

	private int id;
	private String nickname;
	private String email;
	private String phone;
	private UserStatus status = UserStatus.REGISTERED;
	private UserStatus userStatus = UserStatus.REGISTERED;
	private String password;
	private String finpassword;
	private Date regdate;
	private String ipaddress;
	private String confirmPassword;
	private String confirmFinPassword;
	private boolean readRules;
	private UserRole role = UserRole.USER;
	private String parentEmail;
	private List<UserFile> userFiles = Collections.emptyList();
	private Integer apiRateLimit;

	public Integer getApiRateLimit() {
		return apiRateLimit;
	}


	public User() {
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public boolean isReadRules() {
		return readRules;
	}

	public void setReadRules(boolean readRules) {
		this.readRules = readRules;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getIp() {
		return ipaddress;
	}

	public void setIp(String ip) {
		this.ipaddress = ip;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFinpassword() {
		return finpassword;
	}

	public void setFinpassword(String finpassword) {
		this.finpassword = finpassword;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public String getConfirmFinPassword() {
		return confirmFinPassword;
	}

	public void setConfirmFinPassword(String confirmFinPassword) {
		this.confirmFinPassword = confirmFinPassword;
	}

	public String getParentEmail() {
		return parentEmail;
	}

	public void setParentEmail(final String parentEmail) {
		this.parentEmail = parentEmail;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", nickname='" + nickname + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", status=" + status +
				", regdate=" + regdate +
				", ipaddress='" + ipaddress + '\'' +
				", readRules=" + readRules +
				", role=" + role +
				", parentEmail='" + parentEmail + '\'' +
				", userFiles=" + userFiles +
				'}';
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}
}