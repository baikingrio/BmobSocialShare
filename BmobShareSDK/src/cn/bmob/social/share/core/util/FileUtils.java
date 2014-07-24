package cn.bmob.social.share.core.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
/**
 * 文件操作
 * @author youtui
 * @since 14/6/19
 */
public class FileUtils {
	public static  String SDPATH = Environment.getExternalStorageDirectory()+ "/";

	public String getSDPATH() {
		return SDPATH;
		
	}

	/**
	 * 得到当前外部存储设备的目录
	 */
	public FileUtils() {
		//SDPATH = Environment.getExternalStorageDirectory()+ "/";
	}

	/**
	 * 在SD卡上创建文件
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		if(file.exists()){
			file.createNewFile();
		}	
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public  boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path：要写入SDCARD的目录
	 * @param fileName：要写入的文件名
	 * @param inpout：要写入的数据
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			while (input.read(buffer) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	
	/**判断两个文件是否内容相同*/
	public static boolean isSame(byte[] file1,byte[] file2){
		int length = file1.length<file2.length? file1.length:file2.length;
		for(int i=0;i<length;i++){
			if(file1[i]!=file2[i]){
				return false;
			}
		}	
		return true;	
	}
}
