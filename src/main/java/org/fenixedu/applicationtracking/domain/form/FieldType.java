package org.fenixedu.applicationtracking.domain.form;

import java.util.Arrays;
import java.util.List;

public enum FieldType {

    TEXT("text"), PASSWORD("password"), SELECT("select"), TEXTAREA("textarea"), RADIO_GROUP("radio-group"), CHECKBOX_GROUP(
            "checkbox-group"), FILE("file"), SUBFORM("subform"), DATE("date"), TIME("time"), DATETIME("datetime"), AVATAR("avatar"), COUNTRY("country");

    private String type;

    private FieldType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static FieldType fromType(String type) {
        List<FieldType> list = Arrays.asList(FieldType.values());
        return list.stream().filter(m -> m.type.equals(type)).findAny().orElse(null);
    }

}
