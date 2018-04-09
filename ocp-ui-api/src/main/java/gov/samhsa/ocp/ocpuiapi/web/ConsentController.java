package gov.samhsa.ocp.ocpuiapi.web;

import feign.FeignException;
import gov.samhsa.ocp.ocpuiapi.infrastructure.FisClient;
import gov.samhsa.ocp.ocpuiapi.service.dto.ConsentDto;
import gov.samhsa.ocp.ocpuiapi.service.dto.GeneralConsentRelatedFieldDto;
import gov.samhsa.ocp.ocpuiapi.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("ocp-fis")
@Slf4j
public class ConsentController {
    @Autowired
    FisClient fisClient;

    @GetMapping("/consents")
    public Object getConsents(@RequestParam(value = "patient", required = false) String patient,
                              @RequestParam(value = "practitioner", required = false) String practitioner,
                              @RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "generalDesignation", required = false) Boolean generalDesignation,
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        log.info("Searching Consents from FHIR server");
        try {
            Object consents = fisClient.getConsents(patient, practitioner, status, generalDesignation, pageNumber, pageSize);
            log.info("Got Response from FHIR server for Consents Search");
            return consents;
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToSearch(fe, "No Consents were found in configured FHIR server");
            return null;
        }
    }

    @GetMapping("/consents/{consentId}")
    public Object getConsentById(@PathVariable String consentId) {
        log.info("Fetching consent from FHIR Server for the given consentId: " + consentId);
        try {
            Object fisClientResponse = fisClient.getConsentById(consentId);
            log.info("Got response from FHIR Server...");
            return fisClientResponse;
        }
        catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToSearch(fe, "the consent was not found");
            return null;
        }
    }

    @PostMapping("/consents")
    @ResponseStatus(HttpStatus.CREATED)
    public void createConsent(@Valid @RequestBody ConsentDto consentDto) {
        log.info("About to create a consent");
        try {
            fisClient.createConsent(consentDto);
            log.info("Successfully created a consent.");
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToResourceCreate(fe, " that the consent was not created");
        }
    }

    @PutMapping("/consents/{consent}")
    @ResponseStatus(HttpStatus.OK)
    public void updateConsent(@PathVariable String consent, @Valid @RequestBody ConsentDto consentDto) {
        try {
            fisClient.updateConsent(consent, consentDto);
            log.debug("Successfully updated a consent");
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToResourceUpdate(fe, "Consent could not be updated in the FHIR server");
        }
    }

    @PutMapping("/consents/{consentId}/attestation")
    @ResponseStatus(HttpStatus.OK)
    public void attestConsent(@PathVariable String consentId) {
        try {
            fisClient.attestConsent(consentId);
            log.debug("Successfully active a consent");
        } catch (FeignException fe) {
            ExceptionUtil.handleFeignExceptionRelatedToResourceUpdate(fe, "Consent could not be activated in the FHIR server");
        }
    }

    @GetMapping("/generalConsent/{patient}")
    public GeneralConsentRelatedFieldDto getRelatedFieldForGeneralConsent(@PathVariable String patient){
       try{
           return fisClient.getRelatedFieldForGeneralConsent(patient);
       }catch (FeignException fe){
           ExceptionUtil.handleFeignExceptionRelatedToSearch(fe, "the consent was not found");
           return null;
       }
    }
}