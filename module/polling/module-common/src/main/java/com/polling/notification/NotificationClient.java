package com.polling.notification;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationClient {

    @Value("${sms.serviceid}")
    private String serviceId;
    @Value("${sms.accesskey}")
    private String accessKey;
    @Value("${sms.secretkey}")
    private String secretKey;

    private WebClient webClient = WebClient.builder().build();

    private final String SEND_MESSAGE_URL = "/sms/v2/services/" + this.serviceId + "/messages";
    private static final String SENDER = "01096121458";
//
//    public Mono<NotificationSmsResponse> sendSms(SendSMSRequestDto requestDto) {
//        long time = System.currentTimeMillis();
//        List<SendSMSRequestDto> messages = new ArrayList<>();
//        messages.add(requestDto);
//        SendSMSApiRequestDto smsRequest = new SendSMSApiRequestDto("SMS", "COMM", "82", "01065752938", "테스트", messages);
//
//        try {
//            String sig = makeSignature(time);
//            return webClient.post()
//                    .uri("https://sens.apigw.ntruss.com/sms/v2/services/{serviceId}/messages", serviceId)
//                    .headers(headers -> {
//                        headers.setContentType(MediaType.APPLICATION_JSON);
//                        headers.set("x-ncp-apigw-timestamp", Long.toString(time));
//                        headers.set("x-ncp-iam-access-key", accessKey);
//                        headers.set("x-ncp-apigw-signature-v2", sig);
//                    })
//                    .body(BodyInserters.fromValue(smsRequest))
//                    .retrieve()
//                    .bodyToMono(NotificationSmsResponse.class).log()
//                    .doOnError(WebClientResponseException.class, throwable -> log.error("[SMS] message = {}", throwable.getResponseBodyAsString(), throwable))
//                    .timeout(timeout);
//
//        } catch (Exception e) {
//            throw new CustomException(CustomErrorResult.FAIL_SEND_SMS);
//        }
//    }


    public NotificationSmsResponse sendSMS(SendSMSRequestDto requestDto) {
        List<SendSMSRequestDto> messages = new ArrayList<>();
        messages.add(requestDto);
        Long time = System.currentTimeMillis();
        SendSMSApiRequestDto smsRequest = new SendSMSApiRequestDto("SMS", "COMM", "82", "SENDER", "테스트", messages);
        try {
            String sig = makeSignature(time);

            System.out.println("SEND_MESSAGE_URL: " + SEND_MESSAGE_URL);
            System.out.println("Time: " + time);
            System.out.println("AccessKey: " + this.accessKey);
            System.out.println("Signature: " + sig);

            return webClient.post()
                    .uri("https://sens.apigw.ntruss.com" + "/sms/v2/services/" + this.serviceId + "/messages")
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("x-ncp-apigw-timestamp", Long.toString(time));
                        headers.set("x-ncp-iam-access-key", this.accessKey);
                        headers.set("x-ncp-apigw-signature-v2", sig);
                    })
                    .body(BodyInserters.fromValue(smsRequest))
                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new IllegalArgumentException("서버 내부 인증 실패")))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new IllegalArgumentException("우리의 실패")))
                    .bodyToMono(NotificationSmsResponse.class)
                    .block();
        }
        catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("SMS 전송 실패 {}", e.getCause());
        }
        //catch (Exception e) { throw new CustomException(CustomErrorResult.FAIL_SEND_SMS); }
    }



    private String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = method +
                space +
                url +
                newLine +
                timestamp +
                newLine +
                accessKey;

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(rawHmac);
    }

    

}
