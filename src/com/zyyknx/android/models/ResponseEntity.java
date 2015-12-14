package com.zyyknx.android.models;

public class ResponseEntity<T> {

	public ResponseEntity() {
		Status = -10000;
		Error = new Error();
	}

	public ResponseEntity(T result) {
		Status = 1;
		Result = result;
		Error = null;
	}

	private T Result;

	/**
	 * @return the Result
	 */
	public T getResult() {
		return Result;
	}

	/**
	 * @param the
	 *            Result to set
	 */
	public void setResult(T result) {
		this.Result = result;
	}

	private int Status;

	/**
	 * @return the StatusCode
	 */
	public int getStatus() {
		return Status;
	}

	/**
	 * @param the
	 *            StatusCode to set
	 */
	public void setStatus(int status) {
		this.Status = status;
	}

	private Error Error;

	/**
	 * @return the Error
	 */
	public Error getError() {
		return Error;
	}

	/**
	 * @param the
	 *            Error to set
	 */
	public void setError(Error error) {
		this.Error = error;
	}
}
