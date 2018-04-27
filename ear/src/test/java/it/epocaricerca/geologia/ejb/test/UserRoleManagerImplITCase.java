package it.epocaricerca.geologia.ejb.test;

import java.util.List;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import it.epocaricerca.geologia.ejb.dao.UserRoleManager;
import it.epocaricerca.geologia.model.auth.Role;
import it.epocaricerca.geologia.model.auth.User;


public class UserRoleManagerImplITCase extends BaseTest{
	
	private UserRoleManager fixture;
	
	
	@Before
	public void setup() throws Exception {
		InitialContext ctx = new InitialContext();
		fixture = (UserRoleManager) ctx.lookup("SupportoMareggiate-ear/UserRoleManagerImpl/remote");
	}
	
	
	
	@Test
	public void testCRUD() throws Exception 
	{
		User user = super.createUser();
		long userId = fixture.createUser(user);
		assertTrue(userId > 0);
		User retrieved = fixture.findUserByUserId(user.getUserID());
		assertNotNull(retrieved);
		assertEquals(userId, retrieved.getId());
		
		
		retrieved = fixture.findUserById(retrieved.getId());
		assertNotNull(retrieved);
		assertEquals(userId, retrieved.getId());
		
		//ruolo 1
		
		Role roleAdmin = super.createRuoloAdmin();
		
		long roleId =  fixture.createRole(roleAdmin);
		
		Role roleRetrieved = fixture.findRoleById(roleId);
		
		assertEquals(roleId, roleRetrieved.getId());
		
		roleRetrieved = fixture.findRoleByName(roleAdmin.getName());
		
		assertEquals(roleId, roleRetrieved.getId());
		
		
		fixture.addUserRole(retrieved, roleRetrieved);
		
		retrieved = fixture.findUserByUserId(user.getUserID());
		assertNotNull(retrieved);
		List<Role> assignedRoles = fixture.getRolesFromUser(user.getUserID());
		assertEquals(1,assignedRoles.size());
		
		
		//ruolo 2
		Role roleSTB = super.createRuoloStb();
		
		long roleIdStb =  fixture.createRole(roleSTB);
		
		Role roleRetrievedStb = fixture.findRoleById(roleIdStb);
		
		assertEquals(roleIdStb, roleRetrievedStb.getId());
		
		roleRetrieved = fixture.findRoleByName(roleSTB.getName());
		
		assertEquals(roleIdStb, roleRetrievedStb.getId());
		
		
		fixture.addUserRole(retrieved, roleRetrieved);
		assignedRoles = fixture.getRolesFromUser(user.getUserID());
		assertEquals(2,assignedRoles.size());
		
		
		fixture.deleteUser(userId);
		fixture.deleteRole(roleId);
		fixture.deleteRole(roleIdStb);
		
		
	}
	
	


}
