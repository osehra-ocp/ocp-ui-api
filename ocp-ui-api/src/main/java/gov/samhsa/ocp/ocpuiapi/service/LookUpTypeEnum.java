package gov.samhsa.ocp.ocpuiapi.service;

import java.util.Arrays;

public enum LookUpTypeEnum {
    //In alphabetical order
    ADDRESSTYPE,
    ADDRESSUSE,
    ADMINISTRATIVEGENDER,
    CARETEAMCATEGORY,
    CARETEAMSTATUS,
    LANGUAGE,
    LOCATIONIDENTIFIERSYSTEM,
    LOCATIONPHYSICALTYPE,
    LOCATIONSTATUS,
    LOCATIONTYPE,
    ORGANIZATIONIDENTIFIERSYSTEM,
    ORGANIZATIONSTATUS,
    PARTICIPANTROLE,
    PARTICIPANTTYPE,
    PATIENTIDENTIFIERSYSTEM,
    PRACTITIONERIDENTIFIERSYSTEM,
    PRACTITIONERROLES,
    TELECOMSYSTEM,
    TELECOMUSE,
    USCOREBIRTHSEX,
    USCOREETHNICITY,
    USCORERACE,
    USPSSTATES;

    public static boolean contains(String s) {
        return Arrays.stream(values()).anyMatch(key -> key.name().equalsIgnoreCase(s));
    }

}
