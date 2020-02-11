package com.github.cosmoem.uiservice.handlingformsubmission;

import java.time.ZoneId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class FlugController {

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/flug")
    public String flugForm(Model model) {
        model.addAttribute("flug", new Flug());
        return "flug";
    }

    @PostMapping("/flug")
    public String flugSubmit(@ModelAttribute Flug flug) {
        try {
            Flug res = restTemplate.getForObject("http://localhost:8083/api/flug/{flugnummer}", Flug.class, flug.getFlugnummer());
            String status;
            try {
                status = restTemplate.getForObject("http://localhost:8989/api/status/{flugnummer}", String.class, flug.getFlugnummer());
            }
            catch (Exception e) {
                status = "CURRENTLY NOT AVAILABLE";
            }
            if(res != null && status != null) {
                flug.setAirline(res.getAirline());
                flug.setFlugdatum(res.getFlugdatum());
                flug.setNach(res.getNach());
                flug.setVon(res.getVon());
                flug.setUhrzeit(res.getUhrzeit());
                flug.setFlugstatus(status.replaceAll("^\"|\"$", ""));
                return "result";
            }
            else {
                return "failure";
            }
        } catch (Exception e) {
            return "error";
        }


    }

}