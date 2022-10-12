package edu.miu.payment.service.impl;

import edu.miu.payment.model.*;
import edu.miu.payment.service.PaymentService;
import edu.miu.payment.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    HttpServletRequest request;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    JwtUtil jwtUtil;
    @Value("#{${paymentmap}}")
    private Map<String, String> paymentType;

    @Value("${mytoken}")
    private String token;
    private HttpHeaders headers = new HttpHeaders();

    @Override
    public String checkout(OrderRequest orderRequest) {

        PaymentMethod paymentMethod;
        Integer accountId = orderRequest.getAccountId();
        PaymentType paymentType1 = orderRequest.getPaymentType();
        Integer orderId = orderRequest.getOrderId();

        // Getting the payment information before sending to the payments

        if(orderRequest.getPaymentType() == null){
            // get payment information from account
            paymentMethod = restTemplate.getForObject("http://account-service:8081/preferredPaymentMethod/"+accountId,PaymentMethod.class);

        }else{
            //get payment information from account
            paymentMethod = restTemplate.getForObject("http://account-service:8081/preferredPaymentMethod/"+ accountId + "/" + paymentType1,PaymentMethod.class);
        }


        PaymentRequest paymentRequest = new PaymentRequest(accountId,orderId,paymentMethod);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token",token);

        HttpEntity<PaymentRequest> request =
                new HttpEntity<PaymentRequest>(paymentRequest, headers);


        // Dynamically getting the url for the payment services from configMap
        String url = paymentType.get(orderRequest.getPaymentType());

        String response = restTemplate.postForObject("http://"+ url,request,String.class);


        if(response != null){

            // getting the order from order service
              Order order = restTemplate.getForObject("http://order-service:0000/"+ orderId, Order.class);
              order.setStatus("Payed");

           // Updating the order
            restTemplate.put("http://order-service:0000/"+orderId,order);


          // Send to shipping
            if(ship(accountId)){
                order.setStatus("shipped");
                restTemplate.put("http://order-service:0000/"+orderId,order);
                return "shipped";
            }

            return response;
        }

        return null;
    }

    private Boolean ship(Integer accountId){

        // Get shipping address by account id
        ShippingAddress shippingAddress = restTemplate.getForObject("http://shippingAddress/"+accountId + ":8081",ShippingAddress.class);

        // Send to shipping

        HttpEntity<ShippingAddress> request =
                new HttpEntity<>(shippingAddress, headers);
        Boolean response = restTemplate.postForObject("http://shipping-service:9096",request,Boolean.class);

        return response;


    }

}
