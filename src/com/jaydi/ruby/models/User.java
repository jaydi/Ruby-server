package com.jaydi.ruby.models;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.jaydi.ruby.exceptions.DupNameException;
import com.jaydi.ruby.exceptions.DupPhoneException;
import com.jaydi.ruby.utils.TimeUtils;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User extends BaseModel {
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;

	public static final int TYPE_EMAIL = 1;
	public static final int TYPE_FACEBOOK = 2;
	public static final int TYPE_KAKAO = 3;

	public static final int STATE_ALIVE = 0;
	public static final int STATE_DEAD = 1;

	public static final int LEVEL_ROOKIE = 0;
	public static final int LEVEL_BRONZE = 1;
	public static final int LEVEL_SILVER = 2;
	public static final int LEVEL_GOLD = 3;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private String regId;

	@Persistent
	private Long socialId;

	@Persistent
	@Index
	private String name;

	@Persistent
	@Index
	private String phone;

	@Persistent
	@Index
	private String email;

	@Persistent
	private String password;

	@Persistent
	private String imageKey;

	@Persistent
	private long bday;

	@Persistent
	private int gender;

	@Persistent
	private int level;

	@Persistent
	private float ruby;

	@Persistent
	private int type;

	@Persistent
	private int state;

	@Persistent
	private int verCode;

	@NotPersistent
	private boolean isPaired;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public Long getSocialId() {
		return socialId;
	}

	public void setSocialId(Long socialId) {
		this.socialId = socialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public long getBday() {
		return bday;
	}

	public void setBday(long bday) {
		this.bday = bday;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getRuby() {
		return ruby;
	}

	public void setRuby(float ruby) {
		this.ruby = ruby;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	public boolean isPaired() {
		return isPaired;
	}

	public void setPaired(boolean isPaired) {
		this.isPaired = isPaired;
	}

	public void update(User user) throws DupNameException, DupPhoneException {
		updateRegId(user.getRegId());
		updateName(user.getName());
		updatePhone(user.getPhone());
		updatePassword(user.getPassword());
		updateImageKey(user.getImageKey());

		if (user.getBday() > 0)
			bday = user.getBday();
		if (user.getGender() > 0)
			gender = user.getGender();
		if (user.getVerCode() > 0)
			verCode = user.getVerCode();
	}

	private void updateRegId(String newRegId) {
		if (newRegId == null || newRegId.isEmpty() || newRegId.equals(regId))
			return;

		boolean isFirst = false;
		if (regId == null || regId.isEmpty())
			isFirst = true;

		regId = newRegId;

		if (isFirst)
			signUpEvent();
	}

	private void signUpEvent() {
		Job job = new Job();
		job.setType(Job.TYPE_SIGNUP_REWARD);
		job.setTargetId(id);
		job.setScheduledTime(TimeUtils.getNowTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		pm.makePersistent(job);
		pm.close();
	}

	@SuppressWarnings("unchecked")
	private void updateName(String newName) throws DupNameException {
		if (newName == null || newName.isEmpty() || newName.equals(name))
			return;

		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("name == newName");
		query.declareParameters("String newName");
		List<User> dups = (List<User>) pm.newQuery(query).execute(newName);
		if (!dups.isEmpty())
			throw new DupNameException();

		name = newName;
	}

	@SuppressWarnings("unchecked")
	private void updatePhone(String newPhone) throws DupPhoneException {
		if (newPhone == null || newPhone.isEmpty() || newPhone.equals(phone))
			return;

		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("phone == newPhone");
		query.declareParameters("String newPhone");
		List<User> dups = (List<User>) pm.newQuery(query).execute(newPhone);
		if (!dups.isEmpty())
			throw new DupPhoneException();

		phone = newPhone;
	}

	private void updatePassword(String newPassword) {
		if (newPassword == null || newPassword.isEmpty() || newPassword.equals(password))
			return;

		password = newPassword;
	}

	private void updateImageKey(String newImageKey) {
		if (newImageKey == null || newImageKey.isEmpty() || newImageKey.equals(imageKey))
			return;

		imageKey = newImageKey;
	}

	public static int calLevel(int count) {
		if (count < 10)
			return LEVEL_ROOKIE;
		else if (count >= 10 && count < 25)
			return LEVEL_BRONZE;
		else if (count >= 25 && count < 100)
			return LEVEL_SILVER;
		else if (count >= 100)
			return LEVEL_GOLD;
		else
			return LEVEL_ROOKIE;
	}

	public static float calAmpFactor(int level) {
		if (level == LEVEL_ROOKIE)
			return 1f;
		else if (level == LEVEL_BRONZE)
			return 2f;
		else if (level == LEVEL_SILVER)
			return 3f;
		else if (level == LEVEL_GOLD)
			return 5f;
		else
			return 1f;
	}

}
