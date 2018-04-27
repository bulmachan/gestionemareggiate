package it.epocaricerca.geologia.ejb.dao.jpa;

import it.epocaricerca.geologia.ejb.dao.PagingManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

@Stateless
@Remote
public class PagingManagerImpl implements PagingManager{


	protected static Logger logger = Logger.getLogger(PagingManager.class.getName());


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	public <T>List<T> findByCriteria(Class<T> clazz,int startRow, int pageSize, List<Criterion> restrictions, List<Order> ordersBy) {
		try {
			Session session = (Session)em.getDelegate();
			Criteria idsOnlyCriteria = session.createCriteria(clazz);
			idsOnlyCriteria.setMaxResults(pageSize);
			idsOnlyCriteria.setFirstResult(startRow);
			if (restrictions != null)
				for (Criterion c : restrictions)
					configureCriterion(c,idsOnlyCriteria);

			configureOrder(idsOnlyCriteria,ordersBy);
			
			ProjectionList projection = Projections.projectionList();
			
			configureProjection(projection, ordersBy);
			idsOnlyCriteria.setProjection(	Projections.distinct (projection));
			
			
			Collection<Object> objects = idsOnlyCriteria.list();
			
			if(objects == null || objects.size() == 0)
				return new ArrayList();
			
			Collection<Long> ids = getId(objects);
			
			Criteria criteria = session.createCriteria(clazz);
			criteria.add(Restrictions.in("id", ids));
			
			configureOrder(criteria,ordersBy);
			
	
			return criteria.list();	
		} catch (Exception e) {
			logger.warn("Error calling CrudManagerBean.findByCriteria()", e);
		}
		return new ArrayList();
	}
	
	public Collection<Long> getId(Collection<Object> objects)
	{
		
		List<Long> ids = new ArrayList<Long>();
		for (Object object : objects) {
			if(object instanceof Long)
				ids.add((Long) object);
			else
			{
				ids.add((Long)((Object[])object)[0]);
			}
		}
		return ids;
	}

	public int countByCriteria(Class clazz, List<Criterion> restrictions) {
		try {
			org.hibernate.Session session = (Session)em.getDelegate();
			Criteria criteria = session.createCriteria(clazz);

			if (restrictions != null)
				for (Criterion c : restrictions)
					configureCriterion(c,criteria);

			criteria.setProjection( Projections.distinct(Projections.id()));
			if (criteria.list() == null || criteria.list().size() == 0)
				return 0;
			else 
				return (Integer)criteria.list().size();

		} catch (Exception e) {
			logger.warn("Error calling CrudManagerBean.countByCriteria()", e);
		}
		return 0;
	}


	protected void configureProjection(ProjectionList projectionList, List<Order> ordersBy)
	{
		projectionList.add(Projections.id());
		if (ordersBy != null){
			for (Order orderBy : ordersBy){
				if (orderBy instanceof NestedOrder){
					
				}
				else
					projectionList.add(Projections.property(orderBy.toString().split(" ")[0]));
			}
		}
	}
	
	protected void configureOrder(Criteria criteria, List<Order> ordersBy)
	{
		if (ordersBy != null){
			for (Order orderBy : ordersBy){
				if (orderBy instanceof NestedOrder){
					String outerProp = ((NestedOrder)orderBy).getOuterPropertyName();
					String innerProp = ((NestedOrder)orderBy).getInnerPropertyName();
					boolean asc = ((NestedOrder)orderBy).isAscending();
					criteria.createCriteria(outerProp).addOrder( (asc) ? Order.asc(innerProp) : Order.desc(innerProp));
				}
				else
					criteria.addOrder(orderBy);
			}
		}
	}

	
	protected void configureCriterion(Criterion c,Criteria criteria)
	{
		if (c instanceof NestedCriterion){
			String outerProp = ((NestedCriterion)c).getOuterPropertyName();
			Criterion inner = ((NestedCriterion)c).getInnerCriterion();

			configureCriterion(inner,criteria.createCriteria(outerProp));
		}
		else
			criteria.add(c);
	}
}
