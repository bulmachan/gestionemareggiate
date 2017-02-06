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
package it.epocaricerca.geologia.ejb.utils;


import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.EstensioneManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.LivelloCriticitaManager;
import it.epocaricerca.geologia.ejb.dao.LocalitaManager;
import it.epocaricerca.geologia.ejb.dao.MacroAreaManager;
import it.epocaricerca.geologia.ejb.dao.FenomenoManager;
import it.epocaricerca.geologia.ejb.dao.PagingManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneDannoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.STBManager;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.ejb.dao.TendenzaManager;
import it.epocaricerca.geologia.ejb.dao.AltezzaManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.TipoDannoManager;
import it.epocaricerca.geologia.ejb.dao.UserRoleManager;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author fulvio di marco, chiara pezzi, stefano monti
 * 
 * <p>Copyright © 2009-2010 Epoca srl</p>
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
 */
public class JNDIUtils {

	// Replace JNDI Lookup Code
	// private static String hostPort = SupportoMareggiatePropertiesUtil.getJndiHost()+":"+SupportoMareggiatePropertiesUtil.getJndiPort();
	// private static String hostPortIps = SupportoMareggiatePropertiesUtil.getJndiIp();

	//private static String applicationName = SupportoMareggiatePropertiesUtil.getApplicationName();
	private static String applicationName = "java:app/"+SupportoMareggiatePropertiesUtil.getApplicationName();

	private static final Map<String, Object> EJB_CACHE = new HashMap<String, Object>();
	
	private static PrevisioneMeteoManager previsioneMeteoManager = null;
	private static String PrevisioneMeteoManagerName = applicationName+"/PrevisioneMeteoManagerImpl/remote";

	private static ProvenienzaManager provenienzaManager = null;
	private static String ProvenienzaManagerName = applicationName+"/ProvenienzaManagerImpl/remote";

	private static MacroAreaManager macroAreaManager = null;
	private static String MacroAreaManagerName = applicationName+"/MacroAreaManagerImpl/remote";
	
	private static FenomenoManager fenomenoManager = null;
	private static String FenomenoManagerName = applicationName+"/FenomenoManagerImpl/remote";

	private static TendenzaManager tendenzaManager = null;
	private static String TendenzaManagerName = applicationName+"/TendenzaManagerImpl/remote";
	
	private static AltezzaManager altezzaManager = null;
	private static String AltezzaManagerName = applicationName+"/AltezzaManagerImpl/remote";

	private static TipoAllegatoManager tipoAllegatoManager = null;
	public static String TipoAllegatoManagerName = applicationName+"/TipoAllegatoManagerImpl/remote";

	private static PagingManager pagingManager = null;
	private static String PagingManagerName = applicationName+"/PagingManagerImpl/remote";

	private static PrevisioneImpattoManager previsioneImpattoManager = null;
	private static String PrevisioneImpattoManagerName = applicationName+"/PrevisioneImpattoManagerImpl/remote";
	
	private static ImpattoRealeManager impattoRealeManager = null;
	private static String ImpattoRealeManagerName = applicationName+"/ImpattoRealeManagerImpl/remote";

	private static TipoDannoManager tipoDannoManager = null;
	private static final String TipoDannoManagerName = applicationName+"/TipoDannoManagerImpl/remote";
	
	private static DannoManager dannoManager = null;
	public static final String DannoManagerName = applicationName+"/DannoManagerImpl/remote";

	private static LocalitaManager localitaManager = null;
	private static final String LocalitaManagerName = applicationName+"/LocalitaManagerImpl/remote";
	private static final String PrevisioneDannoManagerName = applicationName+"/PrevisioneDannoManagerImpl/remote";

	private static EstensioneManager estensioneManager = null;
	private static String EstensioneManagerName = applicationName+"/EstensioneManagerImpl/remote";
	
	private static STBManager stbManager = null;
	public static String STBManagerName = applicationName+"/STBManagerImpl/remote";
	
	private static FonteManager fonteManager = null;
	public static String FonteManagerName = applicationName+"/FonteManagerImpl/remote";
	
	private static RelazioneGeneraleSTBManager relazioneGeneraleSTBManager = null;
	private static String RelazioneGeneraleSTBManagerName = applicationName+"/RelazioneGeneraleSTBManagerImpl/remote";
	
	private static UserRoleManager userRoleManager = null;
	private static String userRoleManagerName = applicationName+"/UserRoleManagerImpl/remote";
	
	private static LivelloCriticitaManager livelloCriticitaManager = null;
	private static String livelloCriticitaManagerName =  applicationName+"/LivelloCriticitaManagerImpl/remote";
	
