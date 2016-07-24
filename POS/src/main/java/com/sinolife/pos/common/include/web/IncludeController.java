package com.sinolife.pos.common.include.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.im.http.client.IM;
import com.sinolife.im.http.model.Constant;
import com.sinolife.im.http.model.ImageInfo;
import com.sinolife.im.imview.applets.imaging.JsonUtil;
import com.sinolife.pos.acceptance.branch.dto.ClientLocateCriteriaDTO;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.StaffInfoDTO;
import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.sf.framework.comm.BarCodeUtil;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.store.TempFileFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
@RequestMapping("/include")
public class IncludeController extends PosAbstractController {

	@Autowired
	private IncludeService includeService;

	private static TempFileFactory TEM_FILE_FACTORY = TempFileFactory
			.getInstance(IncludeController.class);

	@Autowired
	private IM imageClient;

	@Autowired
	BranchQueryService branchService;

	//@Autowired
	//private TransactionTemplate txTmpl;

	/******************************
	 * 公用机构树页面查询机构基表信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/branchTree")
	@ResponseBody
	public List queryBranchTree(String branchLevel, String branchCode,
			String root) {
		Map pMap = new HashMap();
		pMap.put("branchCode", branchCode); // 一次只查该机构号下一级机构，不传此参数查出86机构
		pMap.put("branchLevel", branchLevel);// 该条件仅用于决定返回数据的state状态，未参与取数条件
		pMap.put("root", root); // 根节点查询标志,Y查根节点，N查子节点

		return includeService.queryBranchTree(pMap);
	}

	/***********************************
	 * 用户权限信息子页面查询用户权限明细
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/userPrivilege")
	@ResponseBody
	public List queryUserPrivilege(String user) {
		return includeService.queryUserPrivilege(PosUtils.parseInputUser(user));
	}

	/******************************
	 * 查询省下面的市
	 * 
	 * @param province
	 * @return
	 */
	@RequestMapping(value = "/queryCityByProvince")
	@ResponseBody
	public List queryCityByProvince(String province) {
		return includeService.queryCityByProvince(province);
	}

	/******************************
	 * 查询市下面的县
	 * 
	 * @param city
	 * @return
	 */
	@RequestMapping(value = "/queryAreaByCity")
	@ResponseBody
	public List queryAreaByCity(String city) {
		return includeService.queryAreaByCity(city);
	}

