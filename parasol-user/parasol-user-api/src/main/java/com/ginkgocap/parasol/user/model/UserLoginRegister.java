package com.ginkgocap.parasol.user.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

/**
 * 用户注册登录表
 */
@Entity
@Table(name = "tb_user_login_register", catalog = "parasol_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = "passport"),
		@UniqueConstraint(columnNames = "mobile"),
		@UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "user_name") })
public class UserLoginRegister implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229992793845063007L;
	private long id;
	/**
	 * 通行证：用户注册时使用的登录名.
	 */
	private String passport;
	/**
	 * 手机号.
	 */
	private String mobile;
	/**
	 * 邮箱.
	 */
	private String email;
	/**
	 * 用户名.
	 */
	private String userName;
	/**
	 * 密码.
	 */
	private String password;
	/**
	 * 1 组织用户，0.个人用户.
	 */
	private Byte userType;
	/**
	 * 密码加密码.
	 */
	private String salt;
	/**
	 * 来源于哪个APP注册的.
	 */
	private String source;
	/**
	 * 创建时间.
	 */
	private Long ctime;
	/**
	 * 修改时间.
	 */
	private Long utime;
	/**
	 * 用户IP.
	 */
	private String ip;

	public UserLoginRegister() {
	}

	public UserLoginRegister(long id, String passport, String password,
			Byte userType, String salt, String source, Long ctime, Long utime,
			String ip) {
		this.id = id;
		this.passport = passport;
		this.password = password;
		this.userType = userType;
		this.salt = salt;
		this.source = source;
		this.ctime = ctime;
		this.utime = utime;
		this.ip = ip;
	}

	public UserLoginRegister(long id, String passport, String mobile,
			String email, String userName, String password, Byte userType,
			String salt, String source, Long ctime, Long utime,
			String ip) {
		this.id = id;
		this.passport = passport;
		this.mobile = mobile;
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.userType = userType;
		this.salt = salt;
		this.source = source;
		this.ctime = ctime;
		this.utime = utime;
		this.ip = ip;
	}

	@Id
	@GeneratedValue(generator = "id")
	@GenericGenerator(name = "id", strategy = "com.ginkgocap.ywxt.framework.dal.dao.id.util.TimeIdGenerator")
	@Column(name = "id")
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "passport", unique = true, nullable = false, length = 50)
	public String getPassport() {
		return this.passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	@Column(name = "mobile", unique = true, length = 20)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "email", unique = true, length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "user_name", unique = true, length = 32)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "password", nullable = false, length = 100)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "user_type", nullable = false)
	public Byte getUsetType() {
		return this.userType;
	}

	public void setUsetType(Byte usetType) {
		this.userType = usetType;
	}

	@Column(name = "salt", nullable = false, length = 40)
	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Column(name = "source", nullable = false, length = 20)
	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	
	@Column(name = "ctime", nullable = false, length = 19)
	public Long getCtime() {
		return this.ctime;
	}

	public void setCtime(Long ctime) {
		this.ctime = ctime;
	}

	@Column(name = "utime", nullable = false, length = 19)
	public Long getUtime() {
		return this.utime;
	}

	public void setUtime(Long utime) {
		this.utime = utime;
	}

	@Column(name = "ip", nullable = false, length = 16)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
