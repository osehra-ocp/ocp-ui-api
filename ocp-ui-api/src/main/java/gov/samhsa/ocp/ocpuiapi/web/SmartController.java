package gov.samhsa.ocp.ocpuiapi.web;

import gov.samhsa.ocp.ocpuiapi.infrastructure.dto.LaunchRequestDto;
import gov.samhsa.ocp.ocpuiapi.infrastructure.dto.LaunchResponseDto;
import gov.samhsa.ocp.ocpuiapi.service.SmartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/smart")
public class SmartController {

    @Autowired
    private SmartService smartService;

    @PostMapping("/launch/{launchId}")
    public LaunchResponseDto mergeAndSave(@PathVariable("launchId") String launchId,
                                          @Valid @RequestBody LaunchRequestDto launchRequest) {
        return smartService.mergeAndSave(launchId, launchRequest);
    }
}