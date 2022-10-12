package edu.miu.payment.service;

import edu.miu.payment.model.OrderRequest;


public interface PaymentService {
    String checkout(OrderRequest orderRequest);
}
