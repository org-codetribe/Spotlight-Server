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



@Entity
@Table(name = "GENRE_BROADCASTER")
@AssociationOverrides({
	 @AssociationOverride(name="pk.genre", joinColumns = @JoinColumn(name="genre_id")),
	 @AssociationOverride(name="pk.broadcasterInfo", joinColumns = @JoinColumn(name="broadcaster_info_id"))
	})
public class GenreBroadcaster implements Serializable {
	@Id
	private GenreBroadcasterPK pk = new GenreBroadcasterPK();

//	@ManyToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "GENRE_ID")
//	private Genre genre = new Genre();
//
//	@ManyToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "BROADCASTER_INFO_ID")
//	private BroadcasterInfo broadcasterInfo = new BroadcasterInfo();

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
	  GenreBroadcaster other = (GenreBroadcaster) obj;
	  if (pk == null) {
	   if (other.pk != null)
	    return false;
	  } else if (!pk.equals(other.pk))
	   return false;
	  return true;
	 }
	 
	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

//	public GenreBroadcaster(Integer id, Genre genre, BroadcasterInfo broadcasterInfo, Timestamp createdOn,
//			String status) {
//		super();
//		this.id = id;
//		this.genre = genre;
//		this.broadcasterInfo = broadcasterInfo;
//		this.createdOn = createdOn;
//		this.status = status;
//	}
//
//	public GenreBroadcaster() {
//	}
//
//	/**
//	 * @return the id
//	 */
//	public Integer getId() {
//		return id;
//	}
//
//	/**
//	 * @param id
//	 *            the id to set
//	 */
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	/**
//	 * @return the genre
//	 */
//	public Genre getGenre() {
//		return genre;
//	}
//
//	/**
//	 * @param genre
//	 *            the genre to set
//	 */
//	public void setGenre(Genre genre) {
//		this.genre = genre;
//	}
//
//	/**
//	 * @return the broadcasterInfo
//	 */
//	public BroadcasterInfo getBroadcasterInfo() {
//		return broadcasterInfo;
//	}
//
//	/**
//	 * @param broadcasterInfo
//	 *            the broadcasterInfo to set
//	 */
//	public void setBroadcasterInfo(BroadcasterInfo broadcasterInfo) {
//		this.broadcasterInfo = broadcasterInfo;
//	}

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

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	@Embeddable
	class GenreBroadcasterPK implements Serializable {
	 @ManyToOne
	 private Genre genre;
	  
	 @ManyToOne
	 private BroadcasterInfo broadcasterInfo;
	 
	 @Override
	 public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result
	    + ((broadcasterInfo == null) ? 0 : broadcasterInfo.hashCode());
	  result = prime * result + ((genre == null) ? 0 : genre.hashCode());
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
	  GenreBroadcasterPK other = (GenreBroadcasterPK) obj;
	  if (broadcasterInfo == null) {
	   if (other.broadcasterInfo != null)
	    return false;
	  } else if (!broadcasterInfo.equals(other.broadcasterInfo))
	   return false;
	  if (genre == null) {
	   if (other.genre != null)
	    return false;
	  } else if (!genre.equals(other.genre))
	   return false;
	  return true;
	 }
	  
	 public Genre getGenre() {
	  return genre;
	 }
	 
	 public void setGenre(Genre genre) {
	  this.genre = genre;
	 }
	 
	 public BroadcasterInfo getSpotlightUser() {
	  return broadcasterInfo;
	 }
	 
	 public void setSpotlightUser(BroadcasterInfo broadcasterInfo) {
	  this.broadcasterInfo = broadcasterInfo;
	 }
	}

}
