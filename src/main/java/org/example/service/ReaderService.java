package org.example.service;

import org.example.entity.Reader;
import org.example.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    public Reader create(Reader reader) {
        reader.setEmail(reader.getEmail().trim().toLowerCase());
        if (readerRepository.existsByEmailIgnoreCase(reader.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Читач з таким email вже існує");
        }
        return readerRepository.save(reader);
    }

    public Reader getById(Long id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Читача не знайдено"));
    }
}
