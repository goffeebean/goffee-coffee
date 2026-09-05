package com.goffeebean.mapper;

import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.entity.Roast;
import org.springframework.stereotype.Component;

@Component
public class RoastMapper {

    public Roast toEntity(RoastRequest request) {
        return new Roast(null, request.name(), request.origin(), request.roastLevel(), request.price(), request.tastingNotes());
    }

    public void updateEntity(Roast roast, RoastRequest request) {
        roast.setName(request.name());
        roast.setOrigin(request.origin());
        roast.setPrice(request.price());
        roast.setRoastLevel(request.roastLevel());
        roast.setTastingNotes(request.tastingNotes());
    }

    public RoastResponse toResponse(Roast roast) {
        return new RoastResponse(roast.getId(), roast.getName(), roast.getOrigin(), roast.getRoastLevel(), roast.getPrice(), roast.getTastingNotes());
    }
}
