package Entities;

import jakarta.persistence.*;

import java.util.Calendar;

@Entity
public class BlindTestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account user;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BlindTest test;
    @Column
    private int success;
    @Column
    private int total;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Calendar timestamp;

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public BlindTest getTest() {
        return test;
    }

    public void setTest(BlindTest test) {
        this.test = test;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

}
