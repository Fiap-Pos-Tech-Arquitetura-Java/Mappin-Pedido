package br.com.fiap.postech.mappin.pedido.repository;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.helper.PedidoHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoRepositoryTest {
    @Mock
    private PedidoRepository pedidoRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarPedido() {
        // Arrange
        var pedido = PedidoHelper.getPedido(false);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        // Act
        var savedPedido = pedidoRepository.save(pedido);
        // Assert
        assertThat(savedPedido).isNotNull().isEqualTo(pedido);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void devePermitirBuscarPedido() {
        // Arrange
        var pedido = PedidoHelper.getPedido(true);
        when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));
        // Act
        var pedidoOpcional = pedidoRepository.findById(pedido.getId());
        // Assert
        assertThat(pedidoOpcional).isNotNull().containsSame(pedido);
        pedidoOpcional.ifPresent(
                pedidoRecebido -> {
                    assertThat(pedidoRecebido).isInstanceOf(Pedido.class).isNotNull();
                    assertThat(pedidoRecebido.getId()).isEqualTo(pedido.getId());
                    assertThat(pedidoRecebido.getIdCliente()).isEqualTo(pedido.getIdCliente());
                    assertThat(pedidoRecebido.getStatus()).isEqualTo(pedido.getStatus());
                    assertThat(pedidoRecebido.getItens()).isNotNull();
                    assertThat(pedidoRecebido.getItens().get(0)).isEqualTo(pedido.getItens().get(0));
                    assertThat(pedidoRecebido.getItens().get(0).getId()).isEqualTo(pedido.getItens().get(0).getId());
                    assertThat(pedidoRecebido.getItens().get(0).getIdProduto()).isEqualTo(pedido.getItens().get(0).getIdProduto());
                    assertThat(pedidoRecebido.getItens().get(0).getQuantidade()).isEqualTo(pedido.getItens().get(0).getQuantidade());
                    assertThat(pedidoRecebido.getItens().get(1)).isEqualTo(pedido.getItens().get(1));
                    assertThat(pedidoRecebido.getItens().get(1).getId()).isEqualTo(pedido.getItens().get(1).getId());
                    assertThat(pedidoRecebido.getItens().get(1).getIdProduto()).isEqualTo(pedido.getItens().get(1).getIdProduto());
                    assertThat(pedidoRecebido.getItens().get(1).getQuantidade()).isEqualTo(pedido.getItens().get(1).getQuantidade());
                }
        );
        verify(pedidoRepository, times(1)).findById(pedido.getId());
    }
    @Test
    void devePermitirRemoverPedido() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(pedidoRepository).deleteById(id);
        //Act
        pedidoRepository.deleteById(id);
        //Assert
        verify(pedidoRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarPedidos() {
        // Arrange
        var pedido1 = PedidoHelper.getPedido(true);
        var pedido2 = PedidoHelper.getPedido(true);
        var listaPedidos = Arrays.asList(
                pedido1,
                pedido2
        );
        when(pedidoRepository.findAll()).thenReturn(listaPedidos);
        // Act
        var pedidosListados = pedidoRepository.findAll();
        assertThat(pedidosListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(pedido1, pedido2);
        verify(pedidoRepository, times(1)).findAll();
    }
}