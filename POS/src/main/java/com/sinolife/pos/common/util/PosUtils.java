package com.sinolife.pos.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sf.cglib.beans.BeanMap;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 乱七八糟的工具集合
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PosUtils {

	private static Logger logger = Logger.getLogger(PosUtils.class);
	
	/**
	 * 取得当前登录用户信息
	 * @return LoginUserInfoDTO 当前登录用户信息
	 * @see com.sinolife.pos.common.dto.LoginUserInfoDTO
	 */
	public static LoginUserInfoDTO getLoginUserInfo() {
		HttpServletRequest request = PlatformContext.getHttpServletRequest();
		if(request != null) {
			return (LoginUserInfoDTO)request.getSession().getAttribute(SessionKeys.LOGIN_USER_INFO);
		} else {
			return null;
		}
		
	}
	
	/**
	 * 使用JSON格式返回对象内容字符串
	 * 
	 * @param bean
	 * @return
	 */
	public static String describBeanAsJSON(Object bean) {
		try {
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
				@Override
				public Object processArrayValue(Object obj, JsonConfig jsonconfig) {
					return getString(obj);
				}
				@Override
				public Object processObjectValue(String s, Object obj,
						JsonConfig jsonconfig) {
					return getString(obj);
				}
				private Object getString(Object obj) {
					if (obj instanceof Date) {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return JSONObject.fromObject("{\"formatted\":\"" + df.format((Date)obj) + "\"}");
					}
					return new JSONObject(true);
				}
			});
			JSON json = JSONSerializer.toJSON(bean, config);
			return json.toString(4, 2);
		}catch(Exception e) {
			e.printStackTrace();
			return bean == null ? "" : bean.toString();
		}
	}

	/**
	 * 将对象转换成为Map，不嵌套转换属性值
	 * 
	 * @param bean
	 * @return
	 */
	public static Map getMapFromBean(Object bean) {
		try {
			if (bean == null)
				return null;

			Map retMap = new HashMap();
			BeanMap bm = BeanMap.create(bean);
			retMap.putAll(bm);
			return retMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对象深拷贝方法
	 * @param obj 被拷贝的对象
	 * @return 传入对象的深拷贝
	 */
	public static Object deepCopy(Object obj) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			/* 将该对象序列化成流,因为写在流里的是对象的一个拷贝，
			 * 而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝 */
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			// 将流序列化成对象
			bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			return ois.readObject();
		}catch(Exception e) {
			throw new RuntimeException("deepCopy bean failed", e);
		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(oos != null) {
				try {
					oos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(ois != null) {
				try {
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从CodeTable中，根据传入的代码查询对应的描述信息
	 * @param codeTableName
	 * @param code
	 * @return 
	 * @See com.sinolife.pos.common.consts.CodeTableNames
	 */
	public static String getDescByCodeFromCodeTable(String codeTableName, String code) {
		PosCodeTableBean posCodeTableBean = PlatformContext.getApplicationContext().getBean(PosCodeTableBean.class);
		List<CodeTableItemDTO> codeTableList = (List<CodeTableItemDTO>) posCodeTableBean.getPosTableMap().get(codeTableName);
		for(CodeTableItemDTO cti : codeTableList) {
			if(cti.getCode().equals(code)) {
				return cti.getDescription();
			}
		} 
		return null;
	}
	
	public PosUtils() {}

	/**
	 * 从集合中根据元素的某个属性查找对象
	 * @param collection
	 * @param propertyName
	 * @param propertyValue
	 * @return 第一个查找到的元素
	 */
	public static Object findInCollectionByProperty(Collection<?> collection, String propertyName, Object propertyValue) {
		if(collection != null && !collection.isEmpty() && StringUtils.isNotBlank(propertyName)) {
			Iterator<?> it = collection.iterator();
			while(it.hasNext()) {
				Object obj = it.next();
				Object value = null;
				if(obj instanceof Map) {
					value = ((Map)obj).get(propertyName);
				} else {
					BeanWrapper bw = new BeanWrapperImpl(obj);
					value = bw.getPropertyValue(propertyName);
				}
				if(propertyValue == null && value == null || propertyValue != null && propertyValue.equals(value)) {
					return obj;
				}
			}
		}
		return null;
	}
	
	/**
	 * 给Map设定默认值，如果给定的key已经存在，则不设置
	 * @param map
	 * @param key
	 * @param value
	 */
	public static void setDefaultValue(Map<String, Object> map, String key, Object value) {
		if(!map.containsKey(key)) {
			map.put(key, value);
		}
	}
	
	/**
	 * 转换字符串
	 * @param obj
	 * @return
	 */
	public static String transToString(Object obj) {
		if(obj == null) {
			return null;
		} else if(obj instanceof Date) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(obj);
		}
		return String.valueOf(obj);
	}
	
	/**
	 * 将空字符串转化为null
	 * @param obj
	 * @return
	 */
	public static Object trimToNullIfNecessary(Object obj) {
		if(obj == null) {
			return null;
		}
		if(obj instanceof String) {
			String str = (String)obj;
			if(StringUtils.isBlank(str)) {
				return null;
			}
			return str.trim();
		}
		return obj;
	}
	
	/**
	 * 判断传入对象是否为空，字符串判断是否为空串，集合判断是否不存在元素，其他直接判断是否为null
	 * @param obj
	 * @return
	 */
	public static boolean isNullOrEmpty(Object obj) {
		if(obj == null)
			return true;
		if(obj instanceof String)
			return StringUtils.isBlank((String) obj);
		if(obj instanceof Collection)
			return ((Collection)obj).isEmpty();
		if(obj instanceof Map)
			return ((Map)obj).isEmpty();
		if(obj.getClass().isArray())
			return ((Object[])obj).length == 0;
		return false;
	}
	
	/**
	 * 判断传入对象是否不为空，字符串判断是否不为空串，集合判断是否存在元素，其他直接判断是否不为null
	 * @param obj
	 * @return
	 */
	public static boolean isNotNullOrEmpty(Object obj) {
		return !isNullOrEmpty(obj);
	}
	
	/**
	 * 判断两个对象（一般用于字符串）是否相等
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean isTheSame(Object obj1,Object obj2){
		Object o1 = trimToNullIfNecessary(obj1);
		Object o2 = trimToNullIfNecessary(obj2);
		
		return o1 == null && o2 == null || o1 != null && o2 != null && o1.equals(o2);
	}
	
	/**
	 * 按给定的模式格式化日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		if(date == null)
			return null;
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}
	
	/**
	 * 得到某环境变量的值
	 * @param name
	 * @return
	 */
	public static String getPosProperty(String name){
		try {
			return BeanUtils.getProperty(PlatformContext.getApplicationContext().getBean("posProperties"), name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 用Null来扩展List为指定的大小
	 * @param list
	 * @param size
	 */
	public static void appendNullToSize(List<?> list, int size) {
		if(list == null || list.size() >= size)
			return;
		for(int i = list.size(); i < size; i++) {
			list.add(null);
		}
	}
	
	/**
	 * 从条码中截取出影像的文档类型，不支持次品单条码<br/>
	 * 申请书条码:4位资料类型条码+2位版本号+9位业务流水号+2位校验码<br/>
	 * 次品单条码:CP+DOC_ID(文档ID)+1位校验码<br/>
	 * 问题函条码:4位资料类型条码+2位版本号+9位业务流水号+2位校验码
	 * @param barcodeNo
	 * @return
	 */
	public static String getDocTypeFromBarcodeNo(String barcodeNo) {
		if(StringUtils.isBlank(barcodeNo) || barcodeNo.length() < 4)
			return null;
		return barcodeNo.substring(0, 4);
	}
	
	/**
	 * 重新组装用户输入的用户名
	 * 邮箱@后缀输或不输或输错，都强制替换成正确的
	 * @param userId
	 * @return
	 */
	public static String parseInputUser(String userId){
		if(StringUtils.isNotBlank(userId)){
			userId = userId.toLowerCase();
			if(userId.indexOf("@")>0){
				userId = userId.substring(0,userId.indexOf("@"));			
			}
			if(userId.startsWith("pos")){
				userId += "@sldev.com";//非生产环境的or捏造的用户， 的域。如果生产环境也需要捏造用户，请用"pos."或其他标志开头
				
			}else{
				userId += "@sino-life.com";
			}			
		}
		return userId;
	}
	
	/**
	 * 根据生日和计算日期计算年龄(年数)
	 * @param birthday
	 * @param calcDate
	 * @return
	 */
	public static int calcAgeYearsFromBirthday(Date birthday, Date calcDate) {
		if(birthday == null)
			return 0;
		
		Calendar calCalc = Calendar.getInstance();
		Calendar calBirth = Calendar.getInstance();
		calCalc.setTime(calcDate);
		calBirth.setTime(birthday);
		
		if(calBirth.compareTo(calCalc) >= 0)
			return 0;
		
		int sysYear = calCalc.get(Calendar.YEAR);
		int birthYear = calBirth.get(Calendar.YEAR);
		int yearsBetween = sysYear - birthYear;
		
		calCalc.add(Calendar.YEAR, yearsBetween * -1);
		if(calCalc.compareTo(calBirth) >= 0)
			return yearsBetween;
		
		return yearsBetween - 1;
	}
	
	/**
	 * 根据生日和计算日期计算年龄(月份)
	 * @param birthday
	 * @param calcDate
	 * @return
	 */
	public static int calcAgeMonthsFromBirthday(Date birthday, Date calcDate) {
		if(birthday == null)
			return 0;
		
		Calendar calCalc = Calendar.getInstance();
		Calendar calBirth = Calendar.getInstance();
		calCalc.setTime(calcDate);
		calBirth.setTime(birthday);
		
		int sysYear = calCalc.get(Calendar.YEAR);
		int birthYear = calBirth.get(Calendar.YEAR);
		int sysMonth = calCalc.get(Calendar.MONTH);
		int birthMonth = calBirth.get(Calendar.MONTH);
		int monthsBetween = (sysYear - birthYear) * 12 + (sysMonth - birthMonth);
		System.out.println("monthsBetween:" + monthsBetween);
		calBirth.add(Calendar.MONTH, monthsBetween);
		if(calBirth.compareTo(calCalc) <= 0)
			return monthsBetween;
		
		return monthsBetween - 1;
	}
	
	/**
	 * 根据生日和计算日期计算年龄(天)
	 * @param birthday
	 * @param calcDate
	 * @return
	 */
	public static int calcAgeDaysFromBirthday(Date birthday, Date calcDate) {
		if(birthday == null || calcDate == null)
			return 0;
		
		if(birthday.compareTo(calcDate) >= 0)
			return 0;
		
		return (int) ((calcDate.getTime() - birthday.getTime())/(1000*60*60*24));
	}
	
	/**
	 * 吃掉异常，只记录
	 * @param e
	 */
	public static void muteException(Exception e) {
		logger.error(e.getMessage(), e);
	}
	
	/**
	 * 安全关闭inputstream
	 * @param is
	 */
	public static void safeCloseInputStream(InputStream is) {
		if(is != null) {
			try {
				is.close();
			} catch (IOException e) {
				muteException(e);
			}
		}
	}
	
	/**
	 * 安全关闭ouputstream
	 * @param is
	 */
	public static void safeCloseOuputStream(OutputStream os) {
		if(os != null) {
			try {
				os.close();
			} catch (IOException e) {
				muteException(e);
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param files
	 */
	public static void deleteFiles(List<File> files) {
		if(files != null && !files.isEmpty()) {
			for(File file : files) {
				deleteFile(file);
			}
		}
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	public static void deleteFile(File file) {
		if(file != null && file.exists()) {
			file.delete();
		}
	}
	/**生成指定位数的 字母和数字的组合随机码*/
	public String  randomCode(int length){

		String val = "";

		Random random = new Random();
		for(int i = 0; i < length; i++)
		{
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字      

		if("char".equalsIgnoreCase(charOrNum)) // 字符串      
		{
			int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母      
			val += (char) (choice + random.nextInt(26));
		}
		else if("num".equalsIgnoreCase(charOrNum)) // 数字  
		{
			val += String.valueOf(random.nextInt(10));
		}
		}

		return val;
	}
	
	/** 
	   * 根据IP地址获取mac地址 
	   * @param ipAddress 127.0.0.1 
	   * @return 
	   * @throws SocketException 
	   * @throws UnknownHostException 
	   */
	  public static String getLocalMac(String ipAddress) throws SocketException, 
	      UnknownHostException { 
	    // TODO Auto-generated method stub 
	    String str = ""; 
	    String macAddress = ""; 
	    final String LOOPBACK_ADDRESS = "127.0.0.1"; 
	    // 如果为127.0.0.1,则获取本地MAC地址。 
	    if (LOOPBACK_ADDRESS.equals(ipAddress)) { 
	      InetAddress inetAddress = InetAddress.getLocalHost(); 
	      // 貌似此方法需要JDK1.6。 
	      byte[] mac = NetworkInterface.getByInetAddress(inetAddress) 
	          .getHardwareAddress(); 
	      // 下面代码是把mac地址拼装成String 
	      StringBuilder sb = new StringBuilder(); 
	      for (int i = 0; i < mac.length; i++) { 
	        if (i != 0) { 
	          sb.append("-"); 
	        } 
	        // mac[i] & 0xFF 是为了把byte转化为正整数 
	        String s = Integer.toHexString(mac[i] & 0xFF); 
	        sb.append(s.length() == 1 ? 0 + s : s); 
	      } 
	      // 把字符串所有小写字母改为大写成为正规的mac地址并返回 
	      macAddress = sb.toString().trim().toUpperCase(); 
	      return macAddress; 
	    } else { 
	      // 获取非本地IP的MAC地址 
	      try { 
	        System.out.println(ipAddress); 
	        Process p = Runtime.getRuntime() 
	            .exec("nbtstat -A " + ipAddress); 
	        System.out.println("===process=="+p); 
	        InputStreamReader ir = new InputStreamReader(p.getInputStream()); 
	           
	        BufferedReader br = new BufferedReader(ir); 
	         
	        while ((str = br.readLine()) != null) { 
	          if(str.indexOf("MAC")>1){ 
	            macAddress = str.substring(str.indexOf("MAC")+9, str.length()); 
	            macAddress = macAddress.trim(); 
	            System.out.println("macAddress:" + macAddress); 
	            break; 
	          } 
	        } 
	        p.destroy(); 
	        br.close(); 
	        ir.close(); 
	      } catch (IOException ex) { 
	      } 
	      return macAddress; 
	    } 
	  }
}
