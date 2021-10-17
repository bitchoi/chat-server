package com.bitchoi.chatserver.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private Set<Message> messages;

	private String username;

	private String sessionId;
}
