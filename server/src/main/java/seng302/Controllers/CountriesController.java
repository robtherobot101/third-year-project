package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.GeneralCountries;
import seng302.Model.Country;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.util.ArrayList;

public class CountriesController {

    private GeneralCountries model;

    public CountriesController() {
        model = new GeneralCountries();
    }

    public String getCountries(Request request, Response response){
        ArrayList<Country> queriedCountries;
        try {
            queriedCountries = model.getCountries();
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedCountries = gson.toJson(queriedCountries);

        response.type("application/json");
        response.status(200);
        return serialQueriedCountries;
    }
}
