package cn.bmob.social.share.core.data;

/**
 * 
 * 分享数据类
 * @author 稻草人
 * @date 2014年7月18日 下午4:01:05
 *
 */
public class ShareData {
	/**如果为app分享设置为true，如果为content分享则设置为false
	 * app分享的内容由开发者预先保留在友推服务器上
	 * content分享的内容由开发者给ShareData实例的各个字段赋值
	 **/
	public boolean isAppShare = false;
	/**分享的标题*/
	private String title = "分享";
	/**分享的描述*/
	private String description = "描述";
	/**分享的文字*/
	private String text = "分享内容...";
	/**分享的图片的本地路径*/
	private String imagePath;
	/**分享的图片的网络url*/
	private String imageUrl;
	/**分享的网页链接*/
	private String target_url;
	/**是否有活动正在进行*/
	private boolean isInProgress = false;
	/**图文分享，该分享类型为默认分享类型，如果开发者未设置，则使用默认分享类型*/
	public static final int SHARETYPE_IMAGEANDTEXT = 0;
	public static final int SHARETYPE_IMAGE = 1;
	/**用来判断分享的类型*/
	private int shareType = SHARETYPE_IMAGEANDTEXT;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTarget_url() {
		return target_url;
	}
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}
	public boolean isInProgress() {
		return isInProgress;
	}
	public void setInProgress(boolean isInProgress) {
		this.isInProgress = isInProgress;
	}
	public int getShareType() {
		return shareType;
	}
	public void setShareType(int shareType) {
		this.shareType = shareType;
	}
	public void setIsAppShare(boolean isAppShare) {
		this.isAppShare = isAppShare;
	}
}
