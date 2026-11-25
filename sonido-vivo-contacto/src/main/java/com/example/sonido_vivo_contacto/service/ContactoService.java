package com.example.sonido_vivo_contacto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sonido_vivo_contacto.dto.ContactoRequest;
import com.example.sonido_vivo_contacto.dto.ContactoResponse;
import com.example.sonido_vivo_contacto.exception.RecursoNoEncontradoException;
import com.example.sonido_vivo_contacto.model.Contacto;
import com.example.sonido_vivo_contacto.repository.ContactoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoResponse crear(ContactoRequest request) {
        Contacto nuevo = Contacto.builder()
                .nombre(request.nombre().trim())
                .correo(request.correo().trim())
                .mensaje(request.mensaje().trim())
                .build();

        return aResponse(contactoRepository.save(nuevo));
    }

    public List<ContactoResponse> listar() {
        return contactoRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::aResponse)
                .toList();
    }

    public ContactoResponse marcarComoLeido(Long id) {
        Contacto contacto = contactoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje de contacto no encontrado: " + id));
        contacto.setLeido(true);
        return aResponse(contactoRepository.save(contacto));
    }

    public void eliminar(Long id) {
        Contacto contacto = contactoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mensaje de contacto no encontrado: " + id));
        contactoRepository.delete(contacto);
    }

    private ContactoResponse aResponse(Contacto c) {
        return new ContactoResponse(
                c.getId(),
                c.getNombre(),
                c.getCorreo(),
                c.getMensaje(),
                c.isLeido(),
                c.getFechaCreacion()
        );
    }
}