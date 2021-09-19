package ru.kos.someApp.entity;

import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(name = "Credit_Offer")
public class CreditOffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(nullable = false)
    private double sumValue;

    @Column
    private double anInitialFee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "credits_id")
    private Credit credit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clients_id")
    private Client client;

    @OneToMany(orphanRemoval=true, cascade = CascadeType.ALL, mappedBy = "creditOffer")
    private List<Payment> paymentList;

    @Column(name = "result_payment")
    private Double resultPayment;

    @Column(name = "result_percent")
    private Double resultPercent;


    public Double getResultPercent() {
        return resultPercent;
    }

    public void setResultPercent(Double resultPercent) {
        this.resultPercent = resultPercent;
    }

    public Double getResultPayment() {
        return resultPayment;
    }

    public void setResultPayment(Double resultPayment) {
        this.resultPayment = resultPayment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getSumValue() {
        return sumValue;
    }

    public void setSumValue(double sumValue) {
        this.sumValue = sumValue;
    }

    public double getInitialFee() {
        return anInitialFee;
    }

    public void setInitialFee(double anInitialFee) {
        this.anInitialFee = anInitialFee;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        for (Payment p : paymentList) {
            p.setCreditOffer(this);
        }
        this.paymentList = paymentList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CreditOffer that = (CreditOffer) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 408080079;
    }
}
