package cn.bmob.social.share.core.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * 
 * 邮件和短信分享功能的实现
 * @author 稻草人
 * @date 2014年7月21日 上午10:27:48
 *
 */
public class OtherShare {
	private Activity act;

	public OtherShare(Activity act) {
		this.act = act;
	}
	/**
	 * 分享到短信
	 * @param sms_body
	 */
	public void sendSMS(String sms_body) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		sendIntent.putExtra("sms_body", sms_body);
//		sendIntent.setType("vnd.android-dir/mms-sms");
		act.startActivityForResult(sendIntent, 1002);
	}

	/**
	 * 分享到Email
	 * @param emailBody
	 */
	public void sendMail(String emailBody) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String emailSubject = "共享软件";
		// 设置邮件默认地址
		// email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		act.startActivityForResult(Intent.createChooser(email, "请选择邮件发送软件"), 1001);
	}
}
