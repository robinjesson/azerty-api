package fr.robinjesson.azertyapi.entities;

import fr.robinjesson.azertyapi.entities.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="transaction",
        discriminatorType = DiscriminatorType.STRING)
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_account_uuid", nullable = false)
    private AccountEntity account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String category;

    @Column(nullable = false)
    private Boolean isPointed;

    @Column(nullable = false)
    private Boolean isReconciled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_recurring_payment_uuid")
    private RecurringPaymentEntity recurringPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_transfer_uuid")
    private TransferEntity transfer;

    private LocalDate deferredDebitDate;

    @Embedded
    private Timestamp timestamp;
}
