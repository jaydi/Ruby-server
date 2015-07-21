package com.jaydi.ruby.models;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Rubymine extends BaseModel {
	public static final int TYPE_RESTAURANT = 1;
	public static final int TYPE_CAFE = 2;
	public static final int TYPE_BAR = 3;
	public static final int TYPE_ETC = 100;

	@Persistent
	@PrimaryKey
	private Long id;

	@Persistent
	private Long rubyzoneId;

	@Persistent
	private String name;

	@Persistent
	private Text contents;

	@Persistent
	private int type;

	@Persistent
	private String typeString;

	@Persistent
	private float ruby;

	@Persistent
	private float value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRubyzoneId() {
		return rubyzoneId;
	}

	public void setRubyzoneId(Long rubyzoneId) {
		this.rubyzoneId = rubyzoneId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Text getContents() {
		return contents;
	}

	public void setContents(Text contents) {
		this.contents = contents;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public float getRuby() {
		return ruby;
	}

	public void setRuby(float ruby) {
		this.ruby = ruby;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void update(Rubymine rubymine) {
		if (rubymine.getName() != null && !rubymine.getName().isEmpty() && !rubymine.getName().equals(name))
			name = rubymine.getName();

		if (rubymine.getContents() != null && !rubymine.getContents().getValue().isEmpty() && !rubymine.getContents().equals(contents))
			contents = rubymine.getContents();

		if (rubymine.getTypeString() != null && !rubymine.getTypeString().isEmpty())
			typeString = rubymine.getTypeString();

		if (rubymine.getType() != 0)
			type = rubymine.getType();

		if (rubymine.getValue() != 0)
			value = rubymine.getValue();
	}

}