	/**
	 * 查询省市对应有哪些机构
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryBranchInProvinceCity")
	@ResponseBody
	public List queryBranchInProvinceCity(String province, String city,
			String channel) {

		return includeService
				.queryBranchInProvinceCity(province, city, channel);
	}

	/******************************
	 * 查询柜面列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/counter")
	@ResponseBody
	public List queryCounter(String branchCode) {
		return includeService.queryCounter(branchCode);
	}

	/**
	 * 根据工号查询业务员信息
	 * 
	 * @param empNo
	 * @return
	 */
	@RequestMapping(value = "/queryStaffByEmpNo")
	@ResponseBody
	public Map<String, Object> queryStaffByEmpNo(String empNo) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (includeService.isStaffExists(empNo)) {
			StaffInfoDTO staffDTO = includeService.queryStaffByEmpNo(empNo);
			if (staffDTO == null) {
				retMap.put("flag", "N");
				retMap.put("message", "该业务员已离职!");
			} else {
				retMap.put("flag", "Y");
				retMap.put("staffInfo", staffDTO);
			}
		} else {
			retMap.put("flag", "N");
			retMap.put("message", "该业务员不存在!");
		}
		return retMap;
	}

	/**
	 * 根据银行机构代码查询银行列表
	 * 
	 * @param branchCode
	 * @return
	 */
	@RequestMapping(value = "/queryBankList")
	@ResponseBody
	public List<BankInfoDTO> queryBankList(@RequestParam String provinceCode,
			@RequestParam String cityCode, @RequestParam String bankCategory) {
		return includeService.queryBankList("156", provinceCode, cityCode,
				bankCategory);
	}

	/*
	 * 查询电销渠道保单投保单影像及回执影像
	 */
	@RequestMapping(value = "/getImagesByBarcodeNo")
	public ModelAndView getImagesByBarcodeNo(
			@RequestParam  String barcodeNo) {		
		
//		return txTmpl.execute(new TransactionCallback<ModelAndView>() {
//			@Override
//			public ModelAndView doInTransaction(
//					TransactionStatus transactionStatus) {
				ModelAndView mav = new ModelAndView(
						ViewNames.INCLUDE_DIS_FULL_IMAGE);
				try {
					// 调用影像查询方法查询影像
					List<ImageInfo> imageList = null;
					imageList = imageClient.queryImageByMainIndex(barcodeNo,
							Constant.CONTENT_YES);

					// 当查询的结果不为空的时候
					if (imageList != null && imageList.size() > 0) {
						// 对将影像数组转化成json的字符串
						String jsonInfo = JsonUtil.toJsonString(imageList);
						// 将处理结果返回给request
						mav.addObject("imageInfo", jsonInfo);
						mav.addObject("imageSize", imageList.size());
					} else {
						mav.addObject("message", "找不到影像信息");
						mav.addObject("imageSize", 0);
					}
				} catch (Exception e) {
					logger.error(e);
					mav.addObject("message",
							barcodeNo + "查询影像出错：" + e.getMessage());
					mav.addObject("imageSize", 0);
				}
				return mav;
//			}
//		});
	}

	/**
	 * 查询影像信息.
	 */
	@RequestMapping(value = "/getImageByBarcodeNo")
	public ModelAndView getImageByBarcodeNo(@RequestParam String barcodeNo) {
		ModelAndView mav = new ModelAndView(ViewNames.INCLUDE_DIS_FULL_IMAGE);
		// barcodeNo = "1111010000000110";
		// 调用影像查询方法查询影像
		try {
			// 调用影像查询方法查询影像
			List<ImageInfo> imageList = null;
			List<ImageInfo> newImageList = new ArrayList<ImageInfo>();
			List<ImageInfo> tmpImageList = new ArrayList<ImageInfo>();
			if (StringUtils.isNotBlank(barcodeNo)) {
				if (barcodeNo.startsWith("1211")
						|| barcodeNo.startsWith("1234")
						|| barcodeNo.startsWith("1233")
						|| barcodeNo.startsWith("1235")
						|| barcodeNo.startsWith("1212")
						|| barcodeNo.startsWith("1213")
						|| barcodeNo.startsWith("1214")
						|| barcodeNo.startsWith("1215")
						|| barcodeNo.startsWith("1216")) {// 保全申请书按主索引查,保全申请书条形码和主索引相同
					imageList = imageClient.queryImageByMainIndex(barcodeNo,
							Constant.CONTENT_YES);
					
					logger.info(barcodeNo+"startsWith12");

				} else {
					imageList = imageClient.queryImageByBarcode(barcodeNo,
							Constant.CONTENT_YES);
					
					for (ImageInfo imageInfo : imageList) {
						if (!imageInfo.getDocTypeName().equals("证件类资料")) {
							newImageList.add(imageInfo);
							logger.info(barcodeNo+"newImageList");
						} else {
							tmpImageList.add(imageInfo);
							logger.info(barcodeNo+"tmpImageList");
						}

					}

					Collections.sort(tmpImageList, new Comparator<ImageInfo>() {
						@Override
						public int compare(ImageInfo imageInfo1,
								ImageInfo imageInfo2) {

							if (imageInfo1.getScanTime().compareTo(
									imageInfo2.getScanTime()) > 0) {

								return 1;
							} else {
								return -1;
							}

						}

					});

					newImageList.addAll(tmpImageList);
					imageList = newImageList;

				}
			}
			
			// 当查询的结果不为空的时候
			if (imageList != null && imageList.size() > 0) {
				// 对将影像数组转化成json的字符串
				String jsonInfo = JsonUtil.toJsonString(imageList);
				logger.info(barcodeNo+"imageList>0"+jsonInfo);
				// 将处理结果返回给request
				mav.addObject("imageInfo", jsonInfo);
				mav.addObject("imageSize", imageList.size());
			} else {
				logger.info(barcodeNo+"imageList=0");
				mav.addObject("message", "找不到影像信息");
				mav.addObject("imageSize", 0);
			}
		} catch (Exception e) {
			logger.error(barcodeNo +"error"+e);
			mav.addObject("message", barcodeNo + "查询影像出错：" + e.getMessage());
			mav.addObject("imageSize", 0);
		}
		return mav;
	}

	/**
	 * 判断申请书条形码是否已经被使用（在pos_apply_files中存在记录）
	 * 
	 * @param barcodeNo
	 * @return
	 */
	@RequestMapping(value = "/isBarcodeNoNotUsed")
	@ResponseBody
	public Boolean isBarcodeNoNotUsed(@RequestParam String barcodeNo) {
		return BarCodeUtil.checkBarCodeSum(barcodeNo)
				&& !includeService.isBarcodeNoUsed(barcodeNo);
	}

	/**
	 * 查询全部的产品信息，返回用于页面自动完成的json字符串
	 * 
	 * @return
	 */
	@RequestMapping(value = "/allProducts")
	@ResponseBody
	public List productsJson() {
		return includeService.productsList();
	}

	/**
	 * 查询定位客户
	 * 
	 * @param clientNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/clientSearch")
	@ResponseBody
	public List clientSearch(HttpServletRequest request) {
		ClientLocateCriteriaDTO criteria = new ClientLocateCriteriaDTO();
		criteria.setQueryType(request.getParameter("queryType"));
		criteria.setClientNo(request.getParameter("clientNo"));
		criteria.setPolicyNo(request.getParameter("policyNo"));
		criteria.setApplicantOrInsured(request
				.getParameter("applicantOrInsured"));
		criteria.setIdTypeCode(request.getParameter("idTypeCode"));
		criteria.setClientIdNo(request.getParameter("clientIdNo"));
		criteria.setClientName(request.getParameter("clientName"));
		criteria.setSex(request.getParameter("sex"));
		criteria.setPhoneNo(request.getParameter("phoneNo"));
		try {
			criteria.setBirthday(new SimpleDateFormat("yyyy-MM-dd")
					.parse(request.getParameter("birthday")));
		} catch (ParseException e) {
		}

		List<ClientInformationDTO> cList = branchService
				.queryClientByCriteria(criteria);
		List list = new ArrayList();
		for (int i = 0; cList != null && i < cList.size(); i++) {
			ClientInformationDTO client = cList.get(i);
			Map map = new HashMap();
			map.put("clientNo", client.getClientNo());
			map.put("clientName", client.getClientName());
			map.put("sexDesc", client.getSexDesc());
			map.put("birthday",
					DateFormat.getDateInstance().format(client.getBirthday()));
			map.put("idTypeDesc",
					client.getIdTypeDesc() == null ? "" : client
							.getIdTypeDesc());
			map.put("idNo", client.getIdNo() == null ? "" : client.getIdNo());

			list.add(map);
		}
		return list;
	}

	/**
	 * 文件上传至IM，并返回IM文件ID
	 * 
	 * @param fileToUpload
	 * @param filePath
	 * @return
	 */
	@RequestMapping(value = "/uploadFile")
	public ModelAndView uploadFile(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) request;
			String fileName = null;
			byte[] content = null;
			SFFile tempFile = null;
			String fileId = null;
			String errorMessage = null;
			OutputStream os = null;
			try {
				MultipartFile fileToUpload = mhsr.getFile("fileToUpload");
				String filePath = mhsr.getParameter("filePath");
				if (fileToUpload != null) {
					fileName = fileToUpload.getOriginalFilename();
					content = fileToUpload.getBytes();

					tempFile = TEM_FILE_FACTORY.createTempFile();
					os = tempFile.openOutputStream();
					os.write(content);
					os.flush();
					os.close();

					SFFilePath fileDestinationPath = new SFFilePath();
					fileDestinationPath.setModule("pos");
					fileDestinationPath.setModuleSubPath(filePath.split("\\/"));
					fileDestinationPath.setStogeType(StogeType.MONTH);
					// fileDestinationPath.setMimeType(MimeType.application_pdf);
					fileId = PlatformContext.getIMFileService().putFile(
							tempFile, fileDestinationPath);
				}
			} catch (Exception e) {
				logger.error(e);
				errorMessage = "上传文件失败：" + e.getMessage();
			} finally {
				PosUtils.safeCloseOuputStream(os);
				PosUtils.deleteFile(tempFile);
			}
			if (StringUtils.isNotBlank(fileName)) {
				retMap.put("fileName", fileName);
			}
			if (StringUtils.isNotBlank(fileId)) {
				retMap.put("fileId", fileId);
			}
			if (StringUtils.isNotBlank(errorMessage)) {
				retMap.put("errorMessage", errorMessage);
			}
		} else {
			retMap.put("errorMessage", "上传失败：请选择要上传的文件");
		}
		PrintWriter pw = null;
		response.setContentType("text/html");
		try {
			pw = response.getWriter();
			pw.print(JSONSerializer.toJSON(retMap).toString());
			pw.flush();
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		return null;
	}

	/**
	 * 根据机构代码尝试查询省市，不保证能查到
	 */
	@RequestMapping(value = "/queryPrivinceAndCityByBranchCode")
	@ResponseBody
	public Map<String, Object> queryPrivinceAndCityByBranchCode(
			@RequestParam String branchCode) {
		return includeService.queryPrivinceAndCityByBranchCode(branchCode);
	}

	/**
	 * 检查用户录入的机构代码是否属于用户权限下的
	 * 
	 * @param branchCode
	 * @return
	 */
	@RequestMapping(value = "/checkUserBranchCode")
	@ResponseBody
	public String checkUserBranchCode(String branchCode) {
		return includeService.checkUserBranchCode(branchCode,
				PlatformContext.getCurrentUser());
	}

}
