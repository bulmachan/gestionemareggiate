/**
 *  <p>Copyright © 2009-2010 Epoca srl</p>
 *
 * <p>This file is part of FatturazioneElettronica. FatturazioneElettronica is free 
 * software: you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version. Fatturazione 
 * Elettronica is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with 
 * FatturazioneElettronica. If not, see <http://www.gnu.org/licenses/>.</p>
 * 
 * 
 */
package it.epocaricerca.geologia.ejb.dao.jpa;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

public class NestedCriterion implements Criterion{

	String outerPropertyName;
	Criterion innerCriterion;

	
	public NestedCriterion(String outerPropertyName, Criterion innerCriterion) {
		super();
		this.outerPropertyName = outerPropertyName;
		this.innerCriterion = innerCriterion;
	}

	public String getOuterPropertyName() {
		return outerPropertyName;
	}

	public Criterion getInnerCriterion() {
		return innerCriterion;
	}

	public void setInnerCriterion(Criterion innerCriterion) {
		this.innerCriterion = innerCriterion;
	}

	public void setOuterPropertyName(String outerPropertyName) {
		this.outerPropertyName = outerPropertyName;
	}
	
	public TypedValue[] getTypedValues(Criteria criteria,
			CriteriaQuery criteriaQuery) throws HibernateException {
		// TODO Auto-generated method stub
		return new TypedValue[]{};
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		// TODO Auto-generated method stub
		return "";
	}

	
}
