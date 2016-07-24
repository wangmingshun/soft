package com.sinolife.pos.others.undelivery.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.unihub.framework.util.common.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gwt.user.datepicker.client.DateBox.Format;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_19;
import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.undelivery.dao.UnDeliveryDAO;
import com.sinolife.pos.posappointmentsync.PosAppointmentSyncImpl;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.commonaccept.CommonAcceptImpl;

@Service("unDeliveryService")
public class UnDeliveryService {

	@Autowired
	UnDeliveryDAO dao;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private ServiceItemsDAO_19 serviceItemsDAO_19;
	
	@Autowired
	private CommonAcceptImpl commonAcceptImpl;
	
	/**
	 * 解析上传的退信数据文件，将数据更新到函件表
	 * 
	 * @param filePath
	 * @throws IOException
	 * @throws BiffException
	 */
	public File parseFile(String filePath) throws BiffException, IOException {
		File outFile = new File(filePath);
		Workbook book = Workbook.getWorkbook(new File(filePath));
		WritableWorkbook bookWritable = Workbook.createWorkbook(new FileOutputStream(filePath));
		Sheet sheet = book.getSheet(0);
		WritableSheet sheetWritable = bookWritable.createSheet(sheet.getName(), 0);
		try {
			for (int i = 0; i < sheet.getRows(); i++) {
				Cell[] cell = sheet.getRow(i);
				sheetWritable.setColumnView(i,20);
				for (int j = 0; j < sheet.getRow(i).length; j++) {

					sheetWritable.addCell(new Label(j, i, cell[j].getContents()));
				}
				if (i==0) {
					sheetWritable.addCell(new Label(sheet.getRow(i).length, i, "处理结果"));					
				}else{
					if (cell != null && cell.length > 0) {
						String sequence = dao.getNoteMainBySequence(cell[0].getContents());
						if (sequence != null) {
							String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
							dao.update(cell[0].getContents(), cell.length > 1 ? cell[1].getContents() : "");
							dao.csfile(cell[0].getContents(), loginUserID);
							sheetWritable.addCell(new Label(sheet.getRow(0).length, i, "成功"));	
						}else{
							sheetWritable.addCell(new Label(sheet.getRow(0).length, i, "失败,查无此数据"));	
						}
					}					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		book.close();
		bookWritable.write();
		bookWritable.close();
		return outFile;
	}
	
	/**
	 * 信函问题件代办列表
	 * 
	 * @param accessor
	 * @return List<PosProblemItemsDTO>
	 */
	public List<PosProblemItemsDTO> queryNoteReturnManage(String accessor) {
		return dao.queryNoteReturnManage(accessor);
	}

	/**
	 *根据条形码查询问题件
	 */
	public PosProblemItemsDTO queryBarcodeNoByID(String barcodeNo,String policyNo,String problemItemNo){
		PosProblemItemsDTO posProblemItemsDTO = dao
		.queryBarcodeNoByID(barcodeNo,policyNo,problemItemNo);
		return posProblemItemsDTO;
	}
	
	/**
	 * getPolicyNoList
	 * @throws ParseException 
	 */
	public Map<String,Object> getPolicyNoList(PosProblemItemsDTO posProblemItemsDTO) {
		Map<String, Object> result = new HashMap<String, Object>();
		Date applyDate = commonQueryDAO.getSystemDate(); //当前期日
		String sysDate = DateUtil.dateToString(applyDate, "yyyy-MM-dd");
		//从数据库获取PosApplyInfoDTO相关信息
		Map<String, Object> pMap = dao.getPolicyNoList(posProblemItemsDTO);
		//设置PosApplyInfoDTO的值
		PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();

		MapToBean(applyInfo, pMap);
		applyInfo.setApplyType("1");//默认为1
		applyInfo.setApplyDate(DateUtil.stringToDate(sysDate,"yyyy-MM-dd"));
		applyInfo.setServiceItems("19");
		applyInfo.setAcceptChannelCode("31");
		String barCode=generatNoteBarcode(applyInfo.getServiceItems()); //自动生成条形码
		applyInfo.setBarCode(barCode);
		ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
		ClientAddressDTO clientAddressDTO =new ClientAddressDTO(); // 客户地址明细DTO
		clientAddressDTO.setProvinceCode(posProblemItemsDTO.getProvinceCode()); //省代码
		clientAddressDTO.setCityCode(posProblemItemsDTO.getCityCode()); // 市代码
		clientAddressDTO.setAreaCode(posProblemItemsDTO.getAreaCode()); // 区代码
		clientAddressDTO.setDetailAddress(posProblemItemsDTO.getDetailAddress()); //详细地址
		clientAddressDTO.setPostalcode(posProblemItemsDTO.getPostalcode()); // 邮政编码
		sii = queryServiceItemsInfoExtra_19(applyInfo,clientAddressDTO);
		sii.setApplyDate(sysDate);
		sii.setAcceptDate(sysDate);
		applyInfo.setServiceItems("19");
		
		Map<String, Object> map = commonAcceptImpl.acceptInternal(sii, applyInfo);//调用受理
		//地址错误更改处理意见
		String posNo= sii.getPosNo();//保全号
		String dealType = posProblemItemsDTO.getProblemItemType();// 处理类型
		String dealOpinion = posProblemItemsDTO.getDealOpinion(); // 处理意见
		String detailSequenceNo = posProblemItemsDTO.getDetailSequenceNo(); //条形码
		dao.getDealType(detailSequenceNo,applyDate,dealType,posNo,dealOpinion);
		return result;
	}
	
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_19(PosApplyInfoDTO pai,ClientAddressDTO clientAddressDTO) {
		ServiceItemsInputDTO_19 items19 = new ServiceItemsInputDTO_19();
		items19.setPolicyNo(pai.getPolicyNo());
		items19.setServiceItems("19");
		items19.setApplyDate(DateUtil.dateToString(pai.getApplyDate(),"yyyy-MM-dd"));
		items19.setClientNo(pai.getClientNo());
		items19 = (ServiceItemsInputDTO_19) serviceItemsDAO_19.queryServiceItemsInfoExtraToReturnNote(items19);
		clientAddressDTO.setChecked("Y");
		items19.setAddress(clientAddressDTO);
		List<PolicyContactInfoDTO> policyContactList=new ArrayList<PolicyContactInfoDTO> ();
		List<PolicyContactInfoDTO> contactList = items19.getPolicyContactList();
		for (int i = 0; contactList != null && i < contactList.size(); i++) {
			PolicyContactInfoDTO policyContact = contactList.get(i);
			if (pai.getPolicyNo().equals(policyContact.getPolicyNo())){
				policyContact.setChecked("Y");
				//policyContact.setAddressSeq(clientAddressDTO.getAddressSeq()); //地址序号
				policyContact.setAddressDesc("3");	// 地址类型描述
				policyContact.setClient(pai.getClientNo()); // 客户号
				policyContact.setPolicyNo(pai.getPolicyNo()); // 保单号
				policyContact.setPostalCode(clientAddressDTO.getPostalcode()); // 邮政编码
				//......
			}
			policyContactList.add(policyContact);		
			}
		//
		ClientEmailDTO emailDTO =new ClientEmailDTO();
		emailDTO.setChecked("N");
		items19.setEmail(emailDTO);
		items19.setPolicyContactList(policyContactList);
		return items19;
	}
	
	/**
	 * @Description: 设置bean的值
	 * @methodName: setBean
	 * @param applyInfo
	 * @param sii
	 * @return void
	 * @author 
	 * @date 
	 * @throws
	 */
	public Object MapToBean(Object bean, Map data) {
		Method[] methods = bean.getClass().getDeclaredMethods();
		for(int i=0; i<methods.length; i++) {
			Method method = methods[i];
			try {
				if(method.getName().startsWith("set")) {
					String field = method.getName();
					field = field.substring(field.indexOf("set") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = data.get(field);
					if(value instanceof List) {
						List list = new ArrayList();
						List list1 = (List) value;
						for(int j=0; j<list1.size(); j++) {
							Map map = (Map) list1.get(j);
							Set s = map.keySet();
							for(Iterator iter = s.iterator();iter.hasNext();) {
								//获取map中的key
								String beanName = (String) iter.next();
								//实例化对象
								Object obj = Class.forName(beanName).newInstance();
								//获取该key对应的map
								Map data1 = (Map) map.get(beanName);
								//递归调用自己
								MapToBean(obj, data1);
								list.add(obj);
							}
						}
						method.invoke(bean, new Object[]{list});
					} else {
						method.invoke(bean, new Object[]{value});
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bean;
	}
	
	/**
	 * 生成NOTE用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	private String generatNoteBarcode(String serviceItems) {
		// 条形码统一改为sequence生成方式
		return "NOTE" + commonQueryDAO.queryBarcodeNoSequence();
	}
	
	/**
	 * 退信任务交接给新信函管理员
	 * 
	 * @param letterManager
	 * @param letterManagerNew
	 * @return Map<String,Object>
	 * @author zhangyi.wb
	 * @time 2016-6-30
	 */
	public String senderHandoverSubmit(String letterManager,
			String letterManagerNew) {
		dao.senderHandoverSubmit(letterManager, letterManagerNew);
		return "Y";
	}

	/**
	 * 检查新邢欢管理员是否有权限
	 * 
	 * @return Map<String,Object>
	 * @author zhangyi.wb
	 * @time 2016-6-30
	 */
	public String checkLetterManager(String letterManagerNew) {
		String checkPass = "N";
		Integer checkCount = dao.checkLetterManager(letterManagerNew);
		if (checkCount > 0) {
			checkPass = "Y";
		}
		return checkPass;
	}
}
