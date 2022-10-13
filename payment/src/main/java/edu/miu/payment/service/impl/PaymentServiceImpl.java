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
    private Map<String, String> way;


    @Value("${mytoken}")
    private String token;
    private HttpHeaders headers = new HttpHeaders();

    @Override
    public String checkout(OrderRequest orderRequest) {

        PaymentMethod paymentMethod;
        Integer accountId = orderRequest.getAccountId();
        Integer orderId = orderRequest.getOrderId();

        // Getting the payment information before sending to the payments

        if(!orderRequest.getPaymentType().isPresent()){
            // get payment information from account
            paymentMethod = restTemplate.getForObject("http://account-service:8081/accounts/preferredPaymentMethod/"+accountId,PaymentMethod.class);
//            paymentMethod = getPaymentMethod();
        }else{
            //get payment information from account
            PaymentType paymentType1 = orderRequest.getPaymentType().get();
            paymentMethod = restTemplate.getForObject("http://account-service:8081/accounts/preferredPaymentMethod/"+ accountId + "/" + paymentType1,PaymentMethod.class);
//            paymentMethod = getPaymentMethod();
        }

        System.out.println(paymentMethod);

        PaymentRequest paymentRequest = new PaymentRequest(accountId,orderId,paymentMethod);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token",token);

        HttpEntity<PaymentRequest> request =
                new HttpEntity<PaymentRequest>(paymentRequest, headers);


        // Dynamically getting the url for the payment services from configMap
        String url = way.get(paymentMethod.getPaymentType().toString());
        System.out.println(url);
        String response = restTemplate.postForObject("http://"+ url,request,String.class);
        System.out.println(response);

        if(response != null){

            // getting the order from order service
//              Order order = restTemplate.getForObject("http://order-service:0000/"+ orderId, Order.class);
                Order order = getOrder();
              order.setStatus("Payed");

           // Updating the order
//            restTemplate.put("http://order-service:0000/"+orderId,order);


          // Send to shipping
            if(ship(accountId)){
                order.setStatus("shipped");
//                restTemplate.put("http://order-service:0000/"+orderId,order);
                return "shipped";
            }

            return response;
        }

        return null;
    }

    private Boolean ship(Integer accountId){

        // Get shipping address by account id
//        ShippingAddress shippingAddress = restTemplate.getForObject("http://account-service:8081/shippingAddress"+accountId ,ShippingAddress.class);
          ShippingAddress shippingAddress = getShippingAddress();
        // Send to shipping

        HttpEntity<ShippingAddress> request =
                new HttpEntity<>(shippingAddress, headers);
        Boolean response = restTemplate.postForObject("http://shipping-service:9096",request,Boolean.class);

        return response;


    }

//    private PaymentMethod getPaymentMethod(){
//        return new PaymentMethod(PaymentType.CC,null,null,"0093412346781","332",null);
//    }
    private Order getOrder(){
        return new Order(1,2,30,"placed");
    }
    private ShippingAddress getShippingAddress(){
        return new ShippingAddress("Ethiopia","Addis Ababa","Bole","1000","africa ave");
    }

}
