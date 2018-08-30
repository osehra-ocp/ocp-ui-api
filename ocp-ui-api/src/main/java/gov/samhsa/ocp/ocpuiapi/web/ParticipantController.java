package gov.samhsa.ocp.ocpuiapi.web;

import feign.FeignException;
import gov.samhsa.ocp.ocpuiapi.infrastructure.FisClient;
import gov.samhsa.ocp.ocpuiapi.infrastructure.OAuth2RestClient;
import gov.samhsa.ocp.ocpuiapi.infrastructure.dto.ManageUserDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.PageDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.ParticipantReferenceDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.ParticipantSearchDto;
import gov.samhsa.ocp.ocpuiapi.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
@RequestMapping("ocp-fis/participants")
public class ParticipantController {

    private final FisClient fisClient;
    private final OAuth2RestClient uaaClient;

    @Autowired
    public ParticipantController(FisClient fisClient, OAuth2RestClient uaaClient) {
        this.fisClient = fisClient;
        this.uaaClient = uaaClient;
    }

    @GetMapping("/search")
    public PageDto<ParticipantSearchDto> getAllParticipants(@RequestParam(value = "patientId") String patientId,
                                                            @RequestParam(value = "member") String member,
                                                            @RequestParam(value = "value", required = false) String value,
                                                            @RequestParam(value = "organization", required = false) String organization,
                                                            @RequestParam(value="participantForCareTeam", required=false) Boolean forCareTeam,
                                                            @RequestParam(value = "showInActive", defaultValue = "false") Boolean showInActive,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "size", required = false) Integer size,
                                                            @RequestParam(value = "showAll", required = false) Boolean showAll) {
        try {
            PageDto<ParticipantSearchDto> fhirParticipantPageDtos = fisClient.getAllParticipants(patientId, member, value, organization, forCareTeam, showInActive, page, size, showAll);

            if(member != null && member.equals("practitioner") && fhirParticipantPageDtos != null) {
                fhirParticipantPageDtos = updatePractitionersWithFhirRoles(fhirParticipantPageDtos);
            }

            return fhirParticipantPageDtos;
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no participants were found for the given parameters");
            return null;
        }
    }

    @GetMapping
    public List<ParticipantReferenceDto> getCareTeamParticipants(@RequestParam(value = "patient") String patient,
                                                                 @RequestParam(value = "roles", required = false) List<String> roles,
                                                                 @RequestParam(value="value", required=false) String name,
                                                                 @RequestParam(value = "communication", required = false) String communication) {
        try {
            return fisClient.getCareTeamParticipants(patient, roles,name, communication);
        }
        catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no participants were found for the given patient and the roles");
            return null;
        }
    }

    private PageDto<ParticipantSearchDto> updatePractitionersWithFhirRoles(PageDto<ParticipantSearchDto> fhirParticipantPageDtos) {

        List<ParticipantSearchDto> fhirParticipantList = fhirParticipantPageDtos.getElements();

        List<String> fhirIds = fhirParticipantList.stream().map(dto -> {
            return dto.getMember().getId();
        }).collect(toList());

        if (fhirIds != null && fhirIds.size() > 0) {
            Map<String, ManageUserDto> map = uaaClient.getUserRoles(fhirIds);

            if (map != null && !map.isEmpty()) {
                List<ParticipantSearchDto> updatedFhirParticipantList = fhirParticipantList.stream().map(dto -> {
                    ManageUserDto manageUserDto = map.get(dto.getMember().getId());

                    if (manageUserDto != null) {
                        dto.setUaaRole(manageUserDto.getDisplayName());
                        return dto;
                    }

                    return dto;
                }).collect(toList());

                fhirParticipantPageDtos.setElements(updatedFhirParticipantList);

                return fhirParticipantPageDtos;
            }
        }

        return fhirParticipantPageDtos;
    }

}

