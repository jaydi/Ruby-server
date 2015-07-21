package com.jaydi.ruby.models;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Gem extends BaseModel {
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	private Long rubymineId;

	@Persistent
	private String name;

	@Persistent
	private String rubymineName;

	@Persistent
	private String imageKey;

	@Persistent
	private String desc;

	@Persistent
	private int type;

	@Persistent
	private int value;

	@Persistent
	private int ea;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRubymineId() {
		return rubymineId;
	}

	public void setRubymineId(Long rubymineId) {
		this.rubymineId = rubymineId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRubymineName() {
		return rubymineName;
	}

	public void setRubymineName(String rubymineName) {
		this.rubymineName = rubymineName;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getEa() {
		return ea;
	}

	public void setEa(int ea) {
		this.ea = ea;
	}

	public void update(Gem gem) {
		if (gem.getName() != null && !gem.getName().isEmpty())
			name = gem.getName();

		if (gem.getImageKey() != null && !gem.getImageKey().isEmpty())
			imageKey = gem.getImageKey();

		if (gem.getDesc() != null && !gem.getDesc().isEmpty())
			desc = gem.getDesc();

		if (gem.getType() != 0)
			type = gem.getType();

		if (gem.getValue() != 0)
			value = gem.getValue();

		if (gem.getEa() != 0)
			ea = gem.getEa();
	}

}
