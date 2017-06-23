package com.cetnaline.findproperty.api.bean;

/**
 * 请求基类
 * Created by fanxl2 on 2016/7/25.
 */
public class ApiResponse<T> {

	public static final int SUCCESS_CODE = 0;

	private int ResultNo;

	private String Message;

	private int Total;

	public int getResultNo() {
		return ResultNo;
	}

	public void setResultNo(int resultNo) {
		ResultNo = resultNo;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	private T Result;

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public T getResult() {
		return Result;
	}

	public void setResult(T result) {
		Result = result;
	}

	public boolean isSuccess() {
		if (this.ResultNo == SUCCESS_CODE) {
			return true;
		} else {
			return false;
		}
	}
}
