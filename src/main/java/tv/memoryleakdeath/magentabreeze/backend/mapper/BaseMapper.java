package tv.memoryleakdeath.magentabreeze.backend.mapper;

public class BaseMapper {

    protected <E extends Enum<E>> E getEnumTypeFromString(String type, Class<E> enumClass) {
        return Enum.valueOf(enumClass, type);
    }
}
