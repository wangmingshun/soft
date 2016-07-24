package com.sinolife.pos.pub.web;

import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateJsonValueProcessor implements JsonValueProcessor {
	 private String dateFormat = "yyyy-MM-dd";

	    @Override
	    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
	        return null;
	    }

	    @Override
	    public Object processObjectValue(String key, Object value,
	            JsonConfig jsonConfig) {
	        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	        Object rs=null;
	        if(value!=null)
	        rs=	sdf.format(value);
	        return rs;
	    }

	    public String getDateFormat() {
	        return dateFormat;
	    }

	    public void setDateFormat(String dateFormat) {
	        this.dateFormat = dateFormat;
	    }
}
