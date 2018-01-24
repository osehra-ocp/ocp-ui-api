package gov.samhsa.ocp.ocpuiapi.service;

import java.util.Arrays;

public enum LookUpTypeEnum {
    //In alphabetical order
    ADDRESSTYPE,
    ADDRESSUSE,
    IDENTIFIERSYSTEM,
    LOCATIONSTATUS,
    LOCATIONPHYSICALTYPE,
    TELECOMSYSTEM,
    TELECOMUSE,
    USPSSTATES,
    ADMINISTRATIVEGENDER,
    USCORERACE,
    USCOREETHNICITY,
    USCOREBIRTHSEX,
    LANGUAGE;

    public static boolean contains(String s) {
        return Arrays.stream(values()).anyMatch(key -> key.name().equalsIgnoreCase(s));
    }

}
