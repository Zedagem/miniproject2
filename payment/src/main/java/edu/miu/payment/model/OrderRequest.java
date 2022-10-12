package edu.miu.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequest {
    private Integer orderId;
    private Integer accountId;
    private Double price;
    private PaymentType paymentType;
}
