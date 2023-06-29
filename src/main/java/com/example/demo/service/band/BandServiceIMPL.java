package com.example.demo.service.band;

import com.example.demo.model.Band;
import com.example.demo.model.Song;
import com.example.demo.model.User;
import com.example.demo.repository.IBandRepository;
import com.example.demo.security.userprincal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BandServiceIMPL implements IBandService {
    @Autowired
    private IBandRepository bandRepository;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    public List<Band> findAll() {
        return bandRepository.findAll();
    }

    @Override
    public Optional<Band> findById(Long id) {
        return bandRepository.findById(id);
    }

    @Override
    public List<Band> findByName(String name) {
        return bandRepository.findByNameContaining(name);
    }

    @Override
    public Page<Band> findAll(Pageable pageable) {
        return bandRepository.findAll(pageable);
    }

    @Override
    public Boolean existsByName(String name) {
        return bandRepository.existsByName(name);
    }

    @Override
    public Band save(Band band) {
        User user = userDetailService.getCurrentUser();
        band.setUser(user);
        return bandRepository.save(band);
    }

    @Override
    public Band updateBand(Band band) {
        return bandRepository.save(band);
    }

    @Override
    public void deleteById(Long id) {
        bandRepository.deleteById(id);
    }

//    @Override
//    public List<Song> findSongsByBandId(Long id) {
//        return bandRepository.findSongsByBandId(id);
//    }
}
