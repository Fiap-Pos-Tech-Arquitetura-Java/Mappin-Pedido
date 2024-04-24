package br.com.fiap.postech.mappin.pedido.controller;

import br.com.fiap.postech.mappin.pedido.entities.Item;
import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class PedidoControllerIT {

    public static final String PATH = "/mappin/pedido";
    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarPedido {
        @Test
        void devePermitirCadastrarPedido() {
            var pedido = PedidoHelper.getPedido(false);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(pedido)
            .when()
                .post(PATH)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/pedido.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarPedido_RequisicaoXml() {
            /*
              Na aula o professor instanciou uma string e enviou no .body()
              Mas como o teste valida o contentType o body pode ser enviado com qualquer conteudo
              ou nem mesmo ser enviado como ficou no teste abaixo.
             */
            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post(PATH)
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedidoPorId() {
            var id = "a8c301d4-347f-4d1b-9171-c191b8aa0d88";
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH + "/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/pedido.schema.json"));
            // TODO VERIFICAR A OBRIGATORIEDADE DO ID
        }
        @Test
        void deveGerarExcecao_QuandoBuscarPedidoPorId_idNaoExiste() {
            var id = PedidoHelper.getPedido(true).getId();
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH + "/{id}", id)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosPedido() {
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/pedido.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosPedido_ComPaginacao() {
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(PATH)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/pedido.page.schema.json"));
        }
    }

    @Nested
    class AlterarPedido {
        @Test
        void devePermitirAlterarPedido() {
            var item1 = new Item(UUID.fromString("cbc55920-97a1-4fdf-a77e-b66bb824d074"), 2);
            item1.setId(UUID.fromString("0f4b8290-678c-49ca-8ca7-3eddf2625cb6"));
            var item2 = new Item(UUID.fromString("39854e07-3c75-4523-be4f-42da0244edc9"), 2);
            item2.setId(UUID.fromString("9bd2f440-be3e-4683-a06c-8ae9c21c40af"));
            var itens = List.of(item1, item2);
            var pedido = new Pedido(
                    UUID.fromString("c0444933-ae73-43d9-b8a9-36eff4a79009"),
                    250.83,
                    "FINALIZADO",
                    itens
            );
            pedido.setId(UUID.fromString("d5b351c5-bc58-4c5c-8549-5113e7fea1ac"));
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .body(pedido).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(PATH + "/{id}", pedido.getId())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/pedido.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedido_RequisicaoXml() {
            var pedido = PedidoHelper.getPedido(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .body(pedido).contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .put(PATH + "/{id}", pedido.getId())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_idNaoExiste() {
            var pedido = PedidoHelper.getPedido(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
                .body(pedido).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(PATH + "/{id}", pedido.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Pedido não encontrado com o ID: " + pedido.getId()));
        }
    }

    @Nested
    class RemoverPedido {
        @Test
        void devePermitirRemoverPedido() {
            var pedido = new Pedido(
                    UUID.randomUUID(),
                    Math.random() * 1000,
                    "FINALIZADO",
                    null
            );
            pedido.setId(UUID.fromString("90b91424-d7f4-4695-b5a8-e489232007b9"));
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
            .when()
                .delete(PATH + "/{id}", pedido.getId())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverPedidoPorId_idNaoExiste() {
            var pedido = PedidoHelper.getPedido(true);
            given()
                //.header(HttpHeaders.AUTHORIZATION, PedidoHelper.getToken())
            .when()
                .delete(PATH + "/{id}", pedido.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("Pedido não encontrado com o ID: " + pedido.getId()));
        }
    }
}
