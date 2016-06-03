package com.sation.knxcontroller.models;

import java.util.Date;

public class EntityBase {

	public static final String ENVIID = "EnviID";
	public static final String CREATEDATE = "CreateDate";
	public static final String CREATEDBY = "CreatedBy";
	public static final String LASTUPDATEDDATE = "LastUpdatedDate";
	public static final String LASTUPDATEDBY = "LastUpdatedBy";

	private long EnviID;

	/**
	 * @return the enviID
	 */
	public long getEnviID() {
		return EnviID;
	}

	/**
	 * @param enviID
	 *            the enviID to set
	 */
	public void setEnviID(long enviID) {
		this.EnviID = enviID;
	}

	private Date CreateDate;

	public Date getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(Date createDate) {
		this.CreateDate = createDate;
	}

	private String CreatedBy;

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		this.CreatedBy = createdBy;
	}

	private Date LastUpdatedDate;

	public Date getLastUpdatedDate() {
		return LastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.LastUpdatedDate = lastUpdatedDate;
	}

	private String LastUpdatedBy;

	public String getLastUpdatedBy() {
		return LastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.LastUpdatedBy = lastUpdatedBy;
	}
}
