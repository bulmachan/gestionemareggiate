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

import org.hibernate.criterion.Order;

public class NestedOrder extends Order{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String outerPropertyName;
	public String getOuterPropertyName() {
		return outerPropertyName;
	}

	public void setOuterPropertyName(String outerPropertyName) {
		this.outerPropertyName = outerPropertyName;
	}

	public String getInnerPropertyName() {
		return innerPropertyName;
	}

	public void setInnerPropertyName(String innerPropertyName) {
		this.innerPropertyName = innerPropertyName;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	String innerPropertyName;
	boolean ascending;
	
	public NestedOrder(String outerPropertyName, String innerPropertyName, boolean ascending) {
		super(innerPropertyName, ascending);
		this.outerPropertyName = outerPropertyName;
		this.innerPropertyName=innerPropertyName;
		this.ascending = ascending;
	}

	
}
