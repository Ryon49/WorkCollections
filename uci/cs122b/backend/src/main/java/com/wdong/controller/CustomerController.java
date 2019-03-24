package com.wdong.controller;

import com.wdong.config.GoogleCaptchaSettings;
import com.wdong.model.CreditCard;
import com.wdong.model.Response;
import com.wdong.model.Customer;
import com.wdong.model.response.GoogleCaptchaResponse;
import com.wdong.model.wrapper.CreditCheckWrapper;
import com.wdong.model.wrapper.LoginWrapper;
import com.wdong.repository.CreditCardsRepository;
import com.wdong.repository.CustomersRepository;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
public class CustomerController {


    @SuppressWarnings("Duplicates")
    @PostMapping(value = "api/user/login")
    public @ResponseBody
    Response login(@RequestParam(name = "email") String email,
                      @RequestParam(name = "password") String password,
                      @RequestParam(name = "recaptchaToken") Optional<String> recaptchaToken) {

        // validate recaptcha
        if (recaptchaToken.isPresent()) {
            RestTemplate restTemplate = new RestTemplate();
            GoogleCaptchaResponse resp = restTemplate.getForObject(captcha.getUrl(recaptchaToken.get()), GoogleCaptchaResponse.class);

            if (resp == null || !resp.success()) {
                return Response.error("Recaptcha validation failed");
            }
        }

        Customer customer = customersRepository.findByEmail(email);

        if (customer != null) {
            //noinspection Duplicates
            try {
                boolean success = new StrongPasswordEncryptor().checkPassword(password, customer.getPassword());
                if (!success) {
                    return Response.error("Incorrect password");
                } else {
                    return Response.ok(new LoginWrapper(customer));
                }
            } catch (EncryptionOperationNotPossibleException e) {
                return Response.error("Password unencrypted");
            }
        }

        return Response.error("Username not found");
    }

    @PostMapping(value = "api/user/credit")
    public @ResponseBody
    Response checkCredit(@RequestParam(name = "cardNumber") String cardNumber,
                         @RequestParam(name = "firstName") String firstName,
                         @RequestParam(name = "lastName") String lastName,
                         @RequestParam(name = "exprYear") int year,
                         @RequestParam(name = "exprMonth") int month) {


        Optional<CreditCard> cardOptional = cardsRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        if (!cardOptional.isPresent()) {
            return Response.error(new CreditCheckWrapper(1));
        }
        CreditCard card = cardOptional.get();
        if (!card.getId().equals(cardNumber)) {
            return Response.error(new CreditCheckWrapper(2));
        }
        int card_year = card.getExpiration().toLocalDate().getYear();
        int card_month = card.getExpiration().toLocalDate().getMonth().getValue();
        if (card_year != year || card_month != month) {
            return Response.error(new CreditCheckWrapper(3));
        }

        return Response.ok();
    }

    @GetMapping(value = "api/user/test")
    public @ResponseBody String test() {
        return captcha.getBaseUrl();
    }


    // region repository
    private final CustomersRepository customersRepository;

    private final CreditCardsRepository cardsRepository;
    // endregion

    private final GoogleCaptchaSettings captcha;

    // region autowired
    @Autowired
    public CustomerController(CustomersRepository customersRepository, CreditCardsRepository cardsRepository, GoogleCaptchaSettings captcha) {
        this.customersRepository = customersRepository;
        this.cardsRepository = cardsRepository;
        this.captcha = captcha;
    }
    // endregion
}
