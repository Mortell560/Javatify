package Entities;

import Interface.LoginRegister.LoggingOperation;
import Interface.Operation;
import Interface.SongRelated.MusicConsulting;
import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Account account;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private int operation;
    @Column
    private String args;
    @Column(nullable = false)
    private boolean read = false;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    private Operation convertToOperation() {
        return switch (operation) {
            case 1 -> new LoggingOperation();
            case 2 -> new MusicConsulting(args);
            default -> null;
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }


}
