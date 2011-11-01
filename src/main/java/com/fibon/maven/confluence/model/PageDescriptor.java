package com.fibon.maven.confluence.model;

import org.apache.commons.lang.StringUtils;

public class PageDescriptor {

	private Long id;

	private String space;

	private String title;

	public PageDescriptor() {
	}

	public PageDescriptor(long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getSpace() {
		return space;
	}

	public String getTitle() {
		return title;
	}

	public boolean isAbsolute() {
		return id != null;
	}

	public boolean isRelative() {
		return StringUtils.isNotBlank(space) && StringUtils.isNotBlank(title);
	}

	public boolean isValid() {
		return isAbsolute() || isRelative();
	}

}
