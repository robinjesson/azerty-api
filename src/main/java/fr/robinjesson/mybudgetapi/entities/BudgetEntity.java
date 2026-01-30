package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.FrequencyEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budget")
@Getter
@Setter
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal targetAmount;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_target_account_id")
    private AccountEntity targetAccount;

    @Column(nullable = false)
    private BigDecimal amountSetAside;

    @Enumerated(EnumType.STRING)
    private FrequencyEnum contributionFrequency;

    private BigDecimal contributionAmount;

    private LocalDate lastContributionDate;

    @Embedded
    private Timestamp timestamp;
}
