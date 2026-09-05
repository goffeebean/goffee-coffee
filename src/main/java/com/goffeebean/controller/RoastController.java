package com.goffeebean.controller;

import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.dto.TastingNoteResponse;
import com.goffeebean.service.RoastService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.goffeebean.service.OllamaTastingNoteService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roasts")
public class RoastController {
    private final RoastService roastService;
    private final OllamaTastingNoteService ollamaTastingNoteService;

    public RoastController(RoastService roastService, OllamaTastingNoteService ollamaTastingNoteService){
        this.roastService = roastService;
        this.ollamaTastingNoteService = ollamaTastingNoteService;
    }

    @GetMapping
    public List<RoastResponse> getListOfRoasts(){
        return roastService.findAll();
    }

    @GetMapping("/{id}")
    public RoastResponse getRoastById(@PathVariable Long id){
        return roastService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RoastResponse> createRoast(@Valid @RequestBody
                                                     RoastRequest request) {
        RoastResponse response = roastService.create(request);
        URI location = URI.create("/api/v1/roasts/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/{id}/tasting-notes/generate")
    public ResponseEntity<TastingNoteResponse> createTastingNotes(@PathVariable Long id) {
        TastingNoteResponse response = ollamaTastingNoteService.generateTastingNotes(id);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public RoastResponse updateRoastById(@PathVariable Long id, @Valid @RequestBody RoastRequest request){
        return roastService.update(id,request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoastById(@PathVariable Long id){
        roastService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
