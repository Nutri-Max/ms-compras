package com.nutrimax.mscompras.controller;

import com.nutrimax.mscompras.model.Pedido;
import com.nutrimax.mscompras.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/carrito/{usuarioId}")
    public ResponseEntity<?> verCarrito(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(pedidoService.obtenerOCrearCarrito(usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/carrito/{usuarioId}/items")
    public ResponseEntity<?> agregarItem(@PathVariable Long usuarioId, @RequestBody Map<String, Object> payload) {
        try {
            Long productoId = Long.valueOf(payload.get("productoId").toString());
            String nombreProducto = (String) payload.get("nombreProducto");
            Integer cantidad = Integer.valueOf(payload.get("cantidad").toString());
            BigDecimal precioUnitario = new BigDecimal(payload.get("precioUnitario").toString());

            Pedido carrito = pedidoService.agregarItem(usuarioId, productoId, nombreProducto, cantidad, precioUnitario);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/carrito/{usuarioId}/items/{itemId}")
    public ResponseEntity<?> quitarItem(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        try {
            Pedido carrito = pedidoService.quitarItem(usuarioId, itemId);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/carrito/{usuarioId}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long usuarioId) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(usuarioId);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<List<Pedido>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.obtenerHistorial(usuarioId));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPedido(@PathVariable Long pedidoId) {
        try {
            return ResponseEntity.ok(pedidoService.obtenerPedido(pedidoId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}