package seng302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seng302.Controllers.*;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class Server {

    private static final Server INSTANCE = new Server();

    public final Logger log = LoggerFactory.getLogger(Server.class);

    private DatabaseController databaseController;
    private UserController userController;
    private AuthorizationController authorizationController;
    private AdminController adminController;
    private ClinicianController clinicianController;
    private MedicationsController medicationsController;
    private ProceduresController proceduresController;
    private DiseasesController diseasesController;
    private HistoryController historyController;
    private DonationsController donationsController;
    private WaitingListController waitingListController;
    private CLIController CLIController;
    private CountriesController countriesController;

    private int port = 7015;
    private boolean testing = true;

    private ProfileUtils profileUtils;

    private Server() {}

    public static Server getInstance() {
        return INSTANCE;
    }

    /**
     * Initialises API Routes, and starts the server
     */
    private void initRoutes() {
        path("/api/v1/", () -> {
            before("/*", (request, response) -> log.info(String.format("%s called to (%s) from IP: %s",
                    request.requestMethod(), request.pathInfo(), request.ip())));

            post( "/login",         authorizationController::login);
            post( "/logout",        authorizationController::logout);
            before("/reset",        profileUtils::hasAdminAccess);
            post( "/reset",         databaseController::reset);
            before("/resample",     profileUtils::hasAdminAccess);
            post( "/resample",      databaseController::resample);
            before("/cli",          profileUtils::hasAdminAccess);
            post( "/cli",           CLIController::executeQuery);

            // Path to check connection/version matches client
            get("/hello", (Request request, Response response) -> {
                response.type("application/json");
                response.status(200);
                return "{\"version\": \"1\"}";
            });

            get("/status", databaseController::status);

            path("/admins", () -> {
                before("",          profileUtils::hasAdminAccess);
                get("",             adminController::getAllAdmins);
                post( "",           adminController::addAdmin);
                before("/:id",      profileUtils::checkId);
                get("/:id",         adminController::getAdmin);
                delete("/:id",      adminController::deleteAdmin);
                patch("/:id",       adminController::editAdmin);
            });

            path("/clinicians", () -> {
                get("", (request, response) -> {
                    if (profileUtils.hasAdminAccess(request, response)) {
                        return clinicianController.getAllClinicians(request, response);
                    } else {
                        return response.body();
                    }
                });
                post( "", (request, response) -> {
                    if (profileUtils.hasAdminAccess(request, response)) {
                        return clinicianController.addClinician(request, response);
                    } else {
                        return response.body();
                    }
                });
                before("/:id",      profileUtils::hasClinicianLevelAccess);
                get( "/:id",        clinicianController::getClinician);
                delete( "/:id",     clinicianController::deleteClinician);
                patch( "/:id",      clinicianController::editClinician);
            });

            path("/users", () -> {
                get("", (request, response) -> {
                    if (profileUtils.hasAccessToAllUsers(request, response)) {
                        return userController.getUsers(request, response);
                    } else {
                        return response.body();
                    }
                });
                post( "",          userController::addUser);

                before("/:id",     profileUtils::hasUserLevelAccess);
                get( "/:id",       userController::getUser);
                patch( "/:id",     userController::editUser);
                delete( "/:id",    userController::deleteUser);
                get("/:id/photo",  userController::getUserPhoto);
                patch("/:id/photo",userController::editUserPhoto);
                delete("/:id/photo",userController::deleteUserPhoto);

                path("/:id/medications", () -> {
                    before("",                  profileUtils::hasUserLevelAccess);
                    get("",                     medicationsController::getAllMedications);
                    post("",                    medicationsController::addMedication);
                    get("/:medicationId",       medicationsController::getSingleMedication);
                    patch("/:medicationId",     medicationsController::editMedication);
                    delete("/:medicationId",    medicationsController::deleteMedication);
                });

                path("/:id/diseases", () -> {
                    before("",                  profileUtils::hasUserLevelAccess);
                    get("",                     diseasesController::getAllDiseases);
                    post("",                    diseasesController::addDisease);
                    get("/:diseaseId",          diseasesController::getSingleDisease);
                    patch("/:diseaseId",        diseasesController::editDisease);
                    delete("/:diseaseId",       diseasesController::deleteDisease);
                });

                path("/:id/procedures", () -> {
                    before("",                  profileUtils::hasUserLevelAccess);
                    get("",                     proceduresController::getAllProcedures);
                    post("",                    proceduresController::addProcedure);
                    get("/:procedureId",        proceduresController::getSingleProcedure);
                    patch("/:procedureId",      proceduresController::editProcedure);
                    delete("/:procedureId",     proceduresController::deleteProcedure);
                });

                path("/:id/history", () -> {
                   before("",                   profileUtils::hasUserLevelAccess);
                   get("",                      historyController::getUserHistoryItems);
                   post("",                     historyController::addUserHistoryItem);
                });

                path("/:id/donations", () -> {
                    before("",                  profileUtils::hasUserLevelAccess);
                    get("",                     donationsController::getAllUserDonations);
                    post("",                    donationsController::addDonation);
                    delete("",                  donationsController::deleteAllUserDonations);
                    get("/:donationListItemName", donationsController::getSingleUserDonationItem);
                    delete("/:donationListItemName", donationsController::deleteUserDonationItem);
                });

                path("/:id/waitingListItems", () -> {
                    before("",                  profileUtils::hasUserLevelAccess);
                    get("",                     waitingListController::getAllUserWaitingListItems);
                    post("",                    waitingListController::addNewUserWaitingListItem);
                    get("/:waitingListItemId",  waitingListController::getSingleUserWaitingListItem);
                    patch("/:waitingListItemId", waitingListController::editWaitingListItem);
                    delete("/:waitingListItemId", waitingListController::deleteWaitingListItem);
                });
            });

            path("/donations", () -> {
                before("", profileUtils::hasAccessToAllUsers);
                get("",  donationsController::getAllDonations);
            });

            path("/waitingListItems", () -> {
                before("", profileUtils::hasAccessToAllUsers);
                get("",  waitingListController::getAllWaitingListItems);
            });

            path("/usercount", () -> {
                before("",   profileUtils::hasAccessToAllUsers);
                get("",      userController::countUsers);
            });

            path("/countries", () -> {
                before("", profileUtils::hasAccessToAllUsers);
                get("", countriesController::getCountries);
                patch("", countriesController::patchCountries);
            });

            path("/unique", () -> {
                get("",    profileUtils::isUniqueIdentifier);
            });
        });
    }

    private void start() {
        initConfig();
        initRoutes();
    }

    public static void main(String[] args) {
        INSTANCE.testing = false;
        List<String> argz = Arrays.asList(args);
        if(argz.size() > 0){
            try{
                if(argz.contains("-t")){
                    INSTANCE.testing = true;
                }
                INSTANCE.port = Integer.parseInt(argz.get(0));
            }
            catch (Exception ignored){

            }
        }
        INSTANCE.start();
    }

    /**
     * Returns whether or not the program is under test
     * @return whether or not the program is under test
     */
    public boolean isTesting(){
        return testing;
    }

    /**
     * Initialises server config + controllers
     */
    private void initConfig() {
        port(port);
        databaseController = new DatabaseController();
        userController = new UserController();
        authorizationController = new AuthorizationController();
        adminController = new AdminController();
        clinicianController = new ClinicianController();
        medicationsController = new MedicationsController();
        proceduresController = new ProceduresController();
        diseasesController = new DiseasesController();
        donationsController = new DonationsController();
        historyController = new HistoryController();
        waitingListController = new WaitingListController();
        profileUtils = new ProfileUtils();
        CLIController = new CLIController();
        countriesController = new CountriesController();
    }
}
