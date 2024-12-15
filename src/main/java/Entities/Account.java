package Entities;

import jakarta.persistence.*;

import java.util.Calendar;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    @Column
    private String surname;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Calendar birthday;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Calendar dateCreation;

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public Calendar getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Calendar dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
