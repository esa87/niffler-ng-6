package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class FriendsTable {
    private final SelenideElement self = $("table[aria-labelledby='tableTitle']");
}
