package si.fri.rso.usersmicroservice.graphql;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.services.beans.UserBean;

@GraphQLClass
@ApplicationScoped
public class UsersMutations {
    @Inject
    private UserBean userBean;

    @GraphQLMutation
    public User registerUser(@GraphQLArgument(name = "user") User user) {
        return userBean.register(user);
    }

    @GraphQLMutation
    public User updateUser(@GraphQLArgument(name = "userId") Integer userId,
            @GraphQLArgument(name = "user") User user) {
        return userBean.putUser(userId, user);
    }

    @GraphQLMutation
    public DeleteResponse deleteUser(@GraphQLArgument(name = "userId") Integer userId) {
        return new DeleteResponse(userBean.deleteUser(userId));
    }
}