package com.example.sonido_vivo_contacto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sonido_vivo_contacto.model.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    List<Contacto> findAllByOrderByFechaCreacionDesc();
}