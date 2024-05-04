package tv.memoryleakdeath.magentabreeze.common;

import java.util.List;

public abstract class BaseServiceSpecificValues {
    private List<ServiceTypes> service;
    private String value;

    protected String getValue(ServiceTypes targetService) {
        if (service.contains(targetService)) {
            return value;
        }
        return null;
    }

    protected String getValue() {
        return this.value;
    }

    protected void setValue(String value) {
        this.value = value;
    }

    protected void setService(ServiceTypes type) {
        this.service = List.of(type);
    }

    protected void setService(List<ServiceTypes> types) {
        this.service = types;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
