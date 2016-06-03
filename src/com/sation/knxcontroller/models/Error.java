package com.sation.knxcontroller.models;

public class Error {

	private int ErrorType;

	/**
	 * @return the ErrorType
	 */
	public int getErrorType() {
		return ErrorType;
	}

	/**
	 * @param ErrorType
	 *            the ErrorType to set
	 */
	public void setErrorType(int errorType) {
		this.ErrorType = errorType;
	}

	private int EventID;

	/**
	 * @return the EventID
	 */
	public int getEventID() {
		return EventID;
	}

	/**
	 * @param EventID
	 *            the EventID to set
	 */
	public void setEventID(int eventID) {
		this.EventID = eventID;
	}

	private String Message;

	/**
	 * @return the Message
	 */
	public String getMessage() {
		return Message;
	}

	/**
	 * @param Message
	 *            the Message to set
	 */
	public void setMessage(String message) {
		this.Message = message;
	}

	private String Description;

	/**
	 * @return the Description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * @param Description
	 *            the Description to set
	 */
	public void setDescription(String description) {
		this.Description = description;
	}

	private String Source;

	/**
	 * @return the Source
	 */
	public String getSource() {
		return Source;
	}

	/**
	 * @param Source
	 *            the Source to set
	 */
	public void setSource(String source) {
		this.Source = source;
	}
}
