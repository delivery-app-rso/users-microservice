package si.fri.rso.usersmicroservice.graphql;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.lib.UserLoginDto;
import si.fri.rso.usersmicroservice.services.beans.UserBean;

@GraphQLClass
@ApplicationScoped
public class UsersQueries {
    @Inject
    private UserBean userBean;

    @GraphQLQuery
    public PaginationWrapper<User> getAllUsers(@GraphQLArgument(name = "pagination") Pagination pagination,
            @GraphQLArgument(name = "sort") Sort sort,
            @GraphQLArgument(name = "filter") Filter filter) {

        return GraphQLUtils.process(userBean.getUsers(), pagination, sort, filter);
    }

    @GraphQLQuery
    public User getUser(@GraphQLArgument(name = "userId") Integer userId) {
        return userBean.getUser(userId);
    }

    @GraphQLQuery
    public User getRandomDeliverer() {
        return userBean.getRandomDeliverer();
    }

    @GraphQLQuery
    public User loginUser(@GraphQLArgument(name = "user") UserLoginDto userLoginDto) {
        return userBean.login(userLoginDto);
    }
}
