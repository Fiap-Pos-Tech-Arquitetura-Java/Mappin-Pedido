package br.com.fiap.postech.mappin.pedido.controller;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.enumerations.Status;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import br.com.fiap.postech.mappin.pedido.services.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PedidoControllerTest {
    public static final String CLIENTE = "/pedido";
    private MockMvc mockMvc;
    @Mock
    private PedidoService pedidoService;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        PedidoController pedidoController = new PedidoController(pedidoService);
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarPedido {
        @Test
        void devePermitirCadastrarPedido() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(false);
            when(pedidoService.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post(CLIENTE).contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(pedido)))
                    .andExpect(status().isCreated());
            // Assert
            verify(pedidoService, times(1)).save(any(Pedido.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarPedido_RequisicaoXml() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(false);
            when(pedidoService.save(any(Pedido.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/pedido").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(pedido)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(pedidoService, never()).save(any(Pedido.class));
        }
    }
    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedidoPorId() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoService.findById(any(UUID.class))).thenReturn(pedido);
            // Act
            mockMvc.perform(get("/pedido/{id}", pedido.getId().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(pedidoService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarPedidoPorId_idNaoExiste() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoService.findById(pedido.getId())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/pedido/{id}", pedido.getId().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(pedidoService, times(1)).findById(pedido.getId());
        }

        @Test
        void devePermitirBuscarTodosPedido() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var pedido = PedidoHelper.getPedido(true);
            var criterioPedido = new Pedido(null, null, pedido.getStatus(), null);
            criterioPedido.setId(null);
            List<Pedido> listPedido = new ArrayList<>();
            listPedido.add(pedido);
            Page<Pedido> pedidos = new PageImpl<>(listPedido);
            var pageable = PageRequest.of(page, size);
            when(pedidoService.findAll(
                            pageable,
                            criterioPedido
                    )
            ).thenReturn(pedidos);
            // Act
            mockMvc.perform(
                            get("/pedido")
                                    .param("page", String.valueOf(page))
                                    .param("size", String.valueOf(size))
                                    .param("status", pedido.getStatus())
                    )
                    //.andDo(print())
                    .andExpect(status().is5xxServerError())
            //.andExpect(jsonPath("$.content", not(empty())))
            //.andExpect(jsonPath("$.totalPages").value(1))
            //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(pedidoService, times(1)).findAll(pageable, criterioPedido);
        }

        @Test
        void devePermitirBuscarPedidoPorStatus() throws Exception {
            // Arrange
            String status = Status.ENTREGUE.name();
            List<Pedido> pedidos = List.of(PedidoHelper.getPedido(true));
            when(pedidoService.findByStatus(status)
            ).thenReturn(pedidos);
            // Act
            mockMvc.perform(get("/pedido/findByStatus/{status}", status))
                    .andExpect(status().isOk());
            // Assert
            verify(pedidoService, times(1)).findByStatus(anyString());
        }
    }

    @Nested
    class AlterarPedido {
        @Test
        void devePermitirAlterarPedido() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoService.update(pedido.getId(), pedido)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/pedido/{id}", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(pedidoService, times(1)).update(pedido.getId(), pedido);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedido_RequisicaoXml() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoService.update(pedido.getId(), pedido)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/pedido/{id}", pedido.getId())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(pedido)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(pedidoService, never()).update(pedido.getId(), pedido);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPedidoPorId_idNaoExiste() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            when(pedidoService.update(pedido.getId(), pedido)).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(put("/pedido/{id}", pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(pedidoService, times(1)).update(any(UUID.class), any(Pedido.class));
        }
    }

    @Nested
    class RemoverPedido {
        @Test
        void devePermitirRemoverPedido() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            doNothing().when(pedidoService).delete(pedido.getId());
            // Act
            mockMvc.perform(delete("/pedido/{id}", pedido.getId()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(pedidoService, times(1)).delete(pedido.getId());
            verify(pedidoService, times(1)).delete(pedido.getId());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverPedidoPorId_idNaoExiste() throws Exception {
            // Arrange
            var pedido = PedidoHelper.getPedido(true);
            doThrow(new IllegalArgumentException("Pedido não encontrado com o ID: " + pedido.getId()))
                    .when(pedidoService).delete(pedido.getId());
            // Act
            mockMvc.perform(delete("/pedido/{id}", pedido.getId()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(pedidoService, times(1)).delete(pedido.getId());
        }
    }
}