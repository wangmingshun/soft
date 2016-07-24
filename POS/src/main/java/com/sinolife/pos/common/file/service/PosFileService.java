package com.sinolife.pos.common.file.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.store.TempFileFactory;

@Service("posFileService")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PosFileService {

	@Autowired
	PosFileDAO dao;
	
	private static TempFileFactory fileFactory = TempFileFactory.getInstance(PosFileService.class);
	
	/**
	 * 上传单个文件
	 * @param file    文件
	 * @param subPath 子路径（若需要）
	 * @return 文件在im系统的ID，唯一ID
	 */
	public String upload(MultipartFile file, String subPath){
		String fileId = null;
		
		byte[] content = null;
		SFFile tempFile = null;
		OutputStream os = null;
		try {
			if(file != null) {
				content = file.getBytes();
				tempFile = fileFactory.createTempFile();
				os = tempFile.openOutputStream();
				os.write(content);
				os.flush();
				os.close();
				
				if(StringUtils.isBlank(subPath)){
					subPath = "receipt";
				}
				
				SFFilePath filePath = new SFFilePath();
				filePath.setModule("pos");
				filePath.setModuleSubPath(subPath.split("\\/"));
				filePath.setStogeType(StogeType.WEEK);
				fileId = PlatformContext.getIMFileService().putFile(tempFile, filePath);
			}
		} catch (Exception e) {
			throw new RuntimeException("文件"+file.getOriginalFilename()+"上传异常：",e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch(Exception e) {}
			}
			if(tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
		}
		return fileId;
	}

	/**
	 * 插入pos_file_info表一条记录
	 * @param fileId
	 * @param fileName
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @param userId
	 */
	public String insertPosFileInfo(String fileId, String fileName, String policyNo, String posNo, String fileType, String userId){
		if(StringUtils.isBlank(fileId)||StringUtils.isBlank(fileName)||(StringUtils.isBlank(policyNo)&&StringUtils.isBlank(posNo))||StringUtils.isBlank(userId)){

		}else{
			dao.insertPosFileInfo(fileId, fileName, policyNo, posNo, fileType, userId);
		}
		
		return "Y";
	}
	
	
	/**
	 * 插入pos_file_info表一条记录(vip尊荣服务)
	 * @param fileId
	 * @param fileName
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @param userId
	 */
	public String insertPosFileInfo(String fileId, String fileName, String fileType, String userId){
		if(StringUtils.isBlank(fileId)||StringUtils.isBlank(fileName)||StringUtils.isBlank(userId)){

		}else{
			dao.insertPosFileInfo(fileId, fileName,fileType, userId);
		}
		
		return "Y";
	}
	
	
	
	/**
	 * 查询已上传的文件Id和Name
	 * 支持多种文件类型一起查询
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @return Map{fileId-文件Id，fileName-文件名}
	 */
	public List<Map<String,String>> queryPosFileInfo(String policyNo,String posNo,String fileType){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String[] type = fileType.split(";");
		for (int i = 0; type!=null && i < type.length; i++) {
			list.addAll(dao.queryPosFileInfo(policyNo, posNo, type[i]));
		}
		return list;
	}
	
	
	
	/**
	 * 查询已上传的文件Id和Name，只取一个
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @return Map{fileId-文件Id，fileName-文件名}
	 */
	public Map<String,String> queryPosFileInfoOne(String policyNo,String posNo,String fileType){
		List list = queryPosFileInfo(policyNo, posNo, fileType);
		
		if(list!=null && list.size()>0){
			return (Map<String,String>)list.get(0);
		}
		return null;
	}
	
	/**
	 * 上传和记录文件一体
	 * @param file
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 */
	public void uploadAndRecord(MultipartFile file, String policyNo, String posNo, String fileType){
		String fileId = upload(file, null);
		insertPosFileInfo(fileId, file.getOriginalFilename(), policyNo, posNo, fileType, PlatformContext.getCurrentUser());
	}
	
	/**
	 * 上传和记录文件一体
	 * @param file
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 */
	public void uploadAndRecord(MultipartFile file,  String fileType){
		String fileId = upload(file, null);
		insertPosFileInfo(fileId, file.getOriginalFilename(), fileType, PlatformContext.getCurrentUser());
	}
	
	
	
	/**
	 * 取得下载文件的url和文件名,只取一个文件
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @return 非null的HashMap
	 */
	public Map<String,String> oneFileNameUrl(String policyNo,String posNo,String fileType){
		Map<String, String> rstMap = new HashMap<String,String>();
		
		Map<String,String> map = queryPosFileInfoOne(policyNo, posNo, fileType);
		if(map!=null){
			String url = PlatformContext.getIMFileService().getFileURL(map.get("FILE_ID"), new UrlSignature(), true, map.get("FILE_NAME"), null, null);
			rstMap.put("fileUrl", url);
			rstMap.put("fileName", map.get("FILE_NAME"));
		}
		
		return rstMap;
	}
	
	/**
	 * 取得下载文件的url和文件名
	 * @param policyNo
	 * @param posNo
	 * @param fileType
	 * @return 非null的List<Map<String, String>>
	 */
	public List<Map<String, String>> fileNameUrlList(String policyNo,String posNo,String fileType){
		List<Map<String, String>> rstList = new ArrayList<Map<String,String>>();
		
		List<Map<String,String>> list = queryPosFileInfo(policyNo, posNo, fileType);
		for (int i = 0; list!=null && i < list.size(); i++) {
			Map<String,String> map = list.get(i);
			String url = PlatformContext.getIMFileService().getFileURL(map.get("FILE_ID"), new UrlSignature(), true, map.get("FILE_NAME"), null, null);
			map.put("fileUrl", url);
			map.put("fileName", map.get("FILE_NAME"));
			
			rstList.add(map);
		}
		
		return rstList;
	}
	
}
