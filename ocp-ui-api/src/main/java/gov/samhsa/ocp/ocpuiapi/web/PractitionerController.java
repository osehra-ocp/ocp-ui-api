package gov.samhsa.ocp.ocpuiapi.web;

import feign.FeignException;
import gov.samhsa.ocp.ocpuiapi.infrastructure.FisClient;
import gov.samhsa.ocp.ocpuiapi.service.dto.PageDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.PractitionerDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.ReferenceDto;
import gov.samhsa.ocp.ocpuiapi.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("ocp-fis")
public class PractitionerController {

    @Autowired
    public PractitionerController(FisClient fisClient) {
        this.fisClient = fisClient;
    }

    public enum SearchType {
        identifier, name
    }

    private final FisClient fisClient;

    @GetMapping("/practitioners/search")
    public PageDto<PractitionerDto> searchPractitioners(@RequestParam(value = "searchType", required = false) SearchType searchType,
                                                        @RequestParam(value = "searchValue", required = false) String searchValue,
                                                        @RequestParam(value = "organization", required = false) String organization,
                                                        @RequestParam(value = "showInactive", required = false) Boolean showInactive,
                                                        @RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "size", required = false) Integer size,
                                                        @RequestParam(value = "showAll", required = false) Boolean showAll) {
        log.info("Searching practitioners from FHIR server");
        try {
            PageDto<PractitionerDto> practitioners = fisClient.searchPractitioners(searchType, searchValue, organization, showInactive, page, size, showAll);
            log.info("Got response from FHIR server for practitioner search");
            return practitioners;
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no practitioners were found in the configured FHIR server for the given searchType and searchValue");
            return null;
        }
    }

    @PostMapping("/practitioners")
    @ResponseStatus(HttpStatus.CREATED)
    void createPractitioner(@Valid @RequestBody PractitionerDto practitionerDto) {
        try {

            fisClient.createPractitioner(practitionerDto);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that the practitioner was not created");
        }
    }

    @PutMapping("/practitioners/{practitionerId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePractitioner(@PathVariable String practitionerId, @Valid @RequestBody PractitionerDto practitionerDto) {
        try {
            fisClient.updatePractitioner(practitionerId, practitionerDto);

        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that the practitioner was not updated");
        }
    }

    @GetMapping("/practitioners/{practitionerId}")
    public PractitionerDto getPractitioner(@PathVariable String practitionerId) {
        try {
            return fisClient.getPractitioner(practitionerId);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no practitioner was found");
            return null;
        }
    }

    @GetMapping("/practitioners/practitioner-references")
    public List<ReferenceDto> getPractitionersInOrganizationByPractitionerId(@RequestParam(value = "practitioner", required = false) String practitioner,
                                                                             @RequestParam(value = "organization", required = false) String organization,
                                                                             @RequestParam(value = "role", required = false) String role) {
        try {
            return fisClient.getPractitionersInOrganizationByPractitionerId(practitioner, organization, role);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no practitioner was found in the organization for the given practitioner");
            return null;
        }
    }

    @GetMapping("/practitioners")
    public PageDto<PractitionerDto> getPractitionersByOrganizationAndRole(@RequestParam("organization") String organization,
                                                                          @RequestParam(value = "role", required = false) String role,
                                                                          @RequestParam(value = "page", required = false) Integer page,
                                                                          @RequestParam(value = "size", required = false) Integer size) {
        try {
            return fisClient.getPractitionersByOrganizationAndRole(organization, role, page, size);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that no practitioner was found for the given organization and the role (if provided) ");
            return null;
        }
    }

    @PutMapping(value = "/practitioners/{practitionerId}/assign")
    void assignLocationToPractitioner(@PathVariable("practitionerId") String practitionerId,
                                       @RequestParam(value = "organizationId") String organizationId,
                                       @RequestParam(value = "locationId") String locationId) {
        try {
            fisClient.assignLocationToPractitioner(practitionerId, organizationId, locationId);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that the location was not assigned");
        }
    }

    @PutMapping(value = "/practitioners/{practitionerId}/unassign")
    void unassignLocationToPractitioner(@PathVariable("practitionerId") String practitionerId,
                                         @RequestParam(value = "organizationId") String organizationId,
                                         @RequestParam(value = "locationId") String locationId) {
        try {
            fisClient.unassignLocationToPractitioner(practitionerId, organizationId, locationId);
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignException(fe, "that the location was not unassigned");
        }
    }
}
