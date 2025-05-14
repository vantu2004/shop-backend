package com.vantu.shop_backend.controller;

import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.response.PaymentResponse;
import com.vantu.shop_backend.response.TransactionResponse;
import com.vantu.shop_backend.security.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("${api.prefix}/payment")
public class PaymentController {

    @GetMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam BigDecimal total) throws UnsupportedEncodingException {

        //Thêm các fields (https://sandbox.vnpayment.vn/apis/docs/chuyen-doi-thuat-toan/changeTypeHash.html)

        BigDecimal exchangeRate = new BigDecimal("2200000"); //Đổi sang VND, hai số 0 cuối cho phần thập phân

        BigDecimal amount = total.multiply(exchangeRate).setScale(0, BigDecimal.ROUND_HALF_UP);

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        //vnp_Params.put("vnp_BankCode", VNPayConfig.vnp_BankCode);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_Amount", amount.toPlainString());
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpDate);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("Ok");
        paymentResponse.setMessage("Success!");


        // Code VNPay cung cấp để mã hóa các fields
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }



        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String payment_url = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        paymentResponse.setUrl(payment_url);


        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);

    }

    @GetMapping("/payment-info")
    public ResponseEntity<ApiResponse> paymentInfo(
            @RequestParam(value="vnp_Amount") String amount,
            @RequestParam(value="vnp_ResponseCode") String responseCode,
            @RequestParam(value="vnp_BankCode") String bankCode,
            @RequestParam(value="vnp_CardType") String cardType)
    {
        if(responseCode.equals("00")){
            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setStatus("Ok");
            transactionResponse.setAmount(amount);
            transactionResponse.setBankCode(bankCode);
            transactionResponse.setCardType(cardType);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Success!", transactionResponse));

        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Payment Failed!", null));
        }

    }
}
