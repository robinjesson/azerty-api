package fr.robinjesson.mybudgetapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserEntity user;

    private BigDecimal startAmount;

    private LocalDate pointingDate;

    @Embedded
    private Timestamp timestamp;
}
