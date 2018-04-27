package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.auth.Role;
import it.epocaricerca.geologia.model.auth.User;


public interface UserRoleManager {


	//User functions
	long createUser(User user);
	User findUserById(Long id);	
	User findUserByUserId(String userId);	
	void updateUser(User user);	
	long deleteUser(Long id) throws EntityNotFoundException;	

	// Role functions
	long createRole(Role role);
	long deleteRole(Long id) throws EntityNotFoundException;
	Role findRoleById(Long idRole);
	Role findRoleByName(String roleName);

	
	//USER Role 
	void addUserRole(User user, Role role) throws EntityNotFoundException;
	Long removeUserRole(User user, Role role) throws EntityNotFoundException;
	List<Role> getRolesFromUser(String userId) throws EntityNotFoundException;

}
