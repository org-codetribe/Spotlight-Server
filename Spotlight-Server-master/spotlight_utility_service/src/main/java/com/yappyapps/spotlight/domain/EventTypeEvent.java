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
 * The EventTypeEvent class is the domain class which maps the EventType java object
 * to Event java object and further there respective tables.
 * 
 * <h1>@Entity</h1> will enable it to map to database table.
 * 
 * <h1>@Table</h1> provides the database table name
 * 
 * @author Rajat Sethi
 * @version 1.0
 * @since 2018-09-08
 */
@Entity
@Table(name = "EVENT_TYPE_EVENT")
@AssociationOverrides({
	 @AssociationOverride(name="pk.eventType", joinColumns = @JoinColumn(name="event_type_id")),
	 @AssociationOverride(name="pk.event", joinColumns = @JoinColumn(name="event_id"))
	})
public class EventTypeEvent implements Serializable {
	
	@Id
	private EventTypeEventPK pk = new EventTypeEventPK();
	
	@Column(name = "CREATED_ON", columnDefinition = "TimeStamp")
	@NotNull
	private Timestamp createdOn;

	@Column(name = "STATUS", columnDefinition = "enum('Active','Inactive')")
	@NotNull
	private String status;

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
	  EventTypeEvent other = (EventTypeEvent) obj;
	  if (pk == null) {
	   if (other.pk != null)
	    return false;
	  } else if (!pk.equals(other.pk))
	   return false;
	  return true;
	 }
	 
	 public EventTypeEventPK getPk() {
	  return pk;
	 }
	 
	 public void setPk(EventTypeEventPK pk) {
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
	class EventTypeEventPK implements Serializable {
	 @ManyToOne
	 private EventType eventType;
	  
	 @ManyToOne
	 private Event event;
	 
	 @Override
	 public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result
	    + ((event == null) ? 0 : event.hashCode());
	  result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
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
	  EventTypeEventPK other = (EventTypeEventPK) obj;
	  if (event == null) {
	   if (other.event != null)
	    return false;
	  } else if (!event.equals(other.event))
	   return false;
	  if (eventType == null) {
	   if (other.eventType != null)
	    return false;
	  } else if (!eventType.equals(other.eventType))
	   return false;
	  return true;
	 }
	  
	 public EventType getEventType() {
	  return eventType;
	 }
	 
	 public void setEventType(EventType eventType) {
	  this.eventType = eventType;
	 }
	 
	 public Event getEvent() {
	  return event;
	 }
	 
	 public void setEvent(Event event) {
	  this.event = event;
	 }
	}
}