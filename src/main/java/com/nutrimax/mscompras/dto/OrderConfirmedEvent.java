package com.nutrimax.mscompras.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrderConfirmedEvent implements Serializable {

    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal total;
    private List<ItemEvento> items;

    public OrderConfirmedEvent() {
    }

    public OrderConfirmedEvent(Long pedidoId, Long usuarioId, BigDecimal total, List<ItemEvento> items) {
        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.total = total;
        this.items = items;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ItemEvento> getItems() {
        return items;
    }

    public void setItems(List<ItemEvento> items) {
        this.items = items;
    }

    public static class ItemEvento implements Serializable {
        private Long productoId;
        private Integer cantidad;

        public ItemEvento() {
        }

        public ItemEvento(Long productoId, Integer cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}