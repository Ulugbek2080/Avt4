package ru.netology.rest;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
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

    private void submit(String city, String date, String name, String phone, boolean agree) {
        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        if (agree) {
            $("[data-test-id=agreement]").click();
        }
        $$("button").findBy(text("Запланировать")).click();
    }

    private SelenideElement error(String field) {
        return $("[data-test-id=" + field + "].input_invalid .input__sub");
    }

    @Test
    void shouldSubmitValidForm() {
        String meetingDate = date(3);
        submit("Москва", meetingDate, "Иван Петров", "+79012345678", true);
        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + meetingDate));
    }

    @Test
    void shouldRejectInvalidName() {
        submit("Москва", date(3), "Ivan Petrov", "+79012345678", true);
        error("name").shouldBe(visible).shouldHave(
                exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldRejectEmptyName() {
        submit("Москва", date(3), "", "+79012345678", true);
        error("name").shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRejectInvalidPhone() {
        submit("Москва", date(3), "Иван Петров", "+790127", true);
        error("phone").shouldBe(visible).shouldHave(
                exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldRejectEmptyPhone() {
        submit("Москва", date(3), "Иван Петров", "", true);
        error("phone").shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRejectUncheckedAgreement() {
        submit("Москва", date(3), "Иван Петров", "+79012345678", false);
        $("[data-test-id=agreement].input_invalid").shouldBe(visible);
    }
}