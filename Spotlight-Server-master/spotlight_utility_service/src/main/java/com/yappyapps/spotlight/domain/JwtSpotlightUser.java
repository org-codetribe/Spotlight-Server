package com.yappyapps.spotlight.domain;

import java.sql.Timestamp;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtSpotlightUser implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Integer id;
    private final String username;
    private final String address1;
    private final String address2;
    private final String city;
    private final String country;
    private final String name;
    private final String paypalEmailId;
    private final String phone;
    private final String state;
    private final String email;
    private final String password;
    private final String status;
    private final String uniqueName;
    private final String userType;
    private final String zip;
    private final Collection<? extends GrantedAuthority> authorities;
//    private final boolean enabled;
    private final Timestamp createdOn;
    private final Timestamp updatedOn;

    public JwtSpotlightUser(
    		Integer id,
            String username,
            String address1,
            String address2,
            String city,
            String country,
            String name,
            String paypalEmailId,
            String phone,
            String state,
            String email,
            String password,
            String status, 
            String uniqueName, 
            String userType, 
            String zip, 
            Collection<? extends GrantedAuthority> authorities,
            Timestamp createdOn,
            Timestamp updatedOn
    ) {
        this.id = id;
        this.username = username;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.country = country;
        this.name = name;
        this.paypalEmailId = paypalEmailId;
        this.phone = phone;
        this.state = state;
        this.email = email;
        this.password = password;
        this.status = status;
        this.uniqueName = uniqueName;
        this.userType = userType;
        this.zip = zip;
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

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getName() {
		return name;
	}

	public String getPaypalEmailId() {
		return paypalEmailId;
	}

	public String getPhone() {
		return phone;
	}

	public String getState() {
		return state;
	}

	public String getStatus() {
		return status;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getUserType() {
		return userType;
	}

	public String getZip() {
		return zip;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

    
}
