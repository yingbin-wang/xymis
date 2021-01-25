package com.cn.wti.entity.view.custom;


/**
 * Menutbl entity. @author MyEclipse Persistence Tools<br>
 */

public class Menutbl implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
;

	// Constructors

	/** default constructor */
	public Menutbl() {
	}

	/** full constructor */
	public Menutbl(String name) {

		this.name = name;

	}

	// Property accessors

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


}