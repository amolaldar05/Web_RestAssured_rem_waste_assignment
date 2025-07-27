package org.rem_waste.utils;
import org.testng.annotations.DataProvider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoginDataProvider {

    @DataProvider(name = "loginData")
    public static Iterator<Object[]> provideLoginData() {
        List<Map<String, String>> testData = JsonReader.getTestData("loginData.json");
        return testData.stream().map(data -> new Object[]{data}).iterator();
    }
}

