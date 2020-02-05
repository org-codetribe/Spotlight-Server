package com.yappyapps.spotlight.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TermVector;
import org.springframework.data.annotation.Transient;

/**
 * The BroadcasterInfo class is the domain class which maps the BroadcasterInfo
 * java object to broadcaster_info table.
 * 
 * <h1>@Entity</h1> will enable it to map to database table.
 * 
 * <h1>@Table</h1> provides the database table name
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Indexed
@Entity
@Table(name = "BROADCASTER_INFO")
public class BroadcasterInfo {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Field(termVector = TermVector.YES)
	@Column(name = "DISPLAY_NAME", length = 255)
	@NotNull
	private String displayName;

	@Field(termVector = TermVector.YES)
	@Column(name = "BIOGRAPHY", columnDefinition = "Text")
	private String biography;

	@Field(termVector = TermVector.YES)
	@Column(name = "SHORT_DESC", length = 255)
	private String shortDesc;

	@Column(name = "BANNER_URL", columnDefinition = "Text")
	@NotNull
	private String bannerUrl;

	@IndexedEmbedded
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE })
	@JoinColumn(name = "spotlight_user_id", nullable = false)
	private SpotlightUser spotlightUser = null;

	@Column(name = "IS_TRENDING")
	private Boolean isTrending;

	@Column(name = "UNIQUE_NAME", length = 255, unique = true)
	private String uniqueName;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	private String status;

	@Transient
	private transient Float commission;

	@IndexedEmbedded
	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "GENRE_BROADCASTER", joinColumns = {
			@JoinColumn(name = "BROADCASTER_INFO_ID") }, inverseJoinColumns = { @JoinColumn(name = "GENRE_ID") })
	private Set<Genre> genre = new HashSet<Genre>(0);

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the biography
	 */
	public String getBiography() {
		return biography;
	}

	/**
	 * @param biography
	 *            the biography to set
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}

	/**
	 * @return the shortDesc
	 */
	public String getShortDesc() {
		return shortDesc;
	}

	/**
	 * @param shortDesc
	 *            the shortDesc to set
	 */
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	/**
	 * @return the bannerUrl
	 */
	public String getBannerUrl() {
		return bannerUrl;
	}

	/**
	 * @param bannerUrl
	 *            the bannerUrl to set
	 */
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	/**
	 * @return the spotlightUser
	 */
	public SpotlightUser getSpotlightUser() {
		return spotlightUser;
	}

	/**
	 * @param spotlightUser
	 *            the spotlightUser to set
	 */
	public void setSpotlightUser(SpotlightUser spotlightUser) {
		this.spotlightUser = spotlightUser;
	}

	/**
	 * @return the isTrending
	 */
	public Boolean getIsTrending() {
		return isTrending;
	}

	/**
	 * @param isTrending
	 *            the isTrending to set
	 */
	public void setIsTrending(Boolean isTrending) {
		this.isTrending = isTrending;
	}

	/**
	 * @return the uniqueName
	 */
	public String getUniqueName() {
		return uniqueName;
	}

	/**
	 * @param uniqueName
	 *            the uniqueName to set
	 */
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
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

	/**
	 * @return the genreBroadcaster
	 */
	public Set<Genre> getGenre() {
		return genre;
	}

	/**
	 * @param genre
	 *            the genre to set
	 */
	public void setGenre(Set<Genre> genre) {
		this.genre = genre;
	}

	/**
	 * @return the commission
	 */
	public Float getCommission() {
		return commission;
	}

	/**
	 * @param commission
	 *            the commission to set
	 */
	public void setCommission(Float commission) {
		this.commission = commission;
	}

}
