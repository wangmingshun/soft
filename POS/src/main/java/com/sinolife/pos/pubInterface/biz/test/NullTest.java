package com.sinolife.pos.pubInterface.biz.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
public class NullTest {
	
	 public static void main(String args[])
	 {
		
		 
		 ServiceItemsInputDTO_2 dto=new ServiceItemsInputDTO_2();
		 dto.setSurrenderToNewApplyBarCode(null);

		 System.out.println(StringUtils.isBlank(dto.getSurrenderToNewApplyBarCode()));
		 
		 System.out.println(StringUtils.isEmpty(dto.getSurrenderToNewApplyBarCode()));
		 
		
	 }

	 
	 
		public  static Object     MapToBean(Object javabean, Map data) {
			Method[] methods = javabean.getClass().getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				try {
					if (method.getName().startsWith("set")) {
						String field = method.getName();
						field = field.substring(field.indexOf("set") + 3);
						field = field.toLowerCase().charAt(0) + field.substring(1);
						Object value = data.get(field.toUpperCase());
						if (value instanceof List) {
							List list = new ArrayList();
							List list1 = (List) value;
							for (int j = 0; j < list1.size(); j++) {
								Map map = (Map) list1.get(j);
								Set s = map.keySet();
								for (Iterator iter = s.iterator(); iter.hasNext();) {
									String beanName = (String) iter.next();
									Object obj = Class.forName(beanName)
											.newInstance();
									Map dat = (Map) map.get(beanName);
									MapToBean(obj, dat);
									list.add(obj);
								}
							}
							method.invoke(javabean, new Object[] { list });
						} else {
							method.invoke(javabean,
									new Object[] { data.get(field.toUpperCase()) });
						}
					}
				} catch (Exception e) {
				}
			}
			return javabean;
		}
}
