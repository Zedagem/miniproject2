package edu.miu.payment.controller;

import edu.miu.payment.model.OrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentController {
    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody OrderRequest orderRequest){

        return ResponseEntity.ok().body(null);
    }

}
