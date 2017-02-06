package it.epocaricerca.geologia.web.controller.auth;

import it.epocaricerca.geologia.web.controller.UserSession;
import it.epocaricerca.geologia.web.util.JSFUtils;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

public class AccessControlPhaseListener implements PhaseListener{

	
	private static Logger logger = Logger.getLogger(AccessControlPhaseListener.class);
	
	
	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		String viewId = context.getViewRoot().getViewId();
		
		UserSession userSession = (UserSession)JSFUtils.getManagedBean("UserSession");
		
		if(userSession.getFirstName().equalsIgnoreCase("notauthorized")){
			logger.info("User  unauthorized to view "+viewId);
			redirectHome(context,viewId);
		}
		
		if(viewId.equalsIgnoreCase("/avvisoMeteoEventoCostieroAddForm.xhtml") && !(userSession.isRoleAvvisiMeteo() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /avvisoMeteoEventoCostieroAddForm.xhtml");
			redirectHome(context,"/avvisoMeteoEventoCostieroAddForm.xhtml");
		}
		
		if(viewId.equalsIgnoreCase("/condizioniMeteoMarineAddForm.xhtml") && !(userSession.isRoleCondizioneMeteo() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /condizioniMeteoMarineAddForm.xhtml");
			redirectHome(context,"/condizioniMeteoMarineAddForm.xhtml");
		}
		
		
		if(viewId.equalsIgnoreCase("/valutazioniImpattiAddForm.xhtml") && !(userSession.isRoleValutazioneImpatti() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /valutazioniImpattiAddForm.xhtml");
			redirectHome(context,"/valutazioniImpattiAddForm.xhtml");
		}
		
		if(viewId.equalsIgnoreCase("/impattiRealiAddForm.xhtml") && !(userSession.isRoleImpatti() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /impattiRealiAddForm.xhtml");
			redirectHome(context,"/impattiRealiAddForm.xhtml");
		}
		
		
		if(viewId.equalsIgnoreCase("/relazioneTecnicaSTBAddForm.xhtml") && !(userSession.isRoleRelazioniTecnicheSTB() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /relazioneTecnicaSTBAddForm.xhtml");
			redirectHome(context,"/relazioneTecnicaSTBAddForm.xhtml");
		}
		
		
		if(viewId.equalsIgnoreCase("/relazioniGeneraliSTBAddForm.xhtml") && !(userSession.isRoleRelazioniGeneraliSTB() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /relazioniGeneraliSTBAddForm.xhtml");
			redirectHome(context,"/relazioniGeneraliSTBAddForm.xhtml");
		}
		
		if(viewId.equalsIgnoreCase("/mareggiateAddForm.xhtml") && !(userSession.isRoleMareggiate() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /mareggiateAddForm.xhtml");
			redirectHome(context,"/mareggiateAddForm.xhtml");
		}
		
		if(viewId.equalsIgnoreCase("/analisi.xhtml") && !(userSession.isRoleMareggiate() || userSession.isRoleSuperUser()) )
		{
			logger.info("User "+userSession.getFirstName()+" unauthorized to view /analisi.xhtml");
			redirectHome(context,"/analisi.xhtml");
		}
		
	}

	public void beforePhase(PhaseEvent event) {
		// TODO Auto-generated method stub
		
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	
	
	private void redirectHome(FacesContext context, String from)
	{
		//addError(context, "access.loginrequired");
		context.getApplication().getNavigationHandler().handleNavigation(context, from, "notauthorized");
	}
}
