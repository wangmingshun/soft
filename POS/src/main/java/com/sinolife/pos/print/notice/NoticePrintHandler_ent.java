package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.dto.PosInfoDTO;

/**
 * 批单
 */
@SuppressWarnings("rawtypes")
@Component("NoticePrintHandler_ent")
public class NoticePrintHandler_ent extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote)
			throws IOException {
		// Map map = PosUtils.getMapFromBean(posNote);

		String acceptChannelCode = (String) ((Map) posNote)
				.get("ACCEPT_CHANNEL_CODE");
		
		// 官网保全和淘宝渠道受理需进行电子印章打印
		if ("7".equals(acceptChannelCode) || "14".equals(acceptChannelCode) || "16".equals(acceptChannelCode) || "17".equals(acceptChannelCode)) {

			return this.templateStore.getTemplateStream("POS", "print",
					"endorsement_net");

        } else {
        	String entType ="";
        	if (((Map) posNote).get("ENTTYPE")!=null){
            	 entType=(String)((Map) posNote).get("ENTTYPE");
        	}
            if (((Map) posNote).get("APPROVAL_SERVICE_TYPE") != null
                    && ((Map) posNote).get("APPROVAL_SERVICE_TYPE").equals("1")) {

                /* 免填单非补退费类加条形码 edit by gaojiaming start */
                String serviceItems = (String) ((Map) posNote).get("SERVICE_ITEMS");
                String isApplicationFree = printDAO.checkIsApplicationFree((String) ((Map) posNote).get("POS_NO"));
				if ("Y".equals(isApplicationFree)){
						if ("19".equals(serviceItems) || "23".equals(serviceItems) || "21".equals(serviceItems)
								|| "18".equals(serviceItems) || "28".equals(serviceItems) || "7".equals(serviceItems)
								|| "26".equals(serviceItems) || "25".equals(serviceItems) || "24".equals(serviceItems)
								|| "30".equals(serviceItems) || "31".equals(serviceItems) || "32".equals(serviceItems)
								|| "29".equals(serviceItems) || "43".equals(serviceItems) || "44".equals(serviceItems)
								 || "45".equals(serviceItems) || "46".equals(serviceItems)) {
                            return this.templateStore.getTemplateStream("POS", "print", "endorsement_add_barcode");
				         }
                        else{
                          if ("logo".equals(entType)){
                        	  return this.templateStore.getTemplateStream("POS", "print", "endorsement_logo");
                          }else{
                        	  return this.templateStore.getTemplateStream("POS", "print", "endorsement");
                          }
                        }
				} else {
                    if ("logo".equals(entType)){
                  	  return this.templateStore.getTemplateStream("POS", "print", "endorsement_logo");
                    }else{
                  	  return this.templateStore.getTemplateStream("POS", "print", "endorsement");
                    }
                }
                // return this.templateStore.getTemplateStream("POS", "print",
                // "endorsement");
                /* 非补退费类加条形码 edit by gaojiaming end */

            } else {// 需邮件寄送
                return this.templateStore.getTemplateStream("POS", "print", "endorsement_mail");
            }
        }

	}

	@Override
	public String getFileName(String key) {
		if (StringUtils.isNotBlank(key)) {
			return key + "(批单).pdf";
		} else {
			return "批单-" + getTimeString() + ".pdf";
		}
	}

	@Override
	public Object queryPosNoteById(String id) {
		logger.info("queryEndorsementInfoByPosNo:" + id);
		return printService.queryEndorsementInfoByPosNo(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		return (Map<String, Object>) posNote;
	}
}
