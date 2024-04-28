package br.com.fiap.postech.mappin.pedido.enumerations;

import java.util.Arrays;

public enum Status {
    AGUARDANDO_PAGAMENTO, PAGAMENTO_REALIZADO, AGUARDANDO_ENTREGA, FINALIZADO;

    public Boolean contains(String status) {
        return Arrays.stream(Status.values()).anyMatch(s -> {
                return s.name().equals(status);
            }
        );
    }
}
