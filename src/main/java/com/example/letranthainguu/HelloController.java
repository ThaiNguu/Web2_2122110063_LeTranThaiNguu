package com.example.letranthainguu;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

    @PostMapping("/hello")
    public int createHello(@RequestBody SumRequest request) {
        return request.getA() + request.getB();
    }

    @PutMapping("/hello")
    public int updateHello(@RequestBody SumRequest request) {
        return request.getA() + request.getB();
    }

    @DeleteMapping("/hello")
    public String deleteHello() {
        return "Hello deleted!";
    }
}

class SumRequest {
    private int a;
    private int b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
