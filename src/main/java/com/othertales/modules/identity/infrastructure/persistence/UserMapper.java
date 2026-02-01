package com.othertales.modules.identity.infrastructure.persistence;

import com.othertales.modules.identity.domain.PlanType;
import com.othertales.modules.identity.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        var entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFullName(user.getFullName());
        entity.setPlanType(toEntityPlanType(user.getPlanType()));
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setVersion(user.getVersion());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFullName(),
                toDomainPlanType(entity.getPlanType()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private UserEntity.PlanTypeEntity toEntityPlanType(PlanType planType) {
        return switch (planType) {
            case FREE -> UserEntity.PlanTypeEntity.FREE;
            case PRO -> UserEntity.PlanTypeEntity.PRO;
        };
    }

    private PlanType toDomainPlanType(UserEntity.PlanTypeEntity planType) {
        return switch (planType) {
            case FREE -> PlanType.FREE;
            case PRO -> PlanType.PRO;
        };
    }
}
