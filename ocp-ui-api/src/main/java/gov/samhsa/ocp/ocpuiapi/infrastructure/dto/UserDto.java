package gov.samhsa.ocp.ocpuiapi.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDto {

    @JsonProperty("id")
    String id;

    @JsonProperty("givenName")
    String givenName;

    @JsonProperty("familyName")
    String familyName;

    @JsonProperty("displayName")
    String displayName;

    @JsonProperty("description")
    String description;

    @JsonProperty("info")
    String info;

    @JsonProperty("role")
    String role;

    @JsonIgnore
    public UserDto(String id, String givenName, String familyName, String displayName, String description, String info, String role) {
        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.displayName = displayName;
        this.description = description;
        this.info = info;
        this.role = role;
    }

    //dummy constructor needed for "json parse error"
    public UserDto() {

    }

}
