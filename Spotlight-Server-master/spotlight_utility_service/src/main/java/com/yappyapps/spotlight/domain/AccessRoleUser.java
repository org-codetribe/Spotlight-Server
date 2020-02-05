package com.yappyapps.spotlight.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * The AccessRoleUser class is the domain class which maps the AccessRoleUser
 * java object to access_role_user table.
 * 
 * <h1>@Entity</h1> will enable it to map to database table.
 * 
 * <h1>@Table</h1> provides the database table name
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Entity
@Table(name = "ACCESS_ROLE_USER")
@AssociationOverrides({
	 @AssociationOverride(name="pk.accessRole", joinColumns = @JoinColumn(name="access_role_id")),
	 @AssociationOverride(name="pk.spotlightUser", joinColumns = @JoinColumn(name="spotlight_user_id"))
	})
public class AccessRoleUser implements Serializable {

	@Id
	private AccessRoleUserPK pk = new AccessRoleUserPK();

	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	 @Override
	 public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + ((pk == null) ? 0 : pk.hashCode());
	  return result;
	 }
	 
	 @Override
	 public boolean equals(Object obj) {
	  if (this == obj)
	   return true;
	  if (obj == null)
	   return false;
	  if (getClass() != obj.getClass())
	   return false;
	  AccessRoleUser other = (AccessRoleUser) obj;
	  if (pk == null) {
	   if (other.pk != null)
	    return false;
	  } else if (!pk.equals(other.pk))
	   return false;
	  return true;
	 }
	 
	 public AccessRoleUserPK getPk() {
	  return pk;
	 }
	 
	 public void setPk(AccessRoleUserPK pk) {
	  this.pk = pk;
	 }
	 
	/**
	 * @return the createdOn
	 */
	public Timestamp getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	
	@Embeddable
	class AccessRoleUserPK implements Serializable {
	 @ManyToOne
	 private AccessRole accessRole;
	  
	 @ManyToOne
	 private SpotlightUser spotlightUser;
	 
	 @Override
	 public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result
	    + ((spotlightUser == null) ? 0 : spotlightUser.hashCode());
	  result = prime * result + ((accessRole == null) ? 0 : accessRole.hashCode());
	  return result;
	 }
	 
	 @Override
	 public boolean equals(Object obj) {
	  if (this == obj)
	   return true;
	  if (obj == null)
	   return false;
	  if (getClass() != obj.getClass())
	   return false;
	  AccessRoleUserPK other = (AccessRoleUserPK) obj;
	  if (spotlightUser == null) {
	   if (other.spotlightUser != null)
	    return false;
	  } else if (!spotlightUser.equals(other.spotlightUser))
	   return false;
	  if (accessRole == null) {
	   if (other.accessRole != null)
	    return false;
	  } else if (!accessRole.equals(other.accessRole))
	   return false;
	  return true;
	 }
	  
	 public AccessRole getAccessRole() {
	  return accessRole;
	 }
	 
	 public void setAccessRole(AccessRole accessRole) {
	  this.accessRole = accessRole;
	 }
	 
	 public SpotlightUser getSpotlightUser() {
	  return spotlightUser;
	 }
	 
	 public void setSpotlightUser(SpotlightUser spotlightUser) {
	  this.spotlightUser = spotlightUser;
	 }
	}

}
