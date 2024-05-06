package br.com.fiap.postech.mappin.pedido.integration;

import br.com.fiap.postech.mappin.pedido.Generated;

@Generated
public class ProdutoResponse {
    private Double preco;

    public ProdutoResponse() {
        super();
    }

    public ProdutoResponse(Double preco) {
        this.preco = preco;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
