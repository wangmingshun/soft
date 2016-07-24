package com.sinolife.pos.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 邮储通知书的FTP访问封转类
 */
public class FTPUtil {

	private Logger logger = Logger.getLogger(getClass());
	
	private FTPClient ftpClient;
	
	private boolean loggedIn = false;
	
	private InetAddress serverAddress;
	
	private int serverPort;
	
	private String user;
	
	private String pass;
	
	private int serverTimeout;
	
	private String defaultDirectory;
	
	private static ThreadLocal<FTPUtil> ftpUtilThreadLocal = new ThreadLocal<FTPUtil>();
	
	/**
	 * 取得当前线程绑定的新FTPUtil对象
	 * @return
	 */
	public static FTPUtil newInstance() {
		destroy();
		ftpUtilThreadLocal.set(new FTPUtil());
		return ftpUtilThreadLocal.get();
	}
	
	/**
	 * 销毁和当前线程绑定的FTPUtil对象，如果当前是连接状态，则会中断连接
	 */
	public static void destroy() {
		FTPUtil instance = ftpUtilThreadLocal.get();
		if(instance != null) {
			ftpUtilThreadLocal.remove();
			instance.disconnect();
		}
	}
	
	/**
	 * 私有构造方法
	 */
	private FTPUtil() {
		PlatformContext.getRuntimeConfig();
		
		String address = RuntimeConfig.get("com.sinolife.pos.print.notice.server.address", String.class);
		String port = RuntimeConfig.get("com.sinolife.pos.print.notice.server.port", String.class);
		String timeout = RuntimeConfig.get("com.sinolife.pos.print.notice.server.timeout", String.class);
		String system = RuntimeConfig.get("com.sinolife.pos.print.notice.server.system", String.class);
		user = RuntimeConfig.get("com.sinolife.pos.print.notice.server.user", String.class);
		pass = RuntimeConfig.get("com.sinolife.pos.print.notice.server.pass", String.class);
		defaultDirectory = RuntimeConfig.get("com.sinolife.pos.print.notice.server.directory", String.class);
		
		try {
			serverAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		if(StringUtils.isNotBlank(timeout)) {
			serverTimeout = Integer.parseInt(timeout);
		} else {
			serverTimeout = 30000;
		}
		if(StringUtils.isNotBlank(port)) {
			serverPort = Integer.parseInt(port);
		} else {
			serverPort = -1;
		}
		
		ftpClient = new FTPClient();
		Set<String> validSystem = new HashSet<String>();
		validSystem.add(FTPClientConfig.SYST_MVS);
		validSystem.add(FTPClientConfig.SYST_NT);
		validSystem.add(FTPClientConfig.SYST_OS2);
		validSystem.add(FTPClientConfig.SYST_OS400);
		validSystem.add(FTPClientConfig.SYST_UNIX);
		validSystem.add(FTPClientConfig.SYST_VMS);
		system = system.toUpperCase();
		if(StringUtils.isNotBlank(system) && validSystem.contains(system)) {
			FTPClientConfig ftpClientConfig = new FTPClientConfig(system);
			ftpClient.configure(ftpClientConfig);
			logger.info("config ftp server as " + system);
		} else {
			logger.info("config ftp server as " + FTPClientConfig.SYST_NT);
			FTPClientConfig ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
			ftpClient.configure(ftpClientConfig);
		}
		ftpClient.enterLocalPassiveMode();
		ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
			@Override
			public void protocolCommandSent(ProtocolCommandEvent event) {
				logger.info(event.getMessage());
			}

			@Override
			public void protocolReplyReceived(ProtocolCommandEvent event) {
				logger.info(event.getMessage());
			}});
	}
	
	/**
	 * 连接到FTP服务器，并切换到指定的默认目录
	 */
	public void connect() {
		logger.info("connect to FTP server => " + user + "/" + pass + "@" + serverAddress + ":" + serverPort + ")");
		ftpClient.setDefaultTimeout(serverTimeout);
		try {
			if(serverPort > 0) {
				ftpClient.connect(serverAddress, serverPort);
			} else {
				ftpClient.connect(serverAddress);
			}
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new RuntimeException("FTP server refused connection.");
			}
			if(StringUtils.isNotBlank(user)) {
				loggedIn = ftpClient.login(user, pass);
				if(loggedIn) {
					logger.info("login success!");
				} else {
					throw new RuntimeException("login failed, pls check the config.");
				}
			}
			logger.info("change working directory to " + defaultDirectory);
			if(!ftpClient.changeWorkingDirectory(defaultDirectory)) {
				throw new RuntimeException("change working directory to " + defaultDirectory + " fail");
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		} catch(IOException ioe) {
			throw new RuntimeException(ioe.getMessage(), ioe);
		}
	}
	
	/**
	 * 中断与服务器的连接
	 */
	public void disconnect() {
		if(ftpClient.isConnected()) {
			if(loggedIn) {
				try {
					ftpClient.logout();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(ftpClient.isConnected())
					ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("disconnect success!");
	}
	
	/**
	 * 创建指定的目录，如果目录存在或创建失败，则抛出运行时异常
	 * @param directoryName
	 * @param directoryCreated
	 */
	public void init(String directoryName, boolean directoryCreated) {
		if(!directoryCreated) {
			if(!isDirectoryExists(directoryName)) {
				try {
					logger.info("create directory " + directoryName);
					ftpClient.makeDirectory(directoryName);
					int reply = ftpClient.getReplyCode();
					if (!FTPReply.isPositiveCompletion(reply)) {
						throw new RuntimeException("make directory failed!");
					}
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else {
				throw new RuntimeException(directoryName + " exists!");
			}
		}
		try {
			logger.info("change working directory to " + defaultDirectory + "/" + directoryName);
			if(!ftpClient.changeWorkingDirectory(defaultDirectory + "/" + directoryName)) {
				throw new RuntimeException("change working directory to " + defaultDirectory + "/" + directoryName + " fail");
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 按文件名称获取文件
	 * @param fileName
	 * @return
	 */
	public FTPFile getFile(String fileName) {
		FTPFile[] files;
		try {
			files = ftpClient.listFiles();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		if(files != null && files.length > 0) {
			for(FTPFile file : files) {
				if(file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
					return file;
				}
			}
		}
		return null;
	}
	
	/**
	 * 判断指定的目录是否存在
	 * @param directoryName
	 * @return
	 */
	public boolean isDirectoryExists(String directoryName) {
		FTPFile[] files;
		try {
			files = ftpClient.listFiles();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		if(files != null && files.length > 0) {
			for(FTPFile file : files) {
				logger.debug("listing ... " + file.getName());
				if(file.isDirectory() && file.getName().equalsIgnoreCase(directoryName)) {
					logger.info("directory " + directoryName + " exists");
					return true;
				}
			}
		}
		logger.info("directory " + directoryName + " not exists");
		return false;
	}
	
	/**
	 * 判断指定的文件是否存在
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName) {
		FTPFile[] files;
		try {
			files = ftpClient.listFiles();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		if(files != null && files.length > 0) {
			for(FTPFile file : files) {
				logger.debug("listing ... " + file.getName());
				if(file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
					logger.info("file " + fileName + " exists");
					return true;
				}
			}
		}
		logger.info("file " + fileName + " not exists");
		return false;
	}
	
	/**
	 * 上传文件
	 * @param file 本地文件
	 * @param fileName 上传到服务器的文件名
	 * @return 成功与否
	 */
	public boolean uploadFile(File file, String fileName) {
		InputStream is = null;
		try {
			if(!file.exists()) {
				throw new RuntimeException("file " + file.getName() + " not exists.");
			}
			is = new FileInputStream(file);
			if(ftpClient.storeFile(fileName, is)) {
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new RuntimeException("upload file " + fileName + " failed!");
				}
				return true;
			}
			return false;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 下载文件到指定的目录，下载之后的文件名会被添加上“server~”前缀
	 * @param fileName
	 * @param directory
	 * @return
	 */
	public File downloadFile(String fileName, File directory) {
		File tmpFile = null;
		OutputStream os = null;
		try {
			tmpFile = new File(directory.getAbsolutePath() + "/server~" + fileName);
			os = new FileOutputStream(tmpFile);
			if(ftpClient.retrieveFile(fileName, os)) {
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new RuntimeException("download file " + fileName + " failed!");
				}
				return tmpFile;
			}
			return null;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 给文件改名
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean renameFile(String from, String to) {
		try {
			return ftpClient.rename(from, to);
		} catch (IOException e) {
			logger.error(e);
		}
		return false;
	}
	
	/**
	 * 删除服务器上的指定文件
	 * @param fileName
	 * @return
	 */
	public boolean delete(String fileName) {
		try {
			return ftpClient.deleteFile(fileName);
		} catch (IOException e) {
			logger.error(e);
		}
		return false;
	}
	
}
