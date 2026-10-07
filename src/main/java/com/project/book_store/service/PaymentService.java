package com.project.book_store.service;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.entity.concretes.Order;
import com.project.book_store.entity.concretes.OrderItem;
import com.project.book_store.payload.request.OrderRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${iyzico.api.key:}")
    private String apiKey;

    @Value("${iyzico.secret.key:}")
    private String secretKey;

    @Value("${iyzico.base.url:https://sandbox-api.iyzipay.com}")
    private String baseUrl;

    @Getter
    public static class PaymentResult {
        private final boolean success;
        private final String paymentId;
        private final String errorMessage;

        private PaymentResult(boolean success, String paymentId, String errorMessage) {
            this.success = success;
            this.paymentId = paymentId;
            this.errorMessage = errorMessage;
        }

        public static PaymentResult success(String paymentId) {
            return new PaymentResult(true, paymentId, null);
        }

        public static PaymentResult failure(String errorMessage) {
            return new PaymentResult(false, null, errorMessage);
        }
    }


    
    public PaymentResult chargeForOrder(Customer customer, Order order, OrderRequest orderRequest, BigDecimal totalPrice) {

        if (apiKey == null || apiKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            return PaymentResult.failure(
                    "Ödeme altyapısı henüz yapılandırılmamış: application.properties dosyasına " +
                    "iyzico.api.key ve iyzico.secret.key eklemeniz gerekiyor. " +
                    "(sandbox hesabı için bkz. iyzico.com)"
            );
        }

        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(order.getOrderNumber());
        request.setPrice(totalPrice);          
        request.setPaidPrice(totalPrice);       
        request.setCurrency(Currency.TRY.name());
        request.setInstallment(1);
        request.setBasketId(order.getOrderNumber());
        request.setPaymentChannel(PaymentChannel.WEB.name());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(orderRequest.getCardHolderName());
        paymentCard.setCardNumber(orderRequest.getCardNumber());
        paymentCard.setExpireMonth(orderRequest.getExpireMonth());
        paymentCard.setExpireYear(orderRequest.getExpireYear());
        paymentCard.setCvc(orderRequest.getCvc());
        paymentCard.setRegisterCard(0);
        request.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId(String.valueOf(customer.getId()));
        buyer.setName(customer.getName());
        buyer.setSurname(customer.getSurname());
        buyer.setGsmNumber(orderRequest.getShippingPhone());
        buyer.setEmail(customer.getEmail());
        
        
        buyer.setIdentityNumber(customer.getTc() != null && !customer.getTc().isBlank() ? customer.getTc() : "11111111111");
        buyer.setRegistrationDate(customer.getCreatedAt() != null
                ? customer.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "2015-10-05 12:43:35");
        buyer.setLastLoginDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now()));
        buyer.setRegistrationAddress(orderRequest.getShippingAddress());
        buyer.setIp("85.34.78.112"); 
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34000");
        request.setBuyer(buyer);

        Address address = new Address();
        address.setContactName(orderRequest.getRecipientName());
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setAddress(orderRequest.getShippingAddress());
        address.setZipCode("34000");
        request.setShippingAddress(address);
        request.setBillingAddress(address);

        List<BasketItem> basketItems = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(String.valueOf(item.getBook().getId()));
            basketItem.setName(item.getBook().getTitle());
            basketItem.setCategory1(item.getBook().getCategory() != null ? item.getBook().getCategory().getCategoryName() : "Kitap");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            
            basketItem.setPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            basketItems.add(basketItem);
        }
        request.setBasketItems(basketItems);

        try {
            Payment payment = Payment.create(request, options);

            if (payment != null && "success".equalsIgnoreCase(payment.getStatus())) {
                return PaymentResult.success(payment.getPaymentId());
            }

            String reason = payment != null && payment.getErrorMessage() != null
                    ? payment.getErrorMessage()
                    : "Ödeme sağlayıcısından yanıt alınamadı.";
            return PaymentResult.failure(reason);

        } catch (Exception ex) {
            System.out.println("IYZICO ÖDEME HATASI: " + ex.getMessage());
            return PaymentResult.failure("Ödeme işlenirken bir hata oluştu: " + ex.getMessage());
        }
    }
}
