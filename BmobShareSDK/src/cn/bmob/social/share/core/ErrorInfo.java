package cn.bmob.social.share.core;

/**
 * 该类用于携带各个分享平台分享的返回信息
 * @author youtui
 * @since 14/6/9  
 */
public class ErrorInfo {
	/**错误信息*/
	private String ErrorMessage;
	/**获取错误信息*/
	public String getErrorMessage() {
		return ErrorMessage;
	}
	/**设置错误信息*/
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
}
