package org.opensrp.domain.postgres;

public class Report {
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.report.id
	 * 
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	private Long id;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.report.json
	 * 
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	private Object json;
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.report.id
	 * 
	 * @return the value of core.report.id
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.report.id
	 * 
	 * @param id the value for core.report.id
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.report.json
	 * 
	 * @return the value of core.report.json
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	public Object getJson() {
		return json;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.report.json
	 * 
	 * @param json the value for core.report.json
	 * @mbg.generated Fri Mar 23 14:23:39 EAT 2018
	 */
	public void setJson(Object json) {
		this.json = json;
	}
}
