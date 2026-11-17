package com.veranum.corepago.repository;

import com.veranum.corepago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByReservaId(Long reservaId);

    List<Pago> findAllByReservaId(Long reservaId);

    boolean existsByReservaId(Long reservaId);
}