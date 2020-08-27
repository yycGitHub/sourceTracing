package com.surekam.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import eu.medsea.mimeutil.MimeUtil;

/**
 * 文件操作工具类
 * 实现文件的创建、删除、复制、压缩、解压以及目录的创建、删除、复制、压缩解压等功能
 * @author sureserve
 * @version 2013-06-21
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
	
	private static Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	public static final String SUFFIX_SPLIT = ".";
	public static final String PATH_SPLIT = "/";
	public static final String TEMPLATE_SUFFIX="html";
	
    static{
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
    }

	/**
	 * 复制单个文件，如果目标文件存在，则不覆盖
	 * @param srcFileName 待复制的文件名
	 * @param descFileName 目标文件名
	 * @return 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFile(String srcFileName, String descFileName) {
		return FileUtils.copyFileCover(srcFileName, descFileName, false);
	}

	/**
	 * 复制单个文件
	 * @param srcFileName 待复制的文件名
	 * @param descFileName 目标文件名
	 * @param coverlay 如果目标文件已存在，是否覆盖
	 * @return 如果复制成功，则返回true，否则返回false
	 */
	public static boolean copyFileCover(String srcFileName,
			String descFileName, boolean coverlay) {
		File srcFile = new File(srcFileName);
		// 判断源文件是否存在
		if (!srcFile.exists()) {
			log.debug("复制文件失败，源文件 " + srcFileName + " 不存在!");
			return false;
		}
		// 判断源文件是否是合法的文件
		else if (!srcFile.isFile()) {
			log.debug("复制文件失败，" + srcFileName + " 不是一个文件!");
			return false;
		}
		File descFile = new File(descFileName);
		// 判断目标文件是否存在
		if (descFile.exists()) {
			// 如果目标文件存在，并且允许覆盖
			if (coverlay) {
				log.debug("目标文件已存在，准备删除!");
				if (!FileUtils.delFile(descFileName)) {
					log.debug("删除目标文件 " + descFileName + " 失败!");
					return false;
				}
			} else {
				log.debug("复制文件失败，目标文件 " + descFileName + " 已存在!");
				return false;
			}
		} else {
			if (!descFile.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建目录
				log.debug("目标文件所在的目录不存在，创建目录!");
				// 创建目标文件所在的目录
				if (!descFile.getParentFile().mkdirs()) {
					log.debug("创建目标文件所在的目录失败!");
					return false;
				}
			}
		}

		// 准备复制文件
		// 读取的位数
		int readByte = 0;
		InputStream ins = null;
		OutputStream outs = null;
		try {
			// 打开源文件
			ins = new FileInputStream(srcFile);
			// 打开目标文件的输出流
			outs = new FileOutputStream(descFile);
			byte[] buf = new byte[1024];
			// 一次读取1024个字节，当readByte为-1时表示文件已经读取完毕
			while ((readByte = ins.read(buf)) != -1) {
				// 将读取的字节流写入到输出流
				outs.write(buf, 0, readByte);
			}
			log.debug("复制单个文件 " + srcFileName + " 到" + descFileName
					+ "成功!");
			return true;
		} catch (Exception e) {
			log.debug("复制文件失败：" + e.getMessage());
			return false;
		} finally {
			// 关闭输入输出流，首先关闭输出流，然后再关闭输入流
			if (outs != null) {
				try {
					outs.close();
				} catch (IOException oute) {
					oute.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException ine) {
					ine.printStackTrace();
				}
			}
		}
	}

	/**
	 * 复制整个目录的内容，如果目标目录存在，则不覆盖
	 * @param srcDirName 源目录名
	 * @param descDirName 目标目录名
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectory(String srcDirName, String descDirName) {
		return FileUtils.copyDirectoryCover(srcDirName, descDirName,
				false);
	}

	/**
	 * 复制整个目录的内容 
	 * @param srcDirName 源目录名
	 * @param descDirName 目标目录名
	 * @param coverlay 如果目标目录存在，是否覆盖
	 * @return 如果复制成功返回true，否则返回false
	 */
	public static boolean copyDirectoryCover(String srcDirName,
			String descDirName, boolean coverlay) {
		File srcDir = new File(srcDirName);
		// 判断源目录是否存在
		if (!srcDir.exists()) {
			log.debug("复制目录失败，源目录 " + srcDirName + " 不存在!");
			return false;
		}
		// 判断源目录是否是目录
		else if (!srcDir.isDirectory()) {
			log.debug("复制目录失败，" + srcDirName + " 不是一个目录!");
			return false;
		}
		// 如果目标文件夹名不以文件分隔符结尾，自动添加文件分隔符
		String descDirNames = descDirName;
		if (!descDirNames.endsWith(File.separator)) {
			descDirNames = descDirNames + File.separator;
		}
		File descDir = new File(descDirNames);
		// 如果目标文件夹存在
		if (descDir.exists()) {
			if (coverlay) {
				// 允许覆盖目标目录
				log.debug("目标目录已存在，准备删除!");
				if (!FileUtils.delFile(descDirNames)) {
					log.debug("删除目录 " + descDirNames + " 失败!");
					return false;
				}
			} else {
				log.debug("目标目录复制失败，目标目录 " + descDirNames + " 已存在!");
				return false;
			}
		} else {
			// 创建目标目录
			log.debug("目标目录不存在，准备创建!");
			if (!descDir.mkdirs()) {
				log.debug("创建目标目录失败!");
				return false;
			}

		}

		boolean flag = true;
		// 列出源目录下的所有文件名和子目录名
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 如果是一个单个文件，则直接复制
			if (files[i].isFile()) {
				flag = FileUtils.copyFile(files[i].getAbsolutePath(),
						descDirName + files[i].getName());
				// 如果拷贝文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 如果是子目录，则继续复制目录
			if (files[i].isDirectory()) {
				flag = FileUtils.copyDirectory(files[i]
						.getAbsolutePath(), descDirName + files[i].getName());
				// 如果拷贝目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			log.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 失败!");
			return false;
		}
		log.debug("复制目录 " + srcDirName + " 到 " + descDirName + " 成功!");
		return true;

	}

	/**
	 * 
	 * 删除文件，可以删除单个文件或文件夹
	 * 
	 * @param fileName 被删除的文件名
	 * @return 如果删除成功，则返回true，否是返回false
	 */
	public static boolean delFile(String fileName) {
 		File file = new File(fileName);
		if (!file.exists()) {
			log.debug(fileName + " 文件不存在!");
			return true;
		} else {
			if (file.isFile()) {
				return FileUtils.deleteFile(fileName);
			} else {
				return FileUtils.deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 
	 * 删除单个文件
	 * 
	 * @param fileName 被删除的文件名
	 * @return 如果删除成功，则返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				log.debug("删除单个文件 " + fileName + " 成功!");
				return true;
			} else {
				log.debug("删除单个文件 " + fileName + " 失败!");
				return false;
			}
		} else {
			log.debug(fileName + " 文件不存在!");
			return true;
		}
	}

	/**
	 * 
	 * 删除目录及目录下的文件
	 * 
	 * @param dirName 被删除的目录所在的文件路径
	 * @return 如果目录删除成功，则返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dirName) {
		String dirNames = dirName;
		if (!dirNames.endsWith(File.separator)) {
			dirNames = dirNames + File.separator;
		}
		File dirFile = new File(dirNames);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			log.debug(dirNames + " 目录不存在!");
			return true;
		}
		boolean flag = true;
		// 列出全部文件及子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = FileUtils.deleteFile(files[i].getAbsolutePath());
				// 如果删除文件失败，则退出循环
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = FileUtils.deleteDirectory(files[i]
						.getAbsolutePath());
				// 如果删除子目录失败，则退出循环
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			log.debug("删除目录失败!");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			log.debug("删除目录 " + dirName + " 成功!");
			return true;
		} else {
			log.debug("删除目录 " + dirName + " 失败!");
			return false;
		}

	}

	/**
	 * 创建单个文件
	 * @param descFileName 文件名，包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			log.debug("文件 " + descFileName + " 已存在!");
			return false;
		}
		if (descFileName.endsWith(File.separator)) {
			log.debug(descFileName + " 为目录，不能创建目录!");
			return false;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				log.debug("创建文件所在的目录失败!");
				return false;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				log.debug(descFileName + " 文件创建成功!");
				return true;
			} else {
				log.debug(descFileName + " 文件创建失败!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(descFileName + " 文件创建失败!");
			return false;
		}

	}

	/**
	 * 创建目录
	 * @param descDirName 目录名,包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createDirectory(String descDirName) {
		String descDirNames = descDirName;
		if (!descDirNames.endsWith(File.separator)) {
			descDirNames = descDirNames + File.separator;
		}
		File descDir = new File(descDirNames);
		if (descDir.exists()) {
			log.debug("目录 " + descDirNames + " 已存在!");
			return false;
		}
		// 创建目录
		if (descDir.mkdirs()) {
			log.debug("目录 " + descDirNames + " 创建成功!");
			return true;
		} else {
			log.debug("目录 " + descDirNames + " 创建失败!");
			return false;
		}

	}

	/**
	 * 压缩文件或目录
	 * @param srcDirName 压缩的根目录
	 * @param fileName 根目录下的待压缩的文件名或文件夹名，其中*或""表示跟目录下的全部文件
	 * @param descFileName 目标zip文件
	 */
	public static void zipFiles(String srcDirName, String fileName,
			String descFileName) {
		// 判断目录是否存在
		if (srcDirName == null) {
			log.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
			return;
		}
		File fileDir = new File(srcDirName);
		if (!fileDir.exists() || !fileDir.isDirectory()) {
			log.debug("文件压缩失败，目录 " + srcDirName + " 不存在!");
			return;
		}
		String dirPath = fileDir.getAbsolutePath();
		File descFile = new File(descFileName);
		try {
			ZipOutputStream zouts = new ZipOutputStream(new FileOutputStream(
					descFile));
			if ("*".equals(fileName) || "".equals(fileName)) {
				FileUtils.zipDirectoryToZipFile(dirPath, fileDir, zouts);
			} else {
				File file = new File(fileDir, fileName);
				if (file.isFile()) {
					FileUtils.zipFilesToZipFile(dirPath, file, zouts);
				} else {
					FileUtils
							.zipDirectoryToZipFile(dirPath, file, zouts);
				}
			}
			zouts.close();
			log.debug(descFileName + " 文件压缩成功!");
		} catch (Exception e) {
			log.debug("文件压缩失败：" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 解压缩ZIP文件，将ZIP文件里的内容解压到descFileName目录下
	 * @param zipFileName 需要解压的ZIP文件
	 * @param descFileName 目标文件
	 */
	public static boolean unZipFiles(String zipFileName, String descFileName) {
		String descFileNames = descFileName;
		if (!descFileNames.endsWith(File.separator)) {
			descFileNames = descFileNames + File.separator;
		}		
        try {
			// 根据ZIP文件创建ZipFile对象
			ZipFile zipFile = new ZipFile(zipFileName);
			ZipEntry entry = null;
			String entryName = null;
			String descFileDir = null;
			byte[] buf = new byte[4096];
			int readByte = 0;
			// 获取ZIP文件里所有的entry
			@SuppressWarnings("rawtypes")
			Enumeration enums = zipFile.getEntries();
			// 遍历所有entry
			while (enums.hasMoreElements()) {
				entry = (ZipEntry) enums.nextElement();
				// 获得entry的名字
				entryName = entry.getName();
				descFileDir = descFileNames + entryName;
				if (entry.isDirectory()) {
					// 如果entry是一个目录，则创建目录
					new File(descFileDir).mkdirs();
					continue;
				} else {
					// 如果entry是一个文件，则创建父目录
					new File(descFileDir).getParentFile().mkdirs();
				}
				File file = new File(descFileDir);
				// 打开文件输出流
				OutputStream os = new FileOutputStream(file);
				// 从ZipFile对象中打开entry的输入流
		        InputStream is = zipFile.getInputStream(entry);
				while ((readByte = is.read(buf)) != -1) {
					os.write(buf, 0, readByte);
				}
				os.close();
				is.close();
			}
			zipFile.close();
			log.debug("文件解压成功!");
			return true;
		} catch (Exception e) {
			log.debug("文件解压失败：" + e.getMessage());
			return false;
		}
	}

	/**
	 * 将目录压缩到ZIP输出流
	 * @param dirPath 目录路径
	 * @param fileDir 文件信息
	 * @param zouts 输出流
	 */
	public static void zipDirectoryToZipFile(String dirPath, File fileDir,
			ZipOutputStream zouts) {
		if (fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();
			// 空的文件夹
			if (files.length == 0) {
				// 目录信息
				ZipEntry entry = new ZipEntry(getEntryName(dirPath, fileDir));
				try {
					zouts.putNextEntry(entry);
					zouts.closeEntry();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					// 如果是文件，则调用文件压缩方法
					FileUtils
							.zipFilesToZipFile(dirPath, files[i], zouts);
				} else {
					// 如果是目录，则递归调用
					FileUtils.zipDirectoryToZipFile(dirPath, files[i],
							zouts);
				}
			}

		}

	}

	/**
	 * 将文件压缩到ZIP输出流
	 * @param dirPath 目录路径
	 * @param file 文件
	 * @param zouts 输出流
	 */
	public static void zipFilesToZipFile(String dirPath, File file,
			ZipOutputStream zouts) {
		FileInputStream fin = null;
		ZipEntry entry = null;
		// 创建复制缓冲区
		byte[] buf = new byte[4096];
		int readByte = 0;
		if (file.isFile()) {
			try {
				// 创建一个文件输入流
				fin = new FileInputStream(file);
				// 创建一个ZipEntry
				entry = new ZipEntry(getEntryName(dirPath, file));
				// 存储信息到压缩文件
				zouts.putNextEntry(entry);
				// 复制字节到压缩文件
				while ((readByte = fin.read(buf)) != -1) {
					zouts.write(buf, 0, readByte);
				}
				zouts.closeEntry();
				fin.close();
				System.out
						.println("添加文件 " + file.getAbsolutePath() + " 到zip文件中!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
	 * @param dirPath 目录名
	 * @param file entry文件名
	 * @return
	 */
	private static String getEntryName(String dirPath, File file) {
		String dirPaths = dirPath;
		if (!dirPaths.endsWith(File.separator)) {
			dirPaths = dirPaths + File.separator;
		}
		String filePath = file.getAbsolutePath();
		// 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
		if (file.isDirectory()) {
			filePath += "/";
		}
		int index = filePath.indexOf(dirPaths);

		return filePath.substring(index + dirPaths.length());
	}
	
	public static String getAbsolutePath(String urlPath) {
		if(StringUtils.isBlank(urlPath)) {
			return "";
		} else {
			return SpringContextHolder.getRootRealPath() + urlPath.substring(urlPath.indexOf("/", 1), urlPath.length());
		}
	}
	
	/**
	 * 将内容写入文件
	 * @param content
	 * @param filePath
	 */
	public static void writeFile(String content, String filePath) {
		try {
			if (FileUtils.createFile(filePath)){
				FileWriter fileWriter = new FileWriter(filePath, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(content);
				bufferedWriter.close();
				fileWriter.close();
			}else{
				log.info("生成失败，文件已存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
    public static String getFileRelativePath() {
        StringBuffer relativePath = new StringBuffer();
        relativePath.append(File.separator);
        relativePath.append("uploadFiles");
        relativePath.append(File.separator);
        return relativePath.toString();
    }

    /**
     * getFileExtensionName 获取文件扩展名
     * @param fileName
     * @return String
     */
    public static String getFileExtensionName(String fileName) {
        int index = fileName.lastIndexOf(SUFFIX_SPLIT);
        if (index == -1) {
            return "";
        }
        String extensionName = fileName.substring(index+1, fileName.length());
        return extensionName;
    }
    
    /**
     * getFileMimeType 获取文件类型
     * @param fileName
     * @return String
     */
    public static String getFileMimeType(String fileName) {
        Collection<?> mimeTypes=MimeUtil.getMimeTypes(fileName);
        if (mimeTypes.isEmpty()){
            return "";
        }
        return mimeTypes.iterator().next().toString();
    }
    
    /**
     * deleteFileExtensionName 获取不包含扩展名的文件名
     * @param fileName
     * @return String
     */
    public static String deleteFileExtensionName(String fileName) {
        int index = fileName.lastIndexOf(SUFFIX_SPLIT);
        if (index == -1) {
            return "";
        }
        String extensionName = fileName.substring(0, index);
        return extensionName;
    }
    
 
    /**
     * getFileAbsolutePath 获取文件绝对路径
     * @param request
     * @param relativePath
     * @return String
     */
    public static String getFileAbsolutePath(HttpServletRequest request,
            String relativePath) { 
        return getFileAbsolutePath( request,relativePath,true);
    }
    
    /**
     * getFileAbsolutePath 获取文件绝对路径
     * @param request
     * @param relativePath
     * @return String
     */
    public static String getFileAbsolutePath(HttpServletRequest request,String relativePath,boolean isDir) {
        StringBuffer absolutePath = new StringBuffer();
        String root = request.getSession().getServletContext().getRealPath("");
        root = root.substring(0,root.lastIndexOf(File.separator));
        absolutePath.append(root + File.separator + relativePath);
        if(isDir){
        	absolutePath.append(File.separator);
        } 
        return absolutePath.toString();
    }
    
    /**
     * getTargetFileName 获取新文件名
     * @param originalFileName
     * @return String
     */
    public static String getTargetFileName(String originalFileName) {
        StringBuffer targetFileName = new StringBuffer();
        targetFileName.append(System.currentTimeMillis());
        targetFileName.append(SUFFIX_SPLIT);
        targetFileName.append(getFileExtensionName(originalFileName));
        return targetFileName.toString();
    }
     
    /**
     * upload 上传文件
     * @param relativePath
     * @param fileDataName
     * @param request
     * @return  String
     */
    public static String upload(String relativePath,String fileDataName,MultipartHttpServletRequest request,Long fileSize){
    	final String[] CONTACT_ALLOW_TYPES = {"gif", "jpeg", "bmp", "png" ,"x-png","x-bmp","x-ms-bmp",
    			"image/gif","image/jpeg","image/bmp","image/png","mp3"};
    	String serverURL = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
    	String absolutePath=FileUtils.getFileAbsolutePath(request, relativePath);
        StringBuffer uplaodFileBuffer = new StringBuffer();
        MultipartFile file = request.getFile(fileDataName);
        if (file.isEmpty()==false) {
            String originalFileName=file.getOriginalFilename();
            String contentType=FileUtils.getFileMimeType(originalFileName);
            String targetFileName=FileUtils.getTargetFileName(originalFileName);
            String url=serverURL+relativePath.replace("\\", "/")+targetFileName;
            boolean complete=true;
            uplaodFileBuffer.append("{");
            if (null!=fileSize&&file.getSize()>fileSize){
                DecimalFormat    df   = new DecimalFormat("######0.00");   
                uplaodFileBuffer.append("1");
                uplaodFileBuffer.append(",\"message\":\"上传文件大于").append(df.format(fileSize/(1024d))).append("KB\"");
            }else{
                try {
                	boolean checkResult = false;
                    for (String allowType : CONTACT_ALLOW_TYPES) {
                        if ( allowType.equals(contentType) ) {
                            checkResult = true;
                        }
                    }
                    FileUtils.uplaodFile(file,absolutePath,targetFileName);
                    if(checkResult){
                        File targetPath = new File(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT);
                        if (targetPath.exists() == false) {
                            targetPath.mkdirs();
                        }
                        if(file.getSize()>=10485760L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.01f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else if(5242880L<=file.getSize() && file.getSize()<10485760L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.03f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else if(1048576L<=file.getSize() && file.getSize()<5242880L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.07f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else if(524288L<=file.getSize() && file.getSize()<1048576L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.1f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else if(102400L<=file.getSize() && file.getSize()<524288L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.2f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else if(10240L<=file.getSize() && file.getSize()<102400L){
                        	Thumbnails.of(absolutePath+targetFileName).scale(0.3f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }else{
                        	Thumbnails.of(absolutePath+targetFileName).scale(1f).toFile(absolutePath+PATH_SPLIT+"thumbnail"+PATH_SPLIT+targetFileName);
                        }
                        String thumbnailUrl =serverURL+relativePath.replace("\\", "/")+"thumbnail"+PATH_SPLIT+targetFileName;
                        uplaodFileBuffer.append("\"thumbnailUrl\":\"");
                        uplaodFileBuffer.append(thumbnailUrl+"\",");
                    }
                } catch (Exception e) {
                    log.error("上传文件出错！",e);
                    complete=false;
                }
                
                if (complete==true){
                    uplaodFileBuffer.append("\"error\":0");
                    uplaodFileBuffer.append(",\"originalFileName\":\"");
                    uplaodFileBuffer.append(originalFileName);
                    uplaodFileBuffer.append("\",\"targetFileName\":\"");
                    uplaodFileBuffer.append(targetFileName);
                    uplaodFileBuffer.append("\",\"contentType\":\"");
                    uplaodFileBuffer.append(contentType);
                    uplaodFileBuffer.append("\",\"url\":\"");
                    uplaodFileBuffer.append(url);
                    uplaodFileBuffer.append("\"");
                }else{
                    uplaodFileBuffer.append("\"error\":1");
                    uplaodFileBuffer.append(",\"message\":\"上传文件失败！\"");
                }
            }
            
            
            uplaodFileBuffer.append("}");
       }
       return uplaodFileBuffer.toString();         
    } 
    
    
    /**
     * upload 上传语音
     * @param relativePath
     * @param fileDataName
     * @param request
     * @return  String
     */
    public static String uploadVoice(String relativePath,String fileDataName,MultipartHttpServletRequest request,Long fileSize){
    	final String[] CONTACT_ALLOW_TYPES = {"jpg","mp3","mp4","application/octet-stream"};
    	String serverURL = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
    	String absolutePath=FileUtils.getFileAbsolutePath(request, relativePath);
        StringBuffer uplaodFileBuffer = new StringBuffer();
        MultipartFile file = request.getFile(fileDataName);
        String thumbnailUrl = null;
        String result = null;
        if (file.isEmpty()==false) {
            String originalFileName=file.getOriginalFilename();
            String contentType=FileUtils.getFileMimeType(originalFileName);
            String targetFileName=FileUtils.getTargetFileName(originalFileName);
            boolean complete=true;
            uplaodFileBuffer.append("{");
            if (null!=fileSize&&file.getSize()>fileSize){
                DecimalFormat    df   = new DecimalFormat("######0.00");   
                uplaodFileBuffer.append("1");
                uplaodFileBuffer.append(",\"message\":\"上传文件大于").append(df.format(fileSize/(1024d))).append("KB\"");
            }else{
                try {
                	boolean checkResult = false;
                    for (String allowType : CONTACT_ALLOW_TYPES) {
                        if ( allowType.equals(contentType) ) {
                            checkResult = true;
                        }
                    }
                    if(checkResult) {
                    	targetFileName = targetFileName.replace(".amr", ".mp3");
	                    FileUtils.uplaodFile(file,absolutePath,targetFileName);
	                    thumbnailUrl =serverURL+relativePath.replace("\\", "/")+targetFileName;
                    }
                } catch (Exception e) {
                    log.error("上传文件出错！",e);
                    complete=false;
                }
                
                if (complete==true){
                  result = thumbnailUrl;
                }
            }
            
            
       }
       return result;         
    } 
    
    /**
     * uplaodFile 上传文件
     * @param originalFile
     * @param uploadPath
     * @param fileName
     * @throws IllegalStateException
     * @throws IOException
     */
    public static void uplaodFile(MultipartFile originalFile,String uploadPath,String fileName) throws IllegalStateException, IOException{
        File targetPath = new File(uploadPath);
        if (targetPath.exists() == false) {
            targetPath.mkdirs();
        }
        File targetFile = new File(uploadPath+fileName);
        originalFile.transferTo(targetFile);
    }
    
    /**
     * getTargetFileName 获取新的文件名
     * @param  fileTreeBuffer
     * @param  parentPath
     * @param  templateRootFile
     * @param  recursion
     */
    public static void getTargetFileName(StringBuffer fileTreeBuffer,String parentPath,File templateParentFile,boolean fullFileName,boolean recursion) {
        File[] fileList=templateParentFile.listFiles();
        Arrays.sort(fileList, new Comparator<File>(){
            public int compare(File first, File second) {
                if (first.isDirectory() && second.isFile()) {
                    return -1;
                } else if (first.isDirectory() && second.isDirectory()) {
                    return Collator.getInstance(Locale.CHINA).compare(first.getName(), second.getName());
                } else if (first.isFile() && second.isDirectory()) {
                    return 1;
                } else {
                    return Collator.getInstance(Locale.CHINA).compare(first.getName(), second.getName());
                }        
            }
        });
        for(File templateFile:fileList){
            String name=templateFile.getName();
            if (templateFile.isFile()){
                if (TEMPLATE_SUFFIX.equalsIgnoreCase(getFileExtensionName(name))){
                    fileTreeBuffer.append("{id:\"");
                    if (StringUtils.isNotBlank(parentPath)){
                        fileTreeBuffer.append(parentPath);
                        fileTreeBuffer.append(PATH_SPLIT);
                    }
                    if (fullFileName){
                        fileTreeBuffer.append(name);
                    }else{
                        fileTreeBuffer.append(deleteFileExtensionName(name));
                    }
                    fileTreeBuffer.append("\",");
                    fileTreeBuffer.append("name:\"");
                    fileTreeBuffer.append(name);
                    fileTreeBuffer.append("\",");
                    fileTreeBuffer.append("pid:\"");
                    fileTreeBuffer.append(parentPath);
                    fileTreeBuffer.append("\",");
                    fileTreeBuffer.append("isParent:false");
                    fileTreeBuffer.append(",nocheck:false");
                    fileTreeBuffer.append(",path:\"");
                    fileTreeBuffer.append(templateFile.getPath());
                    fileTreeBuffer.append("\"");
                    fileTreeBuffer.append("},");
                }
            }else if(templateFile.isDirectory()){
                fileTreeBuffer.append("{id:\"");
                if (StringUtils.isNotBlank(parentPath)){
                    fileTreeBuffer.append(parentPath);
                    fileTreeBuffer.append(PATH_SPLIT);
                }
                fileTreeBuffer.append(name);
                fileTreeBuffer.append("\",");
                fileTreeBuffer.append("name:\"");
                fileTreeBuffer.append(name);
                fileTreeBuffer.append("\",");
                fileTreeBuffer.append("pid:\"");
                fileTreeBuffer.append(parentPath);
                fileTreeBuffer.append("\",");
                fileTreeBuffer.append("isParent:true");
                fileTreeBuffer.append(",nocheck:true");
                fileTreeBuffer.append(",path:\"");
                fileTreeBuffer.append(templateFile.getPath());
                fileTreeBuffer.append("\"");
                fileTreeBuffer.append("},");
                if (recursion){
                    getTargetFileName(fileTreeBuffer,name,templateFile,fullFileName,recursion);
                }
            }
        }
    }

}
