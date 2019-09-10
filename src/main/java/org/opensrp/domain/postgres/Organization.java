package org.opensrp.domain.postgres;

import java.util.Date;

public class Organization {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.identifier
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private String identifier;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.active
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private Boolean active;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.name
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private String name;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.type
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private Object type;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.date_deleted
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private Date dateDeleted;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column team.organization.parent_id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	private Long parentId;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.id
	 * @return  the value of team.organization.id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.id
	 * @param id  the value for team.organization.id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.identifier
	 * @return  the value of team.organization.identifier
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.identifier
	 * @param identifier  the value for team.organization.identifier
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.active
	 * @return  the value of team.organization.active
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.active
	 * @param active  the value for team.organization.active
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.name
	 * @return  the value of team.organization.name
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.name
	 * @param name  the value for team.organization.name
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.type
	 * @return  the value of team.organization.type
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public Object getType() {
		return type;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.type
	 * @param type  the value for team.organization.type
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setType(Object type) {
		this.type = type;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.date_deleted
	 * @return  the value of team.organization.date_deleted
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public Date getDateDeleted() {
		return dateDeleted;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.date_deleted
	 * @param dateDeleted  the value for team.organization.date_deleted
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column team.organization.parent_id
	 * @return  the value of team.organization.parent_id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public Long getParentId() {
		return parentId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column team.organization.parent_id
	 * @param parentId  the value for team.organization.parent_id
	 * @mbg.generated  Fri Aug 30 19:03:40 EAT 2019
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
}