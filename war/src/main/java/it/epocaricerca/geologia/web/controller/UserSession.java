package it.epocaricerca.geologia.web.controller;

import java.util.ArrayList;
import java.util.List;

import it.epocaricerca.geologia.ejb.dao.UserRoleManager;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.auth.Role;
import it.epocaricerca.geologia.web.util.JSFUtils;

import javax.annotation.*;

import org.apache.log4j.Logger;

public class UserSession {

	private static Logger logger = Logger.getLogger(UserSession.class);

	public static String roleSuperUser = "super_admin";
	public static String roleAvvisiMeteo = "avvisiMeteo_admin";
	public static String roleValutazioneImpatti = "valutazioneImpatti_admin";
	public static String roleCondizioneMeteo = "condizioneMeteo_admin";
	public static String roleImpatti = "impatti_admin";
	public static String roleRelazioniTecnicheSTB = "relazioniTecnicheSTB_admin";
	public static String roleRelazioniGeneraliSTB = "relazioniGeneraliSTB_admin";
	public static String roleMareggiate = "mareggiate_admin";
	

	private String firstName;
	private String lastName;

	private List<Role> roles; 

	
	@PostConstruct
	public void initSessionData() 
	{
		UserRoleManager userRoleManager = null;
		try{
			userRoleManager = JNDIUtils.getUserRoleManager();
			String userId = JSFUtils.getRequestHeaderByKey("codicefiscale");

			firstName = JSFUtils.getRequestHeaderByKey("nome");
			lastName = JSFUtils.getRequestHeaderByKey("cognome");

			logger.info("initSessionData "+userId+" firstName "+firstName+" lastName "+lastName);

			roles = userRoleManager.getRolesFromUser(userId);
			if(roles == null){
				roles = new ArrayList<Role>();
				roles.add( userRoleManager.findRoleByName("reader") );
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			firstName = "notauthorized";

			roles = new ArrayList<Role>();
			//roles.add( userRoleManager.findRoleByName("reader") );
		}

	}

	public String getFirstName() {
		if(firstName == null) initSessionData();
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public boolean isRoleSuperUser()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleSuperUser))
				return true;
		}
		return false;
	}

	public boolean isRoleAvvisiMeteo()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleAvvisiMeteo))
				return true;
		}
		return false;
	}
	
	public boolean isRoleValutazioneImpatti()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleValutazioneImpatti))
				return true;
		}
		return false;
	}
	
	
	public boolean isRoleCondizioneMeteo()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleCondizioneMeteo))
				return true;
		}
		return false;
	}
	
	
	public boolean isRoleImpatti()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleImpatti))
				return true;
		}
		return false;
	}
	
	public boolean isRoleRelazioniTecnicheSTB()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleRelazioniTecnicheSTB))
				return true;
		}
		return false;
	}
	
	public boolean isRoleRelazioniGeneraliSTB()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleRelazioniGeneraliSTB))
				return true;
		}
		return false;
	}
	
	public boolean isRoleMareggiate()
	{
		if(firstName == null) initSessionData();
		
		for (Role role : roles) {
			if(role.getName().equalsIgnoreCase(roleMareggiate))
				return true;
		}
		return false;
	}


}
