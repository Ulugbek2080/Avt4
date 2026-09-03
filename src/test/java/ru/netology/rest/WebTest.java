package ru.netology.rest;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
public class WebTest {

    @BeforeEach
    void openPage() {
        open("http://localhost:9999");
    }
    private String date(int plusDays) {
        return LocalDate.now().plusDays(plusDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    private void submit(String city, String date, String name,
                        String phone, boolean agree) {

        $("[data-test-id=city] input")
                .setValue(city);

        $("[data-test-id=date] input")
                .setValue(date);

        $("[data-test-id=name] input")
                .setValue(name);

        $("[data-test-id=phone] input")
                .setValue(phone);

        if (agree) {
            $("[data-test-id=agreement]")
                    .click();
        }

        $$("button")
                .findBy(text("Запланировать"))
                .click();
    }

    private SelenideElement error(String field) {
        return $("[data-test-id=" + field + "] .input__sub");

    }

    @Test
    void shouldRejectInvalidName() {
        submit("Москва", date(3), "Ivan Petrov", "+79012345678", true);
        $("[data-test-id=name]").shouldHave(cssClass("input_invalid"));
        error("name").shouldHave(
                exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldRejectEmptyName() {
        submit("Москва", date(3), "", "+79012345678", true);
        $("[data-test-id=name]").shouldHave(cssClass("input_invalid"));
        error("name").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRejectInvalidPhone() {
        submit("Москва", date(3), "Иван Петров", "+7901234567", true);
        $("[data-test-id=phone]").shouldHave(cssClass("input_invalid"));
        error("phone").shouldHave(
                text("Телефон указан неверно. Должно быть 11 цифр"));
    }

    @Test
    void shouldRejectEmptyPhone() {
        submit("Москва", date(3), "Иван Петров", "", true);
        $("[data-test-id=phone]").shouldHave(cssClass("input_invalid"));
        error("phone").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRejectUncheckedAgreement() {
        submit("Москва", date(3), "Иван Петров", "+79012345678", false);
        $("[data-test-id=agreement]").shouldHave(cssClass("input_invalid"));
        $("[data-test-id=order-success]").shouldNotBe(visible);
    }

    @Test
    void shouldSubmitValidForm() {
        submit("Москва", date(3), "Иван Петров", "+79012345699", true);
        $("[data-test-id=order-success]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + date(3)));
    }
}