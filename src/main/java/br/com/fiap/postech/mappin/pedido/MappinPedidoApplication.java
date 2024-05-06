package br.com.fiap.postech.mappin.pedido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@Generated
public class MappinPedidoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MappinPedidoApplication.class, args);
    }

}
