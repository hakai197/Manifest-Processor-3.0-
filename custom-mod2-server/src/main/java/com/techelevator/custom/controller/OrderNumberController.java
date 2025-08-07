package com.techelevator.custom.controller;

import com.techelevator.custom.dao.OrderNumberDao;
import com.techelevator.custom.exception.DaoException;
import com.techelevator.custom.model.OrderNumber;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderNumberController {

    private final OrderNumberDao orderNumberDao;

    public OrderNumberController(OrderNumberDao orderNumberDao) {
        this.orderNumberDao = orderNumberDao;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<OrderNumber> getAllOrders() {
        try {
            return orderNumberDao.getAllOrders();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/number/{orderNumber}")
    public OrderNumber getOrderByNumber(@PathVariable String orderNumber) {
        try {
            OrderNumber order = orderNumberDao.getOrderByNumber(orderNumber);
            if (order == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }
            return order;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving order");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/trailer/{trailerNumber}")
    public List<OrderNumber> getOrdersByTrailerNumber(@PathVariable String trailerNumber) {
        try {
            List<OrderNumber> orders = orderNumberDao.getOrdersByTrailerNumber(trailerNumber);
            if (orders.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for trailer");
            }
            return orders;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/customer/{customerName}")
    public List<OrderNumber> getOrdersByCustomerName(@PathVariable String customerName) {
        try {
            List<OrderNumber> orders = orderNumberDao.getOrdersByCustomerName(customerName);
            if (orders.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for customer");
            }
            return orders;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/shipper/{shipperName}")
    public List<OrderNumber> getOrdersByShipperName(@PathVariable String shipperName) {
        try {
            List<OrderNumber> orders = orderNumberDao.getOrdersByShipperName(shipperName);
            if (orders.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for shipper");
            }
            return orders;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/status/{status}")
    public List<OrderNumber> getOrdersByStatus(@PathVariable String status) {
        try {
            List<OrderNumber> orders = orderNumberDao.getOrdersByStatus(status);
            if (orders.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found with status: " + status);
            }
            return orders;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving orders by status");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderNumber createOrder(@RequestBody OrderNumber orderNumber) {
        try {
            return orderNumberDao.createOrder(orderNumber);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating order");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{orderNumber}")
    public OrderNumber updateOrder(@PathVariable String orderNumber, @RequestBody OrderNumber updatedOrder) {
        try {
            // Ensure the order number in the path matches the one in the body
            if (!orderNumber.equals(updatedOrder.getOrderNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order number mismatch");
            }

            boolean updated = orderNumberDao.updateOrder(updatedOrder);
            if (!updated) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }

            return orderNumberDao.getOrderByNumber(orderNumber);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating order");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderNumber) {
        try {
            boolean deleted = orderNumberDao.deleteOrder(orderNumber);
            if (!deleted) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting order");
        }
    }
}