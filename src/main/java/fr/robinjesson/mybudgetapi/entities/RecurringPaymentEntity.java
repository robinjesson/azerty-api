package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.FlowTypeEnum;
import fr.robinjesson.mybudgetapi.entities.enums.FrequencyEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recurring_payment")
@Getter
@Setter
public class RecurringPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_account_entity_uuid", nullable = false)
    private AccountEntity accountEntity;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FrequencyEnum frequency;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlowTypeEnum flowType;

    private String category;

    @Column(nullable = false)
    private Boolean isActive;

    @Embedded
    private Timestamp timestamp;
}
