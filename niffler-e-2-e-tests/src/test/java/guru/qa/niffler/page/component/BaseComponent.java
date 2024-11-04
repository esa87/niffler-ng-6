package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;

public abstract class BaseComponent<T extends BaseComponent<?>> {
    protected final SelenideElement self;

    public BaseComponent(SelenideElement self){
        this.self = self;
    }
}
