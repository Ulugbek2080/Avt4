package ru.netology.rest;

import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebTest {

    @BeforeEach
    void openPage() {
        open("http://0.0.0.0:9999");
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
                .findBy(text("Забронировать"))
                .click();
    }

    private String error(String field) {
        return $("[data-test-id=" + field + "].input_invalid .input__sub")
                .getText()
                .trim();
    }

    @Test
    void shouldSubmitValidForm() {
        submit(
                "Москва",
                "26.08.2026",
                "Иван Петров",
                "+79012345678",
                true
        );

        $("[data-test-id=order-success]")
                .shouldHave(text(
                        "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время."
                ));
    }

    @Test
    void shouldRejectInvalidName() {
        submit(
                "Москва",
                "26.08.2026",
                "Ivan Petrov",
                "+79012345678",
                true
        );

        assertEquals(
                "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                error("name")
        );
    }

    @Test
    void shouldRejectEmptyName() {
        submit(
                "Москва",
                "26.08.2026",
                "",
                "+79012345678",
                true
        );

        assertEquals(
                "Поле обязательно для заполнения",
                error("name")
        );
    }

    @Test
    void shouldRejectInvalidPhone() {
        submit(
                "Москва",
                "26.08.2026",
                "Иван Петров",
                "+7901234567",
                true
        );

        assertEquals(
                "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                error("phone")
        );
    }

    @Test
    void shouldRejectEmptyPhone() {
        submit(
                "Москва",
                "26.08.2026",
                "Иван Петров",
                "",
                true
        );

        assertEquals(
                "Поле обязательно для заполнения",
                error("phone")
        );
    }

    @Test
    void shouldRejectUncheckedAgreement() {
        submit(
                "Москва",
                "26.08.2026",
                "Иван Петров",
                "+79012345678",
                false
        );

        $("[data-test-id=agreement]")
                .shouldHave(cssClass("input_invalid"));
    }
}