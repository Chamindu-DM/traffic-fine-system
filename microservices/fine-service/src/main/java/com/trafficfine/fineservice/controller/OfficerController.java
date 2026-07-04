package com.trafficfine.fineservice.controller;

import com.trafficfine.fineservice.entity.Officer;
import com.trafficfine.fineservice.repository.OfficerRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/officers")
public class OfficerController {

    private final OfficerRepository officerRepository;

    public OfficerController(OfficerRepository officerRepository) {
        this.officerRepository = officerRepository;
    }

    @GetMapping
    public List<Officer> getOfficers() {
        return officerRepository.findAll();
    }
}
