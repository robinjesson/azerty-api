package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
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

    @Column(nullable = false)
    private LocalDate transactionDate;

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

    @OneToMany
    @JoinTable(
            name = "transaction_tag",
            joinColumns = @JoinColumn(name = "fk_transaction_uuid"),
            inverseJoinColumns = @JoinColumn(name = "fk_tag_label")
    )
    private Set<TagEntity> tags;

    @Embedded
    private Timestamp timestamp;
}
