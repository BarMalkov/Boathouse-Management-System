package ReservationPackage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReservationFilter")
@XmlEnum
public enum ReservationFilter {
    @XmlEnumValue("ByDay")
    ByDay("Show reservation by Day"),
    @XmlEnumValue("NextWeek")
    NextWeek("Show reservation of next week"),
    @XmlEnumValue("LastWeek")
    LastWeek("Show reservation of last week");

    private final String filterDescription;

    ReservationFilter(String filterDescription) {
        this.filterDescription = filterDescription;
    }

    public String getFilterDescription() {
        return filterDescription;
    }
}
