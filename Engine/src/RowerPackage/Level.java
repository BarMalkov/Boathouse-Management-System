package RowerPackage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Level")
@XmlEnum
public enum Level {
    @XmlEnumValue("Beginner")
    BEGINNER("Beginner"),
    @XmlEnumValue("Intermediate")
    INTERMEDIATE("Intermediate"),
    @XmlEnumValue("Expert")
    EXPERT("Expert");

    private final String levelDescription;

    Level(String level) {
        this.levelDescription = level;
    }

    public String getLevelDescription() {
        return levelDescription;
    }
}