	public static String RelazioneSTBManagerName = applicationName+"/RelazioneSTBManagerImpl/remote";
	public static String StatoRelazioneManagerName = applicationName+"/StatoRelazioneManagerImpl/remote";
	public static String DannoSTBManagerName = applicationName+"/DannoSTBManagerImpl/remote";
	public static String MareggiataManagerName = applicationName+"/MareggiataManagerImpl/remote";
	public static String StazioneManagerName = applicationName+"/StazioneManagerImpl/remote";
	public static String ArpaDataSourceName = applicationName+"/ArpaDataSource/remote";
	public static String CondizioneMeteoManagerName = applicationName+"/CondizioneMeteoManagerImpl/remote";
	public static String RilevazioneManagerName = applicationName+"/RilevazioneManagerImpl/remote";
	public static String VariabileManagerName = applicationName+"/VariabileManagerImpl/remote";
	public static String AllegatoManagerName = applicationName+"/AllegatoManagerImpl/remote";
	public static String ImpattoRealeServiceName = applicationName+"/ImpattoRealeServiceImpl/remote";
	public static String ValutazioneImpattiServiceName = applicationName+"/ValutazioneImpattiServiceImpl/remote";
	public static String AvvisiMeteoEventiCostieriServiceName = applicationName+"/AvvisiMeteoEventiCostieriServiceImpl/remote";
	public static String MareggiataManagerServiceName = applicationName+"/MareggiateControllerServiceImpl/remote";
	public static String CondizioneMeteoManagerServiceName = applicationName+"/CondizioniMeteoMarineServiceImpl/remote";
	public static String RelazioniGeneraliSTBServiceName = applicationName+"/RelazioniGeneraliSTBServiceImpl/remote";
	public static String RelazioniTecnicheSTBServiceName = applicationName+"/RelazioniTecnicheSTBServiceImpl/remote";
	public static String LivelloCriticitaManagerName = applicationName+"/LivelloCriticitaManagerImpl/remote";
	public static String IndirizzoManagerName = applicationName+"/IndirizzoManagerImpl/remote";
	public static String AnalysisManagerName = applicationName+"/AnalysisManagerImpl/remote";
	
	
	
	
	
	private static Logger logger = Logger.getLogger(JNDIUtils.class.getName());
	private static DannoSTBManager dannoStbManager;
	private static PrevisioneDannoManager previsioneDannoManager;
	


	@SuppressWarnings("unchecked")
	private static InitialContext getInitialContext()
			throws Exception {
		//Hashtable props = getInitialContextProperties(hostPort);
		logger.info("Setup JNDI");
		
		// Hashtable props = getInitialContextProperties(hostPortIps);
		// Replace JNDI Lookup Code
		InitialContext context = new InitialContext();
		// return new InitialContext(props);
		return context; 
	}

	/*@SuppressWarnings("unchecked")
	private static Hashtable getInitialContextProperties(String hostPort) {
		Hashtable props = new Hashtable();
		props.put("java.naming.factory.initial",
				"org.jnp.interfaces.NamingContextFactory");
		props.put("java.naming.factory.url.pkgs",
				"org.jboss.naming:org.jnp.interfaces");
		props.put("java.naming.provider.url", hostPort);

		return props;
	}*/
	
