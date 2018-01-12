package gov.samhsa.ocp.ocpuiapi.web;

import feign.FeignException;
import gov.samhsa.ocp.ocpuiapi.infrastructure.FisClient;
import gov.samhsa.ocp.ocpuiapi.service.dto.PageDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.PractitionerDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.ResourceType;
import gov.samhsa.ocp.ocpuiapi.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("ocp-fis")
public class PractitionerController {

    public enum SearchType {
        identifier, name
    }

    @Autowired
    private FisClient fisClient;

    /**
     * Example: http://localhost:8446/ocp-fis/practitioners/
     * http://localhost:8446/ocp-fis/practitioners?showInActive=true&page=1&size=10
     * @param showInactive
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/practitioners")
    public PageDto<PractitionerDto> getAllPractitioners(@RequestParam(value = "showInactive", required = false) boolean showInactive,
                                                        @RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "size", required = false) Integer size) {
        log.info("Fetching practitioners from FHIR server");
        try {
            PageDto<PractitionerDto> practitioners = fisClient.getAllPractitioners(showInactive, page, size);
            log.info("Got response from FHIR server for all practitioners");
            return practitioners;
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToSearch(fe, "no practitioners were found in the configured FHIR server", ResourceType.PRACTITIONER.name());
            return null;
        }
    }

    /**
     * Example: http://localhost:8446/ocp-fis/practitioners/search?searchType=name&searchValue=smith&showInactive=true&page=1&size=10
     * @param searchType
     * @param searchValue
     * @param showInactive
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/practitioners/search")
    public PageDto<PractitionerDto> searchPractitioners(@RequestParam(value = "searchType", required = false) SearchType searchType,
                                                     @RequestParam(value = "searchValue", required = false) String searchValue,
                                                     @RequestParam(value = "showInactive", required = false) Boolean showInactive,
                                                     @RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        log.info("Searching practitioners from FHIR server");
        try {
            PageDto<PractitionerDto> practitioners = fisClient.searchPractitioners(searchType, searchValue, showInactive, page, size);
            log.info("Got response from FHIR server for practitioner search");
            return practitioners;
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToSearch(fe, "no practitioners were found found in the configured FHIR server for the given searchType and searchValue",  ResourceType.PRACTITIONER.name());
            return null;
        }
    }

}