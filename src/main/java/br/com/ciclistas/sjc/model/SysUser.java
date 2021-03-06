package br.com.ciclistas.sjc.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class SysUser extends PanacheEntity {
	
	@Column(length = 40)
	public String name;

	@Column(updatable = false, nullable = false)
	public String password;

	@Column(updatable = false, nullable = false)
	public String role;
	
	public Long getId() {
		return this.id;
	}

}
