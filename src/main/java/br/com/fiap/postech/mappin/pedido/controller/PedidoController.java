package br.com.fiap.postech.mappin.pedido.controller;

import br.com.fiap.postech.mappin.pedido.entities.Pedido;
import br.com.fiap.postech.mappin.pedido.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "registra um pedido")
    @PostMapping
    public ResponseEntity<Pedido> save(@Valid @RequestBody Pedido pedido) {
        Pedido savedPedido = pedidoService.save(pedido);
        return new ResponseEntity<>(savedPedido, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os pedidos")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<Pedido>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Pedido pedido = new Pedido(null, null, status, null);
        pedido.setId(null);
        var pageable = PageRequest.of(page, size);
        var pedidos = pedidoService.findAll(pageable, pedido);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @Operation(summary = "lista todos os pedidos")
    @GetMapping( path = "findByStatus/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pedido>> findByStatus(@PathVariable String status) {
        var pedidos = pedidoService.findByStatus(status);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @Operation(summary = "lista um pedido por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            Pedido pedido = pedidoService.findById(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um pedido por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody Pedido pedido) {
        try {
            Pedido updatedPedido = pedidoService.update(id, pedido);
            return new ResponseEntity<>(updatedPedido, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um pedido por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            pedidoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException
                exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
