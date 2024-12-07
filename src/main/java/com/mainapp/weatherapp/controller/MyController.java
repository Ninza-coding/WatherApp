package com.mainapp.weatherapp.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class MyController {

    @Value("${weather.api.key}")
    private String apiKey;

    @GetMapping(path = {"/weatherapp", "", "/"})
    public String doGet() {
        return "index";  // this refers to index.html in the templates folder
    }

    @PostMapping("/getWeather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        // Build the API URL
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

        // Use RestTemplate to get the weather data
        RestTemplate restTemplate = new RestTemplate();
        String responseContent = restTemplate.getForObject(apiUrl, String.class);

        // Parse the JSON response
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent, JsonObject.class);

        // Date and Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;  // Convert to milliseconds
        String date = new Date(dateTimestamp).toString();

        // Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);

        // Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

        // Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

        // Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

        // Add the data to the model
        model.addAttribute("city", city);
        model.addAttribute("date", date);
        model.addAttribute("temperature", temperatureCelsius);
        model.addAttribute("weatherCondition", weatherCondition);
        model.addAttribute("humidity", humidity);
        model.addAttribute("windSpeed", windSpeed);

        return "index"; // returns the same form but with weather data
    }
}
