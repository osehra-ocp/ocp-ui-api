package gov.samhsa.ocp.ocpuiapi.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationHealthCareServiceDto extends NameLogicalIdIdentifiersDto {
    private String organizationId;
    private String organizationName;
    private String locationId;
    private String locationName;
    private boolean active;
    private String categorySystem;
    private String categoryValue;
    private ValueSetDto category;
    private List<String> programName;
    private List<TelecomDto> telecom;
    private List<ValueSetDto> type;
    private List<NameLogicalIdIdentifiersDto> location;
}
