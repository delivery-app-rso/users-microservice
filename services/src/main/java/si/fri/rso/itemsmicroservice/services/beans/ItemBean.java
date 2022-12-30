package si.fri.rso.itemsmicroservice.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.metrics.annotation.Timed;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.rso.itemsmicroservice.lib.Item;
import si.fri.rso.itemsmicroservice.models.converters.ItemConverter;
import si.fri.rso.itemsmicroservice.models.entities.ItemEntity;

@RequestScoped
public class ItemBean {

    private Logger log = Logger.getLogger(ItemBean.class.getName());

    @Inject
    private EntityManager em;

    @Timed
    public List<Item> getItem() {

        TypedQuery<ItemEntity> query = em.createNamedQuery(
                "ItemEntity.getAll", ItemEntity.class);

        List<ItemEntity> resultList = query.getResultList();

        return resultList.stream().map(ItemConverter::toDto).collect(Collectors.toList());

    }

    public List<Item> getItemFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ItemEntity.class, queryParameters).stream()
                .map(ItemConverter::toDto).collect(Collectors.toList());
    }

    public Item getItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);

        if (itemEntity == null) {
            throw new NotFoundException();
        }

        Item item = ItemConverter.toDto(itemEntity);

        return item;
    }

    public Item createItem(Item item) {

        ItemEntity itemEntity = ItemConverter.toEntity(item);

        try {
            beginTx();
            em.persist(itemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (itemEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ItemConverter.toDto(itemEntity);
    }

    public Item putItem(Integer id, Item item) {

        ItemEntity c = em.find(ItemEntity.class, id);

        if (c == null) {
            return null;
        }

        ItemEntity updatedItemEntity = ItemConverter.toEntity(item);

        try {
            beginTx();
            updatedItemEntity.setId(c.getId());
            updatedItemEntity = em.merge(updatedItemEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return ItemConverter.toDto(updatedItemEntity);
    }

    public boolean deleteItem(Integer id) {

        ItemEntity itemEntity = em.find(ItemEntity.class, id);

        if (itemEntity != null) {
            try {
                beginTx();
                em.remove(itemEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
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
