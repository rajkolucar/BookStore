package com.rajko.lucar.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cart {

	@Id
	@Column
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Integer idCart;
	
	@Column
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Integer idUser;
	
	@Column
	private Integer idOrder;
	
	public Integer getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(Integer idOrder) {
		this.idOrder = idOrder;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public Integer getIdCart() {
		return idCart;
	}

	public void setIdCart(Integer idCart) {
		this.idCart = idCart;
	}
}
