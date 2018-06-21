package seng302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seng302.Controllers.*;

import static spark.Spark.*;

public class Server {

    public static final Logger log = LoggerFactory.getLogger(Server.class);

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

    private ProfileUtils profileUtils;

    /**
     * Initialises API Routes, and starts the server
     */
    private void initRoutes() {
        path("/api/v1/", () -> {
            before("/*", (request, response) -> log.info(String.format("%s called to (%s) from IP: %s",
                    request.requestMethod(), request.pathInfo(), request.ip())));

            post( "/login",         authorizationController::login);
            post( "/logout",        authorizationController::logout);
            post( "/reset",         databaseController::reset);
            post( "/resample",      databaseController::resample);

            // TODO discuss where cache is stored
            /*post( "/clearCache",   Server::stubMethod);*/

            path("/admins", () -> {
                get("",             adminController::getAllAdmins);
                post( "",           adminController::addAdmin);
                before("/:id",      profileUtils::checkId);
                get("/:id",         adminController::getAdmin);
                delete("/:id",      adminController::deleteAdmin);
                patch("/:id",       adminController::editAdmin);
            });

            path("/clinicians", () -> {
                get("",             clinicianController::getAllClinicians);
                post( "",           clinicianController::addClinician);
                before("/:id",      profileUtils::checkId);
                get( "/:id",        clinicianController::getClinician);
                delete( "/:id",     clinicianController::deleteClinician);
                patch( "/:id",      clinicianController::editClinician);
            });

            path("/users", () -> {
                get("",            userController::getAllUsers);
                post( "",          userController::addUser);
                before("/:id",     profileUtils::checkId);
                get( "/:id",       userController::getUser);
                patch( "/:id",     userController::editUser);
                delete( "/:id",    userController::deleteUser);

                path("/:id/medications", () -> {
                    before("",                  profileUtils::checkId);
                    get("",                     medicationsController::getAllMedications);
                    post("",                    medicationsController::addMedication);
                    get("/:medicationId",       medicationsController::getSingleMedication);
                    patch("/:medicationId",     medicationsController::editMedication);
                    delete("/:medicationId",    medicationsController::deleteMedication);
                });

                path("/:id/diseases", () -> {
                    before("",                  profileUtils::checkId);
                    get("",                     diseasesController::getAllDiseases);
                    post("",                    diseasesController::addDisease);
                    get("/:diseaseId",          diseasesController::getSingleDisease);
                    patch("/:diseaseId",        diseasesController::editDisease);
                    delete("/:diseaseId",       diseasesController::deleteDisease);
                });

                path("/:id/procedures", () -> {
                    before("",                  profileUtils::checkId);
                    get("",                     proceduresController::getAllProcedures);
                    post("",                    proceduresController::addProcedure);
                    get("/:procedureId",        proceduresController::getSingleProcedure);
                    patch("/:procedureId",      proceduresController::editProcedure);
                    delete("/:procedureId",     proceduresController::deleteProcedure);
                });

                path("/:id/history", () -> {
                   before("",                   profileUtils::checkId);
                   get("",                      historyController::getUserHistoryItems);
                   post("",                     historyController::addUserHistoryItem);
                });

                path("/:id/donations", () -> {
                    before("",                  profileUtils::checkId);
                    get("",                     donationsController::getAllUserDonations);
                    post("",                    donationsController::addDonation);
                    get("/:donationListItemName", donationsController::getSingleUserDonationItem);
                    delete("/:donationListItemName", donationsController::deleteUserDonationItem);


                });

                path("/:id/waitingListItems", () -> {
                    before("",                  profileUtils::checkId);
                    get("",                     waitingListController::getAllUserWaitingListItems);
                    post("",                    waitingListController::addNewUserWaitingListItem);
                    get("/:waitingListItemId",  waitingListController::getSingleUserWaitingListItem);
                    patch("/:waitingListItemId", waitingListController::editWaitingListItem);
                    delete("/:waitingListItemId", waitingListController::deleteWaitingListItem);
                });
            });

            path("/donations", () -> {
                get("",  donationsController::getAllDonations);
            });

            path("/waitingListItems", () -> {
                get("",  waitingListController::getAllWaitingListItems);
            });
        });
    }

    private void start() {
        initConfig();
        initRoutes();
    }

    public static void main(String[] args) {
        Server apiServer = new Server();
        apiServer.start();
    }

    /**
     * Initialises server config + controllers
     */
    private void initConfig() {
        port(6976);
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
    }
}