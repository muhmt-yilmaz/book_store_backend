package com.project.book_store.service;

import com.project.book_store.entity.concretes.Book;
import com.project.book_store.entity.concretes.Cart;
import com.project.book_store.entity.concretes.CartItem;
import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.payload.mapper.CartMapper; 
import com.project.book_store.payload.request.CartItemRequest;
import com.project.book_store.payload.response.CartResponse;
import com.project.book_store.repository.BookRepository;
import com.project.book_store.repository.CartItemRepository;
import com.project.book_store.repository.CartRepository;
import com.project.book_store.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final CartMapper cartMapper;

    
    @Transactional
    public Cart getOrCreateCart(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı!"));

        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().customer(customer).build();
                    return cartRepository.save(newCart);
                });
    }

    
    @Transactional
    public CartResponse addToCart(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));

        
        if (book.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Yeterli stok bulunmamaktadır! Kalan stok: " + book.getStockQuantity());
        }

        
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (newQuantity > book.getStockQuantity()) {
                throw new RuntimeException("Sepetinizdeki miktar ile birlikte maksimum stok sınırını aşıyorsunuz!");
            }
            cartItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(request.getQuantity())
                    .build();
            cart.getCartItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart); 
    }

    
    @Transactional
    public CartResponse mergeGuestCart(String email, List<CartItemRequest> guestCartItems) {
        if (guestCartItems == null || guestCartItems.isEmpty()) {
            return cartMapper.toCartResponse(getOrCreateCart(email));
        }

        for (CartItemRequest item : guestCartItems) {
            try {
                addToCart(email, item);
            } catch (Exception e) {
                System.out.println("Guest sepetindeki ürün eklenemedi: " + e.getMessage());
            }
        }

        return cartMapper.toCartResponse(getOrCreateCart(email));
    }

    @Transactional
    public CartResponse updateItemQuantity(String email, Long cartItemId, Integer newQuantity) {
        Cart cart = getOrCreateCart(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sepet ürünü bulunamadı!"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok!");
        }

        if (newQuantity <= 0) {
            
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            
            if (cartItem.getBook().getStockQuantity() < newQuantity) {
                throw new RuntimeException("Yeterli stok bulunmamaktadır! Kalan stok: " + cartItem.getBook().getStockQuantity());
            }
            cartItem.setQuantity(newQuantity);
        }

        return cartMapper.toCartResponse(cartRepository.save(cart));
    }

    
    @Transactional
    public CartResponse removeFromCart(String email, Long cartItemId) {
        Cart cart = getOrCreateCart(email);

        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sepet ürünü bulunamadı!"));

        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok!");
        }

        cart.getCartItems().remove(itemToRemove);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public CartResponse getCartResponse(String email) {
        Cart cart = getOrCreateCart(email);
        return cartMapper.toCartResponse(cart);
    }
}