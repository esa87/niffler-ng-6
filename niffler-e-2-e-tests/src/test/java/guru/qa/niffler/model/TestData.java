package guru.qa.niffler.model;

import java.util.List;

public record TestData(String password, List<CategoryJson> categoryJsonList, List<SpendJson> spendJsonList) {
}