	@SuppressWarnings("unchecked")
	public static <T> T retrieveEJB(String key, String name) {
		T ejbBean = null;
		
		if (EJB_CACHE.containsKey(key)) {
			ejbBean = (T) EJB_CACHE.get(key);
		} else {
			try {
				// InitialContext context = getInitialContext( hostPort );
				// Replace JNDI Lookup Code
				InitialContext context = getInitialContext();
				ejbBean = (T) context.lookup(name);
				EJB_CACHE.put(key, ejbBean);
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ejbBean;
	}
	
	
	public static UserRoleManager getUserRoleManager() {

		if (userRoleManager == null) {
			try {
				// userRoleManager = (UserRoleManager) getInitialContext(hostPort ).lookup(userRoleManagerName);
				// Replace JNDI Lookup Code
				userRoleManager = (UserRoleManager) getInitialContext().lookup(userRoleManagerName);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userRoleManager;
	}


	public static DannoSTBManager getDannoSTBManager() {

		if (dannoStbManager == null) {
			try {
				dannoStbManager = (DannoSTBManager) getInitialContext().lookup(DannoSTBManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dannoStbManager;
	}
	
	public static STBManager getSTBManager() {

		if (stbManager == null) {
			try {
				stbManager = (STBManager) getInitialContext().lookup(STBManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stbManager;
	}
	
	public static FonteManager getFonteManager() {

		if (fonteManager == null) {
			try {
				fonteManager = (FonteManager) getInitialContext().lookup(FonteManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fonteManager;
	}
	
	public static EstensioneManager getEstensioneManager() {

		if (estensioneManager == null) {
			try {
				estensioneManager = (EstensioneManager) getInitialContext( ).lookup(EstensioneManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return estensioneManager;
	}

	public static PrevisioneMeteoManager getPrevisioneMeteoManager() {

		if (previsioneMeteoManager == null) {
			try {
				previsioneMeteoManager = (PrevisioneMeteoManager) getInitialContext().lookup(PrevisioneMeteoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return previsioneMeteoManager;
	}

	public static ProvenienzaManager getProvenienzaManager() {

		if (provenienzaManager == null) {
			try {
				provenienzaManager = (ProvenienzaManager) getInitialContext().lookup(ProvenienzaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return provenienzaManager;
	}

	public static MacroAreaManager getMacroAreaManager() {

		if (macroAreaManager == null) {
			try {
				macroAreaManager = (MacroAreaManager) getInitialContext().lookup(MacroAreaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return macroAreaManager;
	}

	public static FenomenoManager getFenomenoManager() {

		if (fenomenoManager == null) {
			try {
				fenomenoManager = (FenomenoManager) getInitialContext().lookup(FenomenoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fenomenoManager;
	}
	
	public static TendenzaManager getTendenzaManager() {
		if (tendenzaManager == null) {
			try {
				tendenzaManager = (TendenzaManager) getInitialContext().lookup(TendenzaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tendenzaManager;
	}
	
	public static AltezzaManager getAltezzaManager() {
		if (altezzaManager == null) {
			try {
				altezzaManager = (AltezzaManager) getInitialContext().lookup(AltezzaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return altezzaManager;
	}


	public static TipoAllegatoManager getTipoAllegatoManager() {
		if (tipoAllegatoManager == null) {
			try {
				tipoAllegatoManager = (TipoAllegatoManager) getInitialContext( ).lookup(TipoAllegatoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tipoAllegatoManager;
	}

	public static PagingManager getPagingManager() {
		if (pagingManager == null) {
			try {
				pagingManager = (PagingManager) getInitialContext().lookup(PagingManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pagingManager;
	}

	
	public static PrevisioneDannoManager getPrevisioneDannoManager() {
		if (previsioneDannoManager == null) {
			try {
				previsioneDannoManager = (PrevisioneDannoManager) getInitialContext().lookup(PrevisioneDannoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return previsioneDannoManager;
	}

	
	public static PrevisioneImpattoManager getPrevisioneImpattoManager() {
		if (previsioneImpattoManager == null) {
			try {
				previsioneImpattoManager = (PrevisioneImpattoManager) getInitialContext().lookup(PrevisioneImpattoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return previsioneImpattoManager;
	}
	
	public static ImpattoRealeManager getImpattoRealeManager() {
		if (impattoRealeManager == null) {
			try {
				impattoRealeManager = (ImpattoRealeManager) getInitialContext().lookup(ImpattoRealeManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return impattoRealeManager;
	}

	public static TipoDannoManager getTipoDannoManager() {
		if (tipoDannoManager == null) {
			try {
				tipoDannoManager = (TipoDannoManager) getInitialContext( ).lookup(TipoDannoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tipoDannoManager;
	}
	
	public static DannoManager getDannoManager() {
		if (dannoManager == null) {
			try {
				dannoManager = (DannoManager) getInitialContext().lookup(DannoManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dannoManager;
	}

	public static LocalitaManager getLocalitaManager() {
		if (localitaManager == null) {
			try {
				localitaManager = (LocalitaManager) getInitialContext().lookup(LocalitaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return localitaManager;
	}
	
	
	public static RelazioneGeneraleSTBManager getRelazioneGeneraleSTBManager() {
		if (relazioneGeneraleSTBManager == null) {
			try {
				relazioneGeneraleSTBManager = (RelazioneGeneraleSTBManager) getInitialContext().lookup(RelazioneGeneraleSTBManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return relazioneGeneraleSTBManager;
	}

	public static LivelloCriticitaManager getLivelloCriticitaManager() {
		if (livelloCriticitaManager == null) {
			try {
				livelloCriticitaManager = (LivelloCriticitaManager) getInitialContext().lookup(livelloCriticitaManagerName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return livelloCriticitaManager;
	}

}

