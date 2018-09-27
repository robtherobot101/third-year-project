package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.GeneralHospital;
import seng302.Model.Hospital;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.List;

public class HospitalController {

    private GeneralHospital model;

    /**
     * Creates a new hospital view controller.
     */
    public HospitalController() {model = new GeneralHospital();}


    /**
     * Returns all hospitals stored in the database.
     *
     * @param request the request body.
     * @param response the response body.
     * @return a list of hospital objects.
     */
    public Object getHospitals(Request request, Response response) {
        List<Hospital> hospitals;
        try {
            hospitals = model.getHospitals();
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialHospitals = gson.toJson(hospitals);

        response.type("application/json");
        response.status(200);
        return serialHospitals;
    }
}
