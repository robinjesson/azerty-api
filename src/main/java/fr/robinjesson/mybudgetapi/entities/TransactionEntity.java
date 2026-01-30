package fr.robinjesson.mybudgetapi.entities;

import fr.robinjesson.mybudgetapi.entities.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="transaction",
        discriminatorType = DiscriminatorType.STRING)
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_account_id", nullable = false)
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
    @JoinColumn(name = "fk_recurring_payment_id")
    private RecurringPaymentEntity recurringPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_transfer_id")
    private TransferEntity transfer;

    private LocalDate deferredDebitDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "transaction_tag",
            joinColumns = @JoinColumn(name = "fk_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_tag_id")
    )
    private List<TagEntity> tags;

    @Embedded
    private Timestamp timestamp;
}
