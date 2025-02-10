package group_2.cursus.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InvalidToken {
    
    @Id
    private String Id;

    private Date expiryTime;

    public InvalidToken() {
        
    }

    public InvalidToken(String id, Date expiryTime) {
        Id = id;
        this.expiryTime = expiryTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }
}
