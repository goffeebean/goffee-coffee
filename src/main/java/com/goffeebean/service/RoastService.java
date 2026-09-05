package com.goffeebean.service;

import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.entity.Roast;
import com.goffeebean.exception.RoastNotFoundException;
import com.goffeebean.mapper.RoastMapper;
import com.goffeebean.repository.RoastRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoastService {

    private final RoastRepository roastRepository;
    private final RoastMapper roastMapper;

    public RoastService(RoastRepository roastRepository, RoastMapper roastMapper) {
        this.roastRepository = roastRepository;
        this.roastMapper = roastMapper;
    }

    public List<RoastResponse> findAll() {
        return roastRepository.findAll().stream()
                .map(roastMapper::toResponse)
                .toList();
    }

    public RoastResponse findById(Long id) {
        return roastRepository.findById(id).map(roastMapper::toResponse).orElseThrow(() -> new RoastNotFoundException(id));
    }

    public RoastResponse create( RoastRequest request){
        // Create entity and then return response
        Roast myRoast = roastMapper.toEntity(request);
        Roast savedRoast = roastRepository.save(myRoast);
        return roastMapper.toResponse(savedRoast);
    }

    public RoastResponse update(Long id, RoastRequest request){
        Roast myRoast = roastRepository.findById(id).orElseThrow(() -> new RoastNotFoundException(id));
        roastMapper.updateEntity(myRoast,request);
        Roast savedRoast = roastRepository.save(myRoast);
        return roastMapper.toResponse(savedRoast);
    }

    public void delete(Long id){
        if (!roastRepository.existsById(id)) {
            throw new RoastNotFoundException(id);
        }
        roastRepository.deleteById(id);
    }
}
