package it.epocaricerca.geologia.ejb.dao.jpa;



import java.util.List;

import it.epocaricerca.geologia.ejb.dao.UserRoleManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.auth.Role;
import it.epocaricerca.geologia.model.auth.User;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;



@Stateless  
@Remote
public class UserRoleManagerImpl extends GenericManager implements UserRoleManager {



	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em; 


	public long createUser(User user) {

		User u = this.findUserByUserId(user.getUserID());


		if ( u == null ) {
			em.persist(user);
			return user.getId();
		}
		else 
			return new Long(-1);

	}



	public User findUserById(Long id) {

		return super.findItemById(User.class, em, id);
	}


	public void updateUser(User user) {
		user = em.merge(user);		  
	}



	public long deleteUser(Long id) throws EntityNotFoundException {

		User user = this.findUserById(id);

		if (user == null)
			throw new EntityNotFoundException("User "+id);

		List<Role> roles = user.getRoles();
		for (Role role : roles) {
			this.removeUserRole(user,role);
		}
		
		em.remove(user);

		return id;
	}




	public User findUserByUserId(String parEmail)  {

		String queryString = "SELECT u FROM User AS u WHERE u.userID = ?1";
		Query query = em.createQuery(queryString);
		query.setParameter(1, parEmail);
		List<User> users =  query.getResultList();
		if(users != null && users.size() == 1)
			return users.get(0);
		return null;
	}




	/**
	 * Set Role of User.
	 * @throws EntityNotFoundException 
	 */
	public void addUserRole(User user, Role role) throws EntityNotFoundException {

		User u = this.findUserById(user.getId());
		Role r = this.findRoleById(role.getId());

		if (u == null || r == null)
			throw new EntityNotFoundException("User "+u.getId() +" Role "+role.getId());


		u.getRoles().add(r);
		em.merge(r);

		r.getUsers().add(u);
		em.merge(u);	


	}


	/**
	 * Set Role of User.
	 * @throws EntityNotFoundException 
	 */
	public Long removeUserRole(User user, Role role) throws EntityNotFoundException {

		User u = this.findUserById(user.getId());
		Role r = this.findRoleById(role.getId());

		if (u == null || r == null)
			throw new EntityNotFoundException("User "+u.getId() +" Role "+role.getId());


		boolean removed = u.getRoles().remove(r);

		if(removed){
			r.getUsers().remove(u);
			em.merge(u);
			em.merge(u);
			return role.getId();
		}
		
		return -1L;


	}

	
	public List<Role> getRolesFromUser(String userId) throws EntityNotFoundException {
		if(this.findUserByUserId(userId) == null)
			throw new EntityNotFoundException("User "+userId);

		Query q = em.createQuery("SELECT m.roles FROM User m WHERE m.userID = ?1");
		q.setParameter(1, userId);
		return q.getResultList();
	}





	/**
	 * CREATE Role
	 * @return 	id of Role if created
	 * 			id of Role if already exists
	 */
	public long createRole(Role role) {

		Role existingRole = this.findRoleByName(role.getName());

		if (existingRole == null) {

			em.persist(role);		
			return role.getId();
		}
		else
			return existingRole.getId();
	}


	public Role findRoleByName(String name) {

		String queryString = "SELECT u FROM Role AS u WHERE u.name = ?1";
		Query query = em.createQuery(queryString);
		query.setParameter(1, name);

		List<Role> roles =  query.getResultList();
		if(roles != null && roles.size() == 1)
			return roles.get(0);
		return null;
	}

	public Role findRoleById(Long idRole) {

		return super.findItemById(Role.class, em, idRole);
	}





	public long deleteRole(Long id) throws EntityNotFoundException {

		Role role = this.findRoleById(id);


		if (role == null)
			throw new EntityNotFoundException("Role"+id);

		List<User> users = role.getUsers();


		for (User user : users) {
			this.removeUserRole(user, role);
		}
		em.remove(role);
		return id;
	}



}



