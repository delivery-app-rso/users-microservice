package si.fri.rso.usersmicroservice.models.converters;

import si.fri.rso.usersmicroservice.lib.User;
import si.fri.rso.usersmicroservice.models.entities.UserEntity;

public class UserConverter {

    public static User toDto(UserEntity entity) {

        User dto = new User();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setPassword(entity.getPassword());
        dto.setType(entity.getType());

        return dto;

    }

    public static UserEntity toEntity(User dto) {

        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setType(dto.getType());

        return entity;

    }

}
