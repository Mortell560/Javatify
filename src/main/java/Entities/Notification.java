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
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private int operation;
    @Column
    private String args;

    private Operation convertToOperation(){
        return switch (operation) {
            case 1 -> new LoggingOperation(args);
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
