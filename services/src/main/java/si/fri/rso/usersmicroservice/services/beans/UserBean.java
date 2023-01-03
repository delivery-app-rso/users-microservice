package si.fri.rso.usersmicroservice.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.management.RuntimeErrorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.DatatypeConverter;

import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.lib.UserLoginDto;
import si.fri.rso.usersmicroservice.models.converters.UserConverter;
import si.fri.rso.usersmicroservice.models.entities.UserEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

@RequestScoped
public class UserBean {

    private Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private ServicesBean servicesBean;

    @Inject
    private EntityManager em;

    public List<User> getUsers() {

        TypedQuery<UserEntity> query = em.createNamedQuery(
                "UserEntity.getAll", UserEntity.class);

        List<UserEntity> resultList = query.getResultList();

        return resultList.stream().map(UserConverter::toDto).collect(Collectors.toList());
    }

    @Timed
    public List<User> getUserFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, UserEntity.class, queryParameters).stream()
                .map(UserConverter::toDto).collect(Collectors.toList());
    }

    public User getUser(Integer userId) {
        UserEntity userEntity = em.find(UserEntity.class, userId);
        return UserConverter.toDto(userEntity);
    }

    public User register(User user) {
        UserEntity userEntity = UserConverter.toEntity(user);
        userEntity.setPassword(this.hashPasword(user.getPassword()));

        if (userEntity.getType() == null) {
            userEntity.setType(0);
        }

        try {
            beginTx();
            em.persist(userEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (userEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        user = UserConverter.toDto(userEntity);

        servicesBean.sendRegistrationSuccessEmail(user);

        return user;
    }

    public User login(UserLoginDto userLoginDto) {
        User user = this.getUserByEmail(userLoginDto.getEmail());

        if (user == null) {
            return null;
        }

        boolean auth = this.authUser(user, userLoginDto.getPassword());
        return auth ? user : null;
    }

    public User putUser(Integer userId, User user) {
        UserEntity c = em.find(UserEntity.class, userId);

        if (c == null) {
            return null;
        }

        UserEntity updatedUserEntity = UserConverter.toEntity(user);

        try {
            beginTx();
            updatedUserEntity.setId(c.getId());
            updatedUserEntity = em.merge(updatedUserEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return UserConverter.toDto(updatedUserEntity);
    }

    public boolean deleteUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);

        if (userEntity != null) {
            try {
                beginTx();
                em.remove(userEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    private User getUserByEmail(String email) {
        List<UserEntity> userEntity = (List<UserEntity>) em
                .createQuery("SELECT u FROM UserEntity u WHERE u.email=:email")
                .setParameter("email", email).getResultList();

        if (userEntity.size() == 0) {
            return null;
        }

        return UserConverter.toDto(userEntity.get(0));
    }

    private String hashPasword(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());
            byte[] digest = md.digest();

            String hashedPassword = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();

            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to generate password");
    }

    private boolean authUser(User user, String password) {
        return user.getPassword().equals(this.hashPasword(password));
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
