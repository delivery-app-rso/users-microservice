package si.fri.rso.itemsmicroservice.models.converters;

import si.fri.rso.itemsmicroservice.lib.Item;
import si.fri.rso.itemsmicroservice.models.entities.ItemEntity;

public class ItemConverter {

    public static Item toDto(ItemEntity entity) {

        Item dto = new Item();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());

        return dto;

    }

    public static ItemEntity toEntity(Item dto) {

        ItemEntity entity = new ItemEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());

        return entity;

    }

}
