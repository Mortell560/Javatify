package Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "friends")
    private Set<Account> friends = new HashSet<>();
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "family")
    private Set<Account> family = new HashSet<>();

    public int getAge(){
        int d1,m1,y1,d2,m2,y2;
        d1 = birthday.get(Calendar.DAY_OF_MONTH);
        m1 = birthday.get(Calendar.MONTH)+1;
        y1 = birthday.get(Calendar.YEAR);
        d2 = dateCreation.get(Calendar.DAY_OF_MONTH);
        m2 = dateCreation.get(Calendar.MONTH)+1;
        y2 = dateCreation.get(Calendar.YEAR);
        return Period.between(LocalDate.of(y1,m1,d1), LocalDate.of(y2, m2, d2)).getYears();
    }

    public Set<Account> getFriends() {
        return friends;
    }

    public void setFriends(Set<Account> friends) {
        this.friends = friends;
    }

    public Set<Account> getFamily() {
        return family;
    }

    public void setFamily(Set<Account> family) {
        this.family = family;
    }

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
