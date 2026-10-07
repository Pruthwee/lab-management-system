package com.lab.management.exception;

public class EntityNotFoundException extends LabException {

    private String entityType;
    private String entityId;

    public EntityNotFoundException(String entityType, String entityId) {
        super("ENTITY_NOT_FOUND", entityType + " with id " + entityId + " not found");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityType, Long entityId) {
        this(entityType, String.valueOf(entityId));
    }

    public EntityNotFoundException(String message) {
        super("ENTITY_NOT_FOUND", message);
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
