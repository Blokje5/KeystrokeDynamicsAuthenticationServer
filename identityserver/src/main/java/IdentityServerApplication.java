import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import data.controller.DataCollectionController;
import data.controller.UserTemplateController;
import exceptions.InvalidRequestException;
import exceptions.utils.ExceptionResponse;
import exceptions.utils.ExceptionUtils;
import identity.login.controller.LoginController;
import identity.registration.controller.UserController;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.options;
import static spark.Spark.post;

public class IdentityServerApplication {

    public static void main(String[] args) {

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "{ \"message\": \"OK\" }";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        Injector injector = Guice.createInjector(new ApplicationModule());
        UserController userController = injector.getInstance(UserController.class);
        LoginController loginController = injector.getInstance(LoginController.class);
        DataCollectionController dataCollectionController =
                injector.getInstance(DataCollectionController.class);
        UserTemplateController userTemplateController = injector.getInstance(UserTemplateController.class);

        post("/registration", userController.handleRegistrationRequest::handle);
        post("/login", loginController.handlePostLogin::handle);
        post("/events", dataCollectionController.dataCollectionRoute);
        post("/session", dataCollectionController.sessionCollectionRoute);
        post("/user-template", userTemplateController.userTemplateRoute);

        exception(InvalidRequestException.class, (exception, request, response) -> {
            ExceptionResponse exceptionResponse = ExceptionUtils.exceptionResponse(exception);
            response.status(exceptionResponse.getStatus());
            response.body(new Gson().toJson(exceptionResponse));
        });

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
            response.status(500);
            response.body( "{ \"message\": \"" + exception.getMessage() + "\" }");
        });
    }
}
