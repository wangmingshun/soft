package com.sinolife.pos.pub.dao;

import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.pub.dto.UlinkPrice;


@Repository("ulinkPrice")
public class UlinkPriceDao extends SqlMapClientDaoSupport {
	
	public Map<String, Object> addUlinkPrice(Map<String, Object>  map){
          getSqlMapClientTemplate().insert(UlinkPriceDao.class.getName()+".addUlinkPrice",map);
		return map;
	}
	public Integer ulinkPricePageCount(UlinkPrice ulinkPrice) {
		return (Integer) getSqlMapClientTemplate().queryForObject(UlinkPriceDao.class.getName() + ".getUlinkPriceCount", ulinkPrice);
	}
	
	@SuppressWarnings("unchecked")
	public List<UlinkPrice> ulinkPricePageData(UlinkPrice ulinkPrice) {
		return getSqlMapClientTemplate().queryForList(UlinkPriceDao.class.getName() + ".getUlinkPriceList", ulinkPrice);
	}
	
}
