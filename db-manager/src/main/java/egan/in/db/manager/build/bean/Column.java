package egan.in.db.manager.build.bean;

/**
 * 存储列的信息
 * @author  egan
 * @email egzosn@gmail.com
 * @date 2016-6-23 14:51:17
 */
public class Column {
    private String field;
    private boolean isPrimary;
    private boolean isAutoincrement;
    private boolean isNullable;
    private String type;
    private String comment;
    private int ordinalPosition;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAutoincrement() {
        return isAutoincrement;
    }

    public void setAutoincrement(boolean isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public Column() {
        isPrimary = false;
    }

    public Column(String field, String type, String comment) {
        this.field = field;
        this.type = type;
        this.comment = comment;
    }

    public Column(String field, String type) {
        this.field = field;
        this.type = type;
    }

    public Column(String field, boolean isPrimary, boolean isAutoincrement, boolean isNullable, String comment, int ordinalPosition) {
        this.field = field;
        this.isPrimary = isPrimary;
        this.isAutoincrement = isAutoincrement;
        this.isNullable = isNullable;
        this.comment = comment;
        this.ordinalPosition = ordinalPosition;
    }

    @Override
    public String toString() {
        return "Column{" +
                "field='" + field + '\'' +
                ", isPrimary=" + isPrimary +
                ", isAutoincrement=" + isAutoincrement +
                ", isNullable=" + isNullable +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", ordinalPosition=" + ordinalPosition +
                '}';
    }
}
