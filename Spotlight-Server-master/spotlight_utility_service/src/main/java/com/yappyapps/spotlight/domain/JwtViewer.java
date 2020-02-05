package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtViewer implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Integer id;
    private final String username;
    private final String fname;
    private final String lname;
    private final String phone;
    private final String email;
    private final String password;
    private final String status;
    private final String uniqueName;
    private final String chatName;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Timestamp createdOn;
    private final Timestamp updatedOn;

    public JwtViewer(
    		Integer id,
            String username,
            String fname,
            String lname,
            String phone,
            String email,
            String password,
            String status, 
            String uniqueName,
            String chatName, 
            Collection<? extends GrantedAuthority> authorities,
            Timestamp createdOn,
            Timestamp updatedOn
    ) {
        this.id = id;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.status = status;
        this.uniqueName = uniqueName;
        this.chatName = chatName;
        this.authorities = authorities;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}

	public String getPhone() {
		return phone;
	}

	public String getStatus() {
		return status;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getChatName() {
		return chatName;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

    
}
