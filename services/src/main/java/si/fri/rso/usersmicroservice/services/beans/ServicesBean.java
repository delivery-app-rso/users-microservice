package si.fri.rso.usersmicroservice.services.beans;

import java.util.HashMap;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.lib.UserMailDto;
import si.fri.rso.usersmicroservice.services.clients.MailProcessApi;
import si.fri.rso.usersmicroservice.services.config.ServicesProperties;

@ApplicationScoped
public class ServicesBean {
    @Inject
    ServicesProperties servicesProperties;

    @Inject
    @RestClient
    private MailProcessApi mailProcessApi;

    public void sendRegistrationSuccessEmail(User user) {
        HashMap<String, String> userData = new HashMap<>();

        userData.put("name", user.getName());
        userData.put("email", user.getEmail());

        CompletionStage<String> stringCompletionStage = mailProcessApi
                .sendMailAsynch(new UserMailDto("registration", userData));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println("Finished sending mail: " + s));
    }
}
