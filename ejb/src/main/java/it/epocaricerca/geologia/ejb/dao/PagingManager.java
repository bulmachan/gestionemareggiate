package it.epocaricerca.geologia.ejb.dao;

import java.util.List;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public interface PagingManager {

	public <T>List<T> findByCriteria( Class<T> clazz,int startRow, int pageSize, List<Criterion> restrictions, List<Order> ordersBy);

	public int countByCriteria( Class clazz, List<Criterion> restrictions);
	
}
