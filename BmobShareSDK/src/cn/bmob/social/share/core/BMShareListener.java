package cn.bmob.social.share.core;

public abstract class BMShareListener {
	/**分享前操作*/
	public abstract void onPreShare();
	/**分享成功操作*/
	public abstract void onSuccess();
	/**分享错误操作*/
	public abstract void onError(ErrorInfo error);
	/**分享取消操作*/
	public abstract void onCancel();
}